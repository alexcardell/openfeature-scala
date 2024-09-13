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

sealed trait Hook[F[_]]

trait BeforeHook[F[_]] extends Hook[F] {

  def apply(
      context: HookContext,
      hints: HookHints
  ): F[Option[EvaluationContext]]

}

object BeforeHook {

  def apply[F[_]](
      f: (HookContext, HookHints) => F[Option[EvaluationContext]]
  ): BeforeHook[F] =
    new BeforeHook[F] {

      def apply(
          context: HookContext,
          hints: HookHints
      ): F[Option[EvaluationContext]] = f(context, hints)

    }

}

trait ErrorHook[F[_]] extends Hook[F] {

  def apply(context: HookContext, hints: HookHints, error: Throwable): F[Unit]

}

object ErrorHook {

  def apply[F[_]](
      f: (HookContext, HookHints, Throwable) => F[Unit]
  ): ErrorHook[F] =
    new ErrorHook[F] {

      def apply(
          context: HookContext,
          hints: HookHints,
          error: Throwable
      ): F[Unit] = f(context, hints, error)

    }

}
