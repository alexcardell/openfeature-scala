package io.cardell.ff4s

trait Flags[F[_]] {

  def get(key: Key): F[FlagCase]

}
