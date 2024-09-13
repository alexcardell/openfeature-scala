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

import cats.Monad
import cats.syntax.all._

import io.cardell.openfeature.FlagValue.BooleanValue
import io.cardell.openfeature.FlagValue.DoubleValue
import io.cardell.openfeature.FlagValue.IntValue
import io.cardell.openfeature.FlagValue.StringValue
import io.cardell.openfeature.FlagValue.StructureValue
import cats.Applicative

sealed trait FlagValueType

object FlagValueType {
  case object BooleanValueType   extends FlagValueType
  case object StringValueType    extends FlagValueType
  case object IntValueType       extends FlagValueType
  case object DoubleValueType    extends FlagValueType
  case object StructureValueType extends FlagValueType
}

sealed trait FlagValue

object FlagValue {
  case class BooleanValue(value: Boolean) extends FlagValue
  case class StringValue(value: String)   extends FlagValue
  case class IntValue(value: Int)         extends FlagValue
  case class DoubleValue(value: Double)   extends FlagValue
  case class StructureValue[A](value: A)  extends FlagValue

  def apply(b: Boolean): FlagValue = BooleanValue(b)
  def apply(s: String): FlagValue  = StringValue(s)
  def apply(i: Int): FlagValue     = IntValue(i)
  def apply(d: Double): FlagValue  = DoubleValue(d)

  def apply[A](s: A): FlagValue = StructureValue(s)
}

case class HookContext(
    flagKey: String,
    evaluationContext: EvaluationContext,
    defaultValue: FlagValue
) {

  def valueType: FlagValueType =
    defaultValue match {
      case _: BooleanValue      => FlagValueType.BooleanValueType
      case _: StringValue       => FlagValueType.StringValueType
      case _: IntValue          => FlagValueType.IntValueType
      case _: DoubleValue       => FlagValueType.DoubleValueType
      case _: StructureValue[_] => FlagValueType.StructureValueType
    }

}

object HookHints {
  def empty: HookHints = Map.empty
}

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

}
