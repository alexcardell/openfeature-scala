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

import cats.syntax.all._
import io.circe.Decoder
import io.circe.parser

sealed trait AttachmentDecodingError

object AttachmentDecodingError {
  case object AttachmentJsonParsingError     extends AttachmentDecodingError
  case object AttachmentDeserialisationError extends AttachmentDecodingError
}

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
  ): Either[AttachmentDecodingError, StructuredVariantEvaluationResponse[A]] = {
    val maybeAttachment =
      if (variant.`match`) {
        decodeJsonAttachment(variant.variantAttachment).map(Some(_))
      } else {
        Option.empty[A].asRight[AttachmentDecodingError]
      }

    maybeAttachment.map { attachment =>
      StructuredVariantEvaluationResponse[A](
        `match` = variant.`match`,
        segmentKeys = variant.segmentKeys,
        reason = variant.reason,
        flagKey = variant.flagKey,
        variantKey = variant.variantKey,
        variantAttachment = attachment,
        requestDurationMillis = variant.requestDurationMillis,
        timestamp = variant.timestamp
      )
    }
  }

  private def decodeJsonAttachment[A: Decoder](
      string: String
  ): Either[AttachmentDecodingError, A] = {
    import AttachmentDecodingError.*
    for {
      json <- parser
        .parse(string)
        .leftMap(_ => AttachmentJsonParsingError)
      attachment <- json
        .as[A]
        .leftMap(_ => AttachmentDeserialisationError)
    } yield attachment
  }

}
