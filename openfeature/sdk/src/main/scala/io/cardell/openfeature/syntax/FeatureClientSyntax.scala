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

package io.cardell.openfeature.syntax

import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.ErrorHook
import io.cardell.openfeature.AfterHook
import io.cardell.openfeature.FinallyHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FeatureClient
import io.cardell.openfeature.Hook
import io.cardell.openfeature.HookContext
import io.cardell.openfeature.HookHints

trait FeatureClientSyntax {

  implicit def hookOps[F[_]](
      client: FeatureClient[F]
  ): FeatureClientHookOps[F] = new FeatureClientHookOps[F](client)

}

class FeatureClientHookOps[F[_]](client: FeatureClient[F]) {

  def withHooks(hooks: List[Hook[F]]): FeatureClient[F] =
    hooks.foldLeft(client)((client, hook) => client.withHook(hook))

  def withBeforeHook(hook: BeforeHook[F]): FeatureClient[F] = client.withHook(
    hook
  )

  def withBeforeHook(
      hook: (HookContext, HookHints) => F[Option[EvaluationContext]]
  ): FeatureClient[F] = client.withHook(BeforeHook[F](hook))

  def withErrorHook(hook: ErrorHook[F]): FeatureClient[F] = client.withHook(
    hook
  )

  def withErrorHook(
      hook: (HookContext, HookHints, Throwable) => F[Unit]
  ): FeatureClient[F] = client.withHook(ErrorHook[F](hook))

  def withAfterHook(hook: AfterHook[F]): FeatureClient[F] = client.withHook(
    hook
  )

  def withAfterHook(
      hook: (HookContext, HookHints) => F[Unit]
  ): FeatureClient[F] = client.withHook(AfterHook[F](hook))

  def withFinallyHook(hook: FinallyHook[F]): FeatureClient[F] = client
    .withHook(hook)

  def withFinallyHook(
      hook: (HookContext, HookHints) => F[Unit]
  ): FeatureClient[F] = client.withHook(FinallyHook[F](hook))

}
