package io.cardell.ff4s.syntax

import cats.Monad
import cats.implicits._

import io.cardell.ff4s.FlagDisabled
import io.cardell.ff4s.FlagEnv
import io.cardell.ff4s.FlagKey
import io.cardell.ff4s.FlagState

trait FlaggedSyntax {

  def flag[F[_]](key: FlagKey)(implicit
      flagEnv: FlagEnv[F]
  ): F[FlagState] =
    flagEnv.run(key)

  def flagged[F[_]: Monad, A](
      key: FlagKey,
      ifEnabled: F[A],
      ifDisabled: F[A]
  )(implicit flagEnv: FlagEnv[F]): F[A] =
    flag[F](key).flatMap {
      case FlagState.Enabled  => ifEnabled
      case FlagState.Disabled => ifDisabled
    }

  def flagged[F[_]: Monad, A](
      key: FlagKey,
      ifEnabled: F[A]
  )(implicit flagEnv: FlagEnv[F]): F[Either[FlagDisabled, A]] =
    flag[F](key).flatMap {
      case FlagState.Enabled  => ifEnabled.map(Right(_))
      case FlagState.Disabled => FlagDisabled(key).pure[F].map(Left(_))
    }

}
