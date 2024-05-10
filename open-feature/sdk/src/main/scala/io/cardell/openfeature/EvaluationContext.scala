package io.cardell.openfeature
import io.cardell.openfeature.ContextValue.BooleanValue
import io.cardell.openfeature.ContextValue.StringValue
import io.cardell.openfeature.ContextValue.IntValue
import io.cardell.openfeature.ContextValue.DoubleValue

sealed trait ContextValue {
  def stringValue: String = this match {
    case StringValue(value)  => value
    case BooleanValue(value) => value.toString()
    case IntValue(value)     => value.toString()
    case DoubleValue(value)  => value.toString()
  }
}

object ContextValue {
  case class BooleanValue(value: Boolean) extends ContextValue
  case class StringValue(value: String) extends ContextValue
  case class IntValue(value: Int) extends ContextValue
  case class DoubleValue(value: Double) extends ContextValue
}

case class EvaluationContext(
    targetingKey: Option[String],
    values: Map[String, ContextValue]
) {
  def ++(other: EvaluationContext): EvaluationContext =
    EvaluationContext(
      other.targetingKey.orElse(targetingKey),
      // TODO check override order in spec
      values ++ other.values
    )
}

object EvaluationContext {
  def empty: EvaluationContext = EvaluationContext(None, Map.empty)
}
