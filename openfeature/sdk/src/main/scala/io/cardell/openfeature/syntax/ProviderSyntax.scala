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

import io.cardell.openfeature.AfterHook
import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.ErrorHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FinallyHook
import io.cardell.openfeature.Hook
import io.cardell.openfeature.HookContext
import io.cardell.openfeature.HookHints
import io.cardell.openfeature.provider.Provider

trait ProviderSyntax {

  implicit def providerOps[F[_]](provider: Provider[F]): ProviderOps[F] =
    new ProviderOps[F](provider)

}

final class ProviderOps[F[_]](provider: Provider[F]) {

  def withHooks(hooks: List[Hook[F]]): Provider[F] =
    hooks.foldLeft(provider)((provider, hook) => provider.withHook(hook))

  def withBeforeHook(hook: BeforeHook[F]): Provider[F] = provider.withHook(
    hook
  )

  def withBeforeHook(
      hook: (HookContext, HookHints) => F[Option[EvaluationContext]]
  ): Provider[F] = provider.withHook(BeforeHook[F](hook))

  def withErrorHook(hook: ErrorHook[F]): Provider[F] = provider.withHook(
    hook
  )

  def withErrorHook(
      hook: (HookContext, HookHints, Throwable) => F[Unit]
  ): Provider[F] = provider.withHook(ErrorHook[F](hook))

  def withAfterHook(hook: AfterHook[F]): Provider[F] = provider.withHook(
    hook
  )

  def withAfterHook(
      hook: (HookContext, HookHints) => F[Unit]
  ): Provider[F] = provider.withHook(AfterHook[F](hook))

  def withFinallyHook(hook: FinallyHook[F]): Provider[F] = provider
    .withHook(hook)

  def withFinallyHook(
      hook: (HookContext, HookHints) => F[Unit]
  ): Provider[F] = provider.withHook(FinallyHook[F](hook))

}
