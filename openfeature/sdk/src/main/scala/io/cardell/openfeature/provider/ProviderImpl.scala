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

import cats.MonadThrow
import cats.syntax.all._

import io.cardell.openfeature.AfterHook
import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.ErrorHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FinallyHook
import io.cardell.openfeature.HasFlagValue
import io.cardell.openfeature.Hook
import io.cardell.openfeature.HookContext
import io.cardell.openfeature.HookHints
import io.cardell.openfeature.Hooks
import io.cardell.openfeature.StructureCodec

protected class ProviderImpl[F[_]: MonadThrow](
    evaluationProvider: EvaluationProvider[F],
    val beforeHooks: List[BeforeHook[F]],
    val errorHooks: List[ErrorHook[F]],
    val afterHooks: List[AfterHook[F]],
    val finallyHooks: List[FinallyHook[F]]
) extends Provider[F] {

  override def metadata: ProviderMetadata = evaluationProvider.metadata

  override def withHook(hook: Hook[F]): ProviderImpl[F] =
    hook match {
      case h: BeforeHook[F] =>
        new ProviderImpl[F](
          evaluationProvider = evaluationProvider,
          beforeHooks = beforeHooks.appended(h),
          errorHooks = errorHooks,
          afterHooks = afterHooks,
          finallyHooks = finallyHooks
        )
      case h: ErrorHook[F] =>
        new ProviderImpl[F](
          evaluationProvider = evaluationProvider,
          beforeHooks = beforeHooks,
          errorHooks = errorHooks.appended(h),
          afterHooks = afterHooks,
          finallyHooks = finallyHooks
        )
      case h: AfterHook[F] =>
        new ProviderImpl[F](
          evaluationProvider = evaluationProvider,
          beforeHooks = beforeHooks,
          errorHooks = errorHooks,
          afterHooks = afterHooks.appended(h),
          finallyHooks = finallyHooks
        )
      case h: FinallyHook[F] =>
        new ProviderImpl[F](
          evaluationProvider = evaluationProvider,
          beforeHooks = beforeHooks,
          errorHooks = errorHooks,
          afterHooks = afterHooks,
          finallyHooks = finallyHooks.appended(h)
        )

    }

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] =
    hookedResolve[Boolean](flagKey, defaultValue, context) { newContext =>
      evaluationProvider.resolveBooleanValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    }

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] =
    hookedResolve[String](flagKey, defaultValue, context) { newContext =>
      evaluationProvider.resolveStringValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    }

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] =
    hookedResolve[Int](flagKey, defaultValue, context) { newContext =>
      evaluationProvider.resolveIntValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    }

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] =
    hookedResolve[Double](flagKey, defaultValue, context) { newContext =>
      evaluationProvider.resolveDoubleValue(
        flagKey = flagKey,
        defaultValue = defaultValue,
        context = newContext
      )
    }

  override def resolveStructureValue[A: StructureCodec](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = ???

  private def hookedResolve[A: HasFlagValue](
      flagKey: String,
      default: A,
      evaluationContext: EvaluationContext
  )(
      resolve: (EvaluationContext) => F[ResolutionDetails[A]]
  ): F[ResolutionDetails[A]] = {
    val hc = HookContext(
      flagKey = flagKey,
      defaultValue = HasFlagValue[A].toFlagValue(default),
      evaluationContext = evaluationContext
    )
    val hints = HookHints.empty

    val run =
      for {
        context <- Hooks.runBefore(beforeHooks)(hc, hints)
        hc2 = hc.copy(evaluationContext = context)
        res <- resolve(context)
        _ <-
          Hooks.runAfter(afterHooks)(
            hc2,
            hints
          )
      } yield res

    val runFinally              = Hooks.runFinally(finallyHooks)(hc, hints)
    def runErrors(e: Throwable) = Hooks.runErrors(errorHooks)(hc, hints, e)

    run.attempt.flatMap {
      case Right(value) => runFinally *> value.pure[F]
      case Left(error) =>
        runErrors(error).attempt.flatMap {
          case Right(_) => runFinally *> error.raiseError
          // TODO how to handle error in error hook?
          case Left(_) => runFinally *> error.raiseError
        }
    }
  }

}

object ProviderImpl {

  def apply[F[_]: MonadThrow](
      evaluationProvider: EvaluationProvider[F]
  ): ProviderImpl[F] =
    new ProviderImpl[F](
      evaluationProvider = evaluationProvider,
      beforeHooks = List.empty[BeforeHook[F]],
      errorHooks = List.empty[ErrorHook[F]],
      afterHooks = List.empty[AfterHook[F]],
      finallyHooks = List.empty[FinallyHook[F]]
    )

}
