/*
 * Copyright 2023 Alex Cardell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cardell.openfeature.otel4s

import cats.data.OptionT
import cats.effect.IO
import cats.effect.IOLocal
import cats.syntax.all._
import org.typelevel.otel4s.Attributes
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
    import FeatureFlagAttributes._

    val before = BeforeHook[IO] { case (context, _) =>
      val attributes = Attributes(
        FeatureFlagKey(context.flagKey)
      )

      Tracer[IO]
        .span("resolve-flag", attributes)
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
