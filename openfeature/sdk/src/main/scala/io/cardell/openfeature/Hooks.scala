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

import cats.Applicative
import cats.Monad
import cats.syntax.all._

case class HookContext(
    flagKey: String,
    evaluationContext: EvaluationContext,
    defaultValue: FlagValue
)

object Hooks {

  def runBefore[F[_]: Monad](
      hooks: List[BeforeHook[F]]
  )(context: HookContext, hints: HookHints): F[EvaluationContext] = {
    def aux(
        hooks: List[BeforeHook[F]],
        context: HookContext
    ): F[Option[EvaluationContext]] =
      hooks match {
        case head :: tail =>
          head.apply(context, hints).flatMap {
            case Some(evaluationContext) =>
              val newContext = context.copy(evaluationContext =
                context.evaluationContext ++ evaluationContext
              )
              aux(tail, newContext)
            case None => aux(tail, context)
          }
        case Nil => context.evaluationContext.some.pure[F]
      }

    aux(hooks, context).map(_.getOrElse(context.evaluationContext))
  }

  def runErrors[F[_]: Applicative](
      hooks: List[ErrorHook[F]]
  )(context: HookContext, hints: HookHints, error: Throwable): F[Unit] =
    hooks.traverse(_.apply(context, hints, error)).void

  def runAfter[F[_]: Applicative](
      hooks: List[AfterHook[F]]
  )(context: HookContext, hints: HookHints): F[Unit] =
    hooks.traverse(_.apply(context, hints)).void

  def runFinally[F[_]: Applicative](
      hooks: List[FinallyHook[F]]
  )(context: HookContext, hints: HookHints): F[Unit] =
    hooks.traverse(_.apply(context, hints)).void

}
