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
import cats.syntax.all._

import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.Hook
import io.cardell.openfeature.HookContext
import io.cardell.openfeature.HookHints
import io.cardell.openfeature.Hooks
import io.cardell.openfeature.StructureDecoder

protected class ProviderImpl[F[_]: Monad](
    evaluationProvider: EvaluationProvider[F],
    val beforeHooks: List[BeforeHook[F]]
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
  ): F[ResolutionDetails[Boolean]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(flagKey, context, FlagValue(defaultValue)),
          HookHints.empty
        )
      res <- evaluationProvider.resolveBooleanValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    } yield res

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(flagKey, context, FlagValue(defaultValue)),
          HookHints.empty
        )
      res <- evaluationProvider.resolveStringValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    } yield res

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(flagKey, context, FlagValue(defaultValue)),
          HookHints.empty
        )
      res <- evaluationProvider.resolveIntValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    } yield res

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(flagKey, context, FlagValue(defaultValue)),
          HookHints.empty
        )
      res <- evaluationProvider.resolveDoubleValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    } yield res

  override def resolveStructureValue[A: StructureDecoder](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(flagKey, context, FlagValue(defaultValue)),
          HookHints.empty
        )
      res <- evaluationProvider.resolveStructureValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    } yield res

}

object ProviderImpl {

  def apply[F[_]: Monad](
      evaluationProvider: EvaluationProvider[F]
  ): ProviderImpl[F] =
    new ProviderImpl[F](
      beforeHooks = List.empty[BeforeHook[F]],
      evaluationProvider = evaluationProvider
    )

}
