package io.cardell.ff4s

import cats.Monad
import cats.implicits._
import cats.mtl.Ask

import io.cardell.ff4s.FlagKey
import io.cardell.ff4s.FlagState

package object mtl {

  type PureFlagEnv = FlagKey => FlagState

  def flag[F[_]: Monad](key: FlagKey)(implicit
      fask: Ask[F, PureFlagEnv]
  ): F[FlagState] =
    fask.ask.map(_.apply(key))

  // def flagged[F[_]: Monad, A](
  //     key: FlagKey,
  //     ifEnabled: F[A],
  //     ifDisabled: F[A]
  // )(implicit fask: Ask[F, PureFlagEnv]): F[A] =
  //   fask.ask.flatMap(implicit env =>
  //     envFlagged.flagged(key, ifEnabled, ifDisabled)
  //   )

  // def flagged[F[_]: Monad, A](
  //     key: FlagKey,
  //     ifEnabled: F[A]
  // )(implicit fask: Ask[F, FlagEnv[F]]): F[Either[FlagDisabled, A]] =
  //   fask.ask.flatMap(implicit env => envFlagged.flagged(key, ifEnabled))

}
