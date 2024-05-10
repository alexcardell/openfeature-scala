package io.cardell.openfeature.provider

import io.cardell.openfeature.EvaluationReason
import io.cardell.openfeature.ErrorCode

sealed trait FlagMetadataValue

object FlagMetadataValue {
  case class Boolean(value: Boolean) extends FlagMetadataValue
  case class String(value: String) extends FlagMetadataValue
  case class Int(value: String) extends FlagMetadataValue
  case class Double(value: Double) extends FlagMetadataValue
  // TODO circe unwrapped codecs
}

case class ResolutionDetails[A](
    value: A,
    errorCode: Option[ErrorCode],
    errorMessage: Option[String],
    reason: Option[EvaluationReason],
    variant: Option[String],
    metadata: Option[FlagMetadata]
)
