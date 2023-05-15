package io.cardell

import cats.Applicative
import cats.data.Kleisli
import cats.syntax.applicative._

package object ff4s {

  type FlagKey = String

  sealed trait FlagState
  object FlagState {
    case object Enabled extends FlagState
    case object Disabled extends FlagState
  }

  case class FlagDisabled(key: FlagKey)
  case class FlagNotFound(key: FlagKey)

  type FlagEnv[F[_]] = Kleisli[F, FlagKey, FlagState]

  object FlagEnv {
    def fromMap[F[_]: Applicative](
        envMap: Map[FlagKey, FlagState]
    ): FlagEnv[F] = Kleisli { key =>
      envMap.getOrElse(key, FlagState.Disabled).pure[F]
    }
  }

}
