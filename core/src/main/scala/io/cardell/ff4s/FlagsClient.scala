package io.cardell.ff4s

trait FlagsClient[F[_]] {

  def setup(): F[Flags[F]]

}
