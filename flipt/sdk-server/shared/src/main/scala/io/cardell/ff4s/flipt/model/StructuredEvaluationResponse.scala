package io.cardell.ff4s.flipt.model

import cats.syntax.all.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.Decoder
import io.circe.Json

case class StructuredVariantEvaluationResponse[A](
    `match`: Boolean,
    segmentKeys: List[String],
    reason: EvaluationReason,
    flagKey: String,
    variantKey: String,
    variantAttachment: Option[A],
    requestDurationMillis: Float,
    timestamp: String
)

object StructuredVariantEvaluationResponse {
  def apply[A: Decoder](
      variant: VariantEvaluationResponse
  ): Decoder.Result[StructuredVariantEvaluationResponse[A]] = {
    val json = io.circe.parser.parse(variant.variantAttachment)
    println(json)
    json.flatMap(_.as[A]).leftMap(_ => ???).map { attachment =>
      StructuredVariantEvaluationResponse[A](
        `match` = variant.`match`,
        segmentKeys = variant.segmentKeys,
        reason = variant.reason,
        flagKey = variant.flagKey,
        variantKey = variant.variantKey,
        variantAttachment = Some(attachment),
        requestDurationMillis = variant.requestDurationMillis,
        timestamp = variant.timestamp
      )
    }
  }
}
