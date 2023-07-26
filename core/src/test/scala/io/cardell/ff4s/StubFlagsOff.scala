package io.cardell.ff4s

import cats.Applicative

class StubFlagsOff[F[_]: Applicative] extends Flags[F] {
  def get(key: String): F[FlagCase] =
    Applicative[F].pure(FlagCase.Off)
}
