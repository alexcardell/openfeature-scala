package io.cardell.openfeature

sealed trait ContextValue

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
