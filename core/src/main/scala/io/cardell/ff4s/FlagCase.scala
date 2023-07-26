package io.cardell.ff4s

case class Variant(key: Key)

// TODO better name
sealed trait FlagCase

object FlagCase {
  case object Off extends FlagCase
  case class On(variant: Option[Variant]) extends FlagCase

  object On {
    def apply(): FlagCase = On(None)
  }
}
