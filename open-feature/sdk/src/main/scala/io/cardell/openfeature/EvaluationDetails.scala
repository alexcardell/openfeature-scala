package io.cardell.openfeature

import io.cardell.openfeature.provider.FlagMetadata
import io.cardell.openfeature.provider.ResolutionDetails

case class EvaluationDetails[A](
    flagKey: String,
    value: A,
    errorCode: Option[ErrorCode],
    errorMessage: Option[String],
    reason: Option[EvaluationReason],
    variant: Option[String],
    metadata: Option[FlagMetadata]
)

object EvaluationDetails {

  def apply[A](
      flagKey: String,
      resolution: ResolutionDetails[A]
  ): EvaluationDetails[A] =
    EvaluationDetails[A](
      flagKey = flagKey,
      value = resolution.value,
      errorCode = resolution.errorCode,
      errorMessage = errorMessage.errorMessage,
      reason = resolution.reason,
      variant = resolution.variant,
      metadata = resolution.metadata
    )
}
