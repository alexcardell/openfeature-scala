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

package io.cardell.openfeature.log4cats

import cats.MonadThrow

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.StructureCodec
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

class ThrowingEvaluationProvider[F[_]: MonadThrow]()
    extends EvaluationProvider[F] {

  override def metadata: ProviderMetadata = ProviderMetadata(name =
    "throwing-provider"
  )

  val err = new Throwable("Evaluation Error! This provider always throws")

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = MonadThrow[F].raiseError(err)

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = MonadThrow[F].raiseError(err)

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = MonadThrow[F].raiseError(err)

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = MonadThrow[F].raiseError(err)

  override def resolveStructureValue[A: StructureCodec](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = MonadThrow[F].raiseError(err)

}
