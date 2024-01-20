package io.cardell.ff4s.flipt

import io.circe.generic.semiauto.deriveEncoder
import io.circe.Encoder

case class EvaluationRequest(
    namespaceKey: String,
    flagKey: String,
    entityId: Option[String],
    context: Map[String, String],
    reference: Option[String]
)

object EvaluationRequest {
  implicit val d: Encoder[EvaluationRequest] = deriveEncoder
}
