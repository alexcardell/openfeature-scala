package io.cardell.ff4s.flipt.model

import io.circe.generic.semiauto.deriveDecoder
import io.circe.Decoder

case class BatchEvaluationResponse(
    responses: List[EvaluationResponse]
)

object BatchEvaluationResponse {
  implicit val d: Decoder[BatchEvaluationResponse] = deriveDecoder
}
