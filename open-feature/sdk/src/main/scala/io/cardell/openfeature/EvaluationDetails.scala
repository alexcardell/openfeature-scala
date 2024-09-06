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
  ): EvaluationDetails[A] = EvaluationDetails[A](
    flagKey = flagKey,
    value = resolution.value,
    errorCode = resolution.errorCode,
    errorMessage = resolution.errorMessage,
    reason = resolution.reason,
    variant = resolution.variant,
    metadata = resolution.metadata
  )

}
