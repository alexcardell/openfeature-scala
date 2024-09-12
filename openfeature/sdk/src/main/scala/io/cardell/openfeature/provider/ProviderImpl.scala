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

import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.Hook
import io.cardell.openfeature.StructureDecoder

protected class ProviderImpl[F[_]](
    evaluationProvider: EvaluationProvider[F],
    val beforeHooks: List[BeforeHook[F]],
) extends Provider[F] {

  override def metadata: ProviderMetadata = evaluationProvider.metadata

  override def withHook(hook: Hook[F]): Provider[F] =
    hook match {
      case bh: BeforeHook[F] =>
        new ProviderImpl[F](evaluationProvider, beforeHooks.appended(bh))

    }

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = evaluationProvider.resolveBooleanValue(
    flagKey = flagKey,
    defaultValue = defaultValue,
    context = context
  )

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = evaluationProvider.resolveStringValue(
    flagKey = flagKey,
    defaultValue = defaultValue,
    context = context
  )

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = evaluationProvider.resolveIntValue(
    flagKey = flagKey,
    defaultValue = defaultValue,
    context = context
  )

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = evaluationProvider.resolveDoubleValue(
    flagKey = flagKey,
    defaultValue = defaultValue,
    context = context
  )

  override def resolveStructureValue[A: StructureDecoder](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = evaluationProvider.resolveStructureValue(
    flagKey = flagKey,
    defaultValue = defaultValue,
    context = context
  )

}

object ProviderImpl {

  def apply[F[_]](evaluationProvider: EvaluationProvider[F]): ProviderImpl[F] =
    new ProviderImpl[F](
      beforeHooks = List.empty[BeforeHook[F]],
      evaluationProvider = evaluationProvider,
    )

}
