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

package io.cardell.openfeature.provider

import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.EvaluationReason

sealed trait FlagMetadataValue

object FlagMetadataValue {
  case class BooleanValue(value: Boolean) extends FlagMetadataValue
  case class StringValue(value: String)   extends FlagMetadataValue
  case class IntValue(value: String)      extends FlagMetadataValue
  case class DoubleValue(value: Double)   extends FlagMetadataValue
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

object ResolutionDetails {

  def error[A](defaultValue: A, error: Throwable): ResolutionDetails[A] =
    ResolutionDetails[A](
      value = defaultValue,
      errorCode = Some(ErrorCode.General),
      errorMessage = Some(error.getMessage()),
      reason = Some(EvaluationReason.Error),
      variant = None,
      metadata = None
    )

}
