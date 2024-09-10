/*
 * Copyright 2023 Alex Cardell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cardell.flipt.model

import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

sealed trait EvaluationResponse

object EvaluationResponse {

  implicit def decoder: Decoder[EvaluationResponse] =
    List[Decoder[EvaluationResponse]](
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
  implicit def decoder: Decoder[BooleanEvaluationResponse] = deriveDecoder
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
  implicit def d: Decoder[VariantEvaluationResponse] = deriveDecoder
}

case class ErrorEvaluationResponse(
    flagKey: String,
    namespaceKey: String,
    reason: ErrorEvaluationReason
) extends EvaluationResponse

object ErrorEvaluationResponse {
  implicit def decoder: Decoder[ErrorEvaluationResponse] = deriveDecoder
}
