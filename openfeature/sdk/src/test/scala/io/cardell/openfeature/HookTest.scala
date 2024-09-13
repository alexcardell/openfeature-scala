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

import cats.effect.IO
import cats.syntax.all._
import munit.CatsEffectSuite

class HookTest extends CatsEffectSuite {

  val initContext = EvaluationContext(
    Map("k1" -> ContextValue.StringValue("s1"))
  )

  def hookContext(ctx: EvaluationContext) = HookContext(
    flagKey = "test-flag",
    defaultValue = FlagValue.BooleanValue(false),
    evaluationContext = ctx
  )

  test("no-op hooks preserve original context") {
    val expected = initContext

    val h1 = BeforeHook[IO] { case _ => IO.pure(None) }
    val h2 = BeforeHook[IO] { case _ => IO.pure(None) }

    val hooks = List(h1, h2)

    val ctx = hookContext(initContext)

    for {
      result <- Hooks.run(hooks)(ctx, HookHints.empty)
    } yield assertEquals(result, expected)

  }

  test("hooks can add keys to original context") {
    val newContext = EvaluationContext(
      initContext.values ++ Map("k3" -> ContextValue.DoubleValue(20.2))
    )
    val expected = newContext

    val h1 = BeforeHook[IO] { case _ => None.pure[IO] }
    val h2 = BeforeHook[IO] { case _ => Some(newContext).pure[IO] }

    val hooks = List(h1, h2)

    val ctx = hookContext(initContext)

    for {
      result <- Hooks.run(hooks)(ctx, HookHints.empty)
    } yield assertEquals(result, expected)

  }

  test("hooks can replace keys from original") {
    val newValues = Map("k1" -> ContextValue.DoubleValue(20.2))

    val expected = EvaluationContext(newValues)

    val newContext = EvaluationContext(
      initContext.values ++ newValues
    )

    val h1 = BeforeHook[IO] { case _ => None.pure[IO] }
    val h2 = BeforeHook[IO] { case _ => Some(newContext).pure[IO] }

    val hooks = List(h1, h2)

    val ctx = hookContext(initContext)

    for {
      result <- Hooks.run(hooks)(ctx, HookHints.empty)
    } yield assertEquals(result, expected)

  }

  // test("erroring hooks short-circuit execution") {
  //   val firstValues = Map("k1" -> ContextValue.DoubleValue(20.2))
  //
  //   val firstContext = EvaluationContext(
  //     initContext.values ++ firstValues
  //   )
  //
  //   val expected     = Some(firstContext)
  //
  //   val secondValues = Map("k5" -> ContextValue.StringValue("test-value"))
  //   val secondContext = EvaluationContext(
  //     firstContext.values ++ secondValues
  //   )
  //
  //   val hookError = new Throwable("hook error")
  //
  //   val h1 = BeforeHook[IO] { case _ => Some(firstContext).pure[IO] }
  //   val h2 = BeforeHook[IO] { case _ => IO.raiseError(hookError) }
  //   val h3 = BeforeHook[IO] { case _ => Some(secondContext).pure[IO] }
  //
  //   val hooks = List(h1, h2, h3)
  //
  //   val ctx = hookContext(initContext)
  //
  //   for {
  //     result <- Hook.run(hooks)(ctx, HookHints.empty)
  //   } yield assertEquals(result, expected)
  //
  // }

}
