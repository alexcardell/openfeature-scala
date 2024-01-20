package io.cardell.ff4s.flipt.model

import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

sealed trait EvaluationResponse

object EvaluationResponse {
  implicit val d: Decoder[EvaluationResponse] = List[Decoder[EvaluationResponse]](
    Decoder[BooleanEvaluationResponse].widen,
    Decoder[VariantEvaluationResponse].widen,
    Decoder[ErrorEvaluationResponse].widen
  ).reduceLeft(_ or _)
}

case class BooleanEvaluationResponse(
    enabled: Boolean,
    flagKey: String,
    reason: EvaluationReason,
    requestDurationMillis: Double,
    timestamp: String
) extends EvaluationResponse

object BooleanEvaluationResponse {
  implicit val d: Decoder[BooleanEvaluationResponse] = deriveDecoder
}

case class VariantEvaluationResponse(
    `match`: Boolean,
    segmentKeys: List[String],
    reason: EvaluationReason,
    flagKey: String,
    variantKey: String,
    variantAttachment: String,
    requestDurationMillis: Float,
    timestamp: String
) extends EvaluationResponse

object VariantEvaluationResponse {
  implicit val d: Decoder[VariantEvaluationResponse] = deriveDecoder
}

case class ErrorEvaluationResponse(
    flagKey: String,
    namespaceKey: String,
    reason: ErrorEvaluationReason
) extends EvaluationResponse

object ErrorEvaluationResponse {
  implicit val d: Decoder[ErrorEvaluationResponse] = deriveDecoder
}
