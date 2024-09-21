package io.cardell.openfeature.otel4s

import cats.data.OptionT
import cats.effect.IO
import cats.effect.IOLocal
import cats.syntax.all._
import org.typelevel.otel4s.trace.Span
import org.typelevel.otel4s.trace.StatusCode
import org.typelevel.otel4s.trace.Tracer

import io.cardell.openfeature.AfterHook
import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.ErrorHook
import io.cardell.openfeature.FinallyHook
import io.cardell.openfeature.Hook

object TraceHooks {

  def ioLocal(implicit T: Tracer[IO]): IO[List[Hook[IO]]] = IOLocal(
    Option.empty[Span[IO]]
  ).map(fromIOLocal)

  private def fromIOLocal(
      local: IOLocal[Option[Span[IO]]]
  )(implicit T: Tracer[IO]): List[Hook[IO]] = {
    val before = BeforeHook[IO] { case _ =>
      Tracer[IO]
        .span("resolve-flag")
        .startUnmanaged
        .flatMap(s => local.update(_ => s.some))
        .as(None)
    }

    val after = AfterHook[IO] { case _ =>
      OptionT(local.get)
        .semiflatMap { span =>
          for {
            _ <- span.setStatus(StatusCode.Ok)
            _ <- span.end
          } yield ()
        }
        .value
        .void
    }

    val error = ErrorHook[IO] { case (_, _, error) =>
      OptionT(local.get)
        .semiflatMap { span =>
          for {
            _ <- span.setStatus(StatusCode.Error)
            _ <- span.recordException(error)
          } yield ()
        }
        .value
        .void
    }

    val finally_ = FinallyHook[IO] { case _ =>
      OptionT(local.get)
        .semiflatMap(_.end)
        .value
        .void
    }

    List(before, after, error, finally_)
  }

}
