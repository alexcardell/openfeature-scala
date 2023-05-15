package io.cardell.ff4s

import weaver._
import io.cardell.ff4s.FlagEnv
import io.cardell.ff4s.FlagKey
import io.cardell.ff4s.FlagState
import io.cardell.ff4s.syntax._
import cats.Id
import io.cardell.ff4s.FlagDisabled
import cats.Monad
import cats.data.Kleisli

object FlaggedSyntaxSuite extends SimpleIOSuite {

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

  pureTest("flag[F] reads existing enabled key") {
    implicit val flagEnv = EnabledEnv[Id]

    val result = flag[Id](Key)

    expect(result == FlagState.Enabled)
  }

  pureTest("flag[F] reads existing disabled key") {
    implicit val flagEnv = DisabledEnv[Id]

    val result = flag[Id](Key)

    expect(result == FlagState.Disabled)
  }

  pureTest("flagged[F] with fallback runs enabled effect") {
    implicit val flagEnv = EnabledEnv[Id]

    val result = flagged[Id, Int](Key, Id(1), Id(2))

    expect(result == 1)
  }

  pureTest("flagged[F] with fallback runs disabled effect") {
    implicit val flagEnv = DisabledEnv[Id]

    val result = flagged[Id, Int](Key, Id(1), Id(2))

    expect(result == 2)
  }

  pureTest("flagged[F] with no fallback returns right of enabled effect") {
    implicit val flagEnv = EnabledEnv[Id]

    val result = flagged[Id, Int](Key, Id(1))

    expect(result == Right(1))
  }

  pureTest(
    "flagged[F] with no fallback returns left of exception for disabled flag"
  ) {
    implicit val flagEnv = DisabledEnv[Id]

    val result = flagged[Id, Int](Key, Id(1))

    expect(result == Left(FlagDisabled(Key)))
  }

}
