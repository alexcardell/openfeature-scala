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

package io.cardell.ff4s.flipt.model

import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import scala.annotation.unused

sealed trait EvaluationResponse[A]

object EvaluationResponse {
  implicit def d[A](implicit a: Decoder[A]): Decoder[EvaluationResponse[A]] =
    List[Decoder[EvaluationResponse[A]]](
      Decoder[BooleanEvaluationResponse[A]].widen,
      Decoder[VariantEvaluationResponse[A]].widen,
      Decoder[ErrorEvaluationResponse[A]].widen
    ).reduceLeft(_ or _)
}

case class BooleanEvaluationResponse[A](
    enabled: Boolean,
    flagKey: String,
    reason: EvaluationReason,
    requestDurationMillis: Double,
    timestamp: String
) extends EvaluationResponse[A]

object BooleanEvaluationResponse {
  implicit def decoder[A](implicit
      @unused a: Decoder[A]
  ): Decoder[BooleanEvaluationResponse[A]] = deriveDecoder
}

case class VariantEvaluationResponse[A](
    `match`: Boolean,
    segmentKeys: List[String],
    reason: EvaluationReason,
    flagKey: String,
    variantKey: String,
    variantAttachment: String,
    requestDurationMillis: Float,
    timestamp: String
) extends EvaluationResponse[A]

object VariantEvaluationResponse {
  implicit def d[A](implicit
      @unused da: Decoder[A]
  ): Decoder[VariantEvaluationResponse[A]] = deriveDecoder
}

case class ErrorEvaluationResponse[A](
    flagKey: String,
    namespaceKey: String,
    reason: ErrorEvaluationReason
) extends EvaluationResponse[A]

object ErrorEvaluationResponse {
  implicit def decoder[A](implicit
      @unused a: Decoder[A]
  ): Decoder[ErrorEvaluationResponse[A]] = deriveDecoder
}
