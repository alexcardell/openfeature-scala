package io.cardell.openfeature

import io.cardell.openfeature.FlagValue.BooleanValue
import io.cardell.openfeature.FlagValue.DoubleValue
import io.cardell.openfeature.FlagValue.IntValue
import io.cardell.openfeature.FlagValue.StringValue
import io.cardell.openfeature.FlagValue.StructureValue

trait HasFlagValue[A] {
  def toFlagValue(a: A): FlagValue
}

object HasFlagValue {
  def apply[A](implicit h: HasFlagValue[A]): HasFlagValue[A] = implicitly

  implicit val boolean: HasFlagValue[Boolean] = BooleanValue.apply _
  // implicit val boolean: HasFlagValue[Boolean] = (BooleanValue.apply _)

}
