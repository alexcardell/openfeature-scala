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

package io.cardell.ff4s.flipt

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class EvaluationRequest(
    namespaceKey: String,
    flagKey: String,
    entityId: Option[String],
    context: Map[String, String],
    reference: Option[String]
)

object EvaluationRequest {
  implicit val d: Encoder[EvaluationRequest] = deriveEncoder

  def apply(
      namespaceKey: String,
      flagKey: String,
      entityId: Option[String] = None,
      context: Map[String, String] = Map.empty,
      reference: Option[String] = None
  ): EvaluationRequest = EvaluationRequest(
    namespaceKey,
    flagKey,
    entityId,
    context,
    reference
  )

}
