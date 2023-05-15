package io.cardell.ff4s.mtl

import weaver._
import io.cardell.ff4s.FlagEnv
import cats.implicits._
import io.cardell.ff4s.FlagKey
import io.cardell.ff4s.FlagState
import io.cardell.ff4s.mtl._
import cats.Id
import io.cardell.ff4s.FlagDisabled
import cats.Monad
import cats.data.Kleisli
import cats.mtl.Ask
import cats.data.EitherT

object MtlSuite extends SimpleIOSuite {

  val Key = "flag-key"

  def EnabledEnv[F[_]: Monad]: FlagEnv[F] =
    Kleisli { (key: FlagKey) =>
      key match {
        case Key => Monad[F].pure(FlagState.Enabled)
        case _   => Monad[F].pure(FlagState.Disabled)
      }
    }

  def DisabledEnv[F[_]: Monad]: FlagEnv[F] =
    Kleisli { (_: FlagKey) =>
      Monad[F].pure(FlagState.Disabled)
    }

  pureTest("mtl flag[F] reads existing enabled key") {
    val flags = Map(Key -> FlagState.Enabled)
    val pureFlagEnv = (key: FlagKey) => flags.getOrElse(key, FlagState.Disabled)

    type E[A] = Kleisli[EitherT[Id, Throwable, *], PureFlagEnv, A]

    def program[F[_]: Monad](implicit ask: Ask[F, PureFlagEnv]): F[Int] = for {
      flagState <- flag[F](Key)
      result <-
        if (flagState == FlagState.Enabled) Monad[F].pure(1)
        else Monad[F].pure(2)
    } yield result

    val result: Id[Either[Throwable, Int]] = program[E].run(pureFlagEnv).value

    expect(result == Right(1))
  }

}
