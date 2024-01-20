package io.cardell.ff4s.flipt.model

import io.cardell.ff4s.flipt.EvaluationRequest
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Encoder

case class BatchEvaluationRequest(
    requestId: Option[String],
    requests: List[EvaluationRequest],
    reference: Option[String]
)

object BatchEvaluationRequest {
  implicit val d: Encoder[BatchEvaluationRequest] = deriveEncoder
}
