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

import cats.Monad

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureCodec

class StubEvaluationProvider[F[_]: Monad]() extends EvaluationProvider[F] {

  override def metadata: ProviderMetadata = ProviderMetadata(name =
    "stub-provider"
  )

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = resolve(defaultValue)

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = resolve(defaultValue)

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = resolve(defaultValue)

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = resolve(defaultValue)

  override def resolveStructureValue[A: StructureCodec](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = resolve(defaultValue)

  private def resolve[A](value: A) = Monad[F].pure(
    ResolutionDetails[A](
      value = value,
      errorCode = None,
      errorMessage = None,
      reason = None,
      variant = None,
      metadata = None
    )
  )

}
