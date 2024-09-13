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

import cats.effect.IO
import cats.effect.kernel.Ref
import munit.CatsEffectSuite

import io.cardell.openfeature.AfterHook
import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.ErrorHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.HookContext
import io.cardell.openfeature.HookHints
import io.cardell.openfeature.FinallyHook

class ProviderImplTest extends CatsEffectSuite {
  val evaluationProvider = new StubEvaluationProvider[IO]

  val beforeHook1 =
    new BeforeHook[IO] {
      def apply(ctx: HookContext, hints: HookHints) = IO.pure(None)
    }

  test("Default apply method has no before hooks") {
    val expected = List.empty[BeforeHook[IO]]

    val result = ProviderImpl(evaluationProvider).beforeHooks

    assertEquals(result, expected)
  }

  test("withHook adds new before hook to empty list") {
    val expected = List(beforeHook1)

    val provider = ProviderImpl(evaluationProvider)

    val result =
      provider.withHook(beforeHook1).asInstanceOf[ProviderImpl[IO]].beforeHooks

    assertEquals(result, expected)
  }

  test("withHook appends new before hook to existing list") {
    val beforeHook2 =
      new BeforeHook[IO] {
        def apply(ctx: HookContext, hints: HookHints) = IO.pure(None)
      }

    val expected = List(beforeHook1, beforeHook2)

    val provider = ProviderImpl(evaluationProvider).withHook(beforeHook1)

    val result =
      provider.withHook(beforeHook2).asInstanceOf[ProviderImpl[IO]].beforeHooks

    assertEquals(result, expected)
  }

  test("before hooks run on boolean evaluation") {
    val ref = Ref.unsafe[IO, Int](0)

    val h1 = BeforeHook[IO] { case _ => ref.update(_ + 1).as(None) }
    val h2 = BeforeHook[IO] { case _ => ref.update(_ + 2).as(None) }

    val provider = ProviderImpl(evaluationProvider).withHook(h1).withHook(h2)

    val expected = 3

    for {
      _ <- provider.resolveBooleanValue(
        "test-flag",
        false,
        EvaluationContext.empty
      )
      result <- ref.get
    } yield assertEquals(result, expected)
  }

  test("error hooks run when before hook throws") {
    val ref = Ref.unsafe[IO, Int](0)

    val beforeHook = BeforeHook[IO] { case _ =>
      IO.raiseError(new Throwable("before hook error"))
    }

    val errorHook = ErrorHook[IO] { case _ => ref.update(_ + 2) }

    val provider = ProviderImpl(evaluationProvider)
      .withHook(beforeHook)
      .withHook(errorHook)

    val expected = 2

    for {
      _ <-
        provider
          .resolveBooleanValue(
            "test-flag",
            false,
            EvaluationContext.empty
          )
          .attempt
      result <- ref.get
    } yield assertEquals(result, expected)
  }

  test("before hook errors are raised from provider") {
    val beforeHook = BeforeHook[IO] { case _ =>
      IO.raiseError(new Throwable("before hook error"))
    }

    val provider = ProviderImpl(evaluationProvider)
      .withHook(beforeHook)

    for {
      result <-
        provider
          .resolveBooleanValue(
            "test-flag",
            false,
            EvaluationContext.empty
          )
          .attempt
    } yield assert(result.isLeft, "result was not a Left")
  }

  test("after hooks run after successful evaluation") {
    val ref = Ref.unsafe[IO, Int](0)

    val afterHook = AfterHook[IO] { case _ => ref.update(_ + 2) }

    val provider = ProviderImpl(evaluationProvider)
      .withHook(afterHook)

    val expected = 2

    for {
      _ <-
        provider
          .resolveBooleanValue(
            "test-flag",
            false,
            EvaluationContext.empty
          )
          .attempt
      result <- ref.get
    } yield assertEquals(result, expected)
  }

  test("after hooks are skipped after before hook throws") {
    val ref = Ref.unsafe[IO, Int](0)

    val beforeHook = AfterHook[IO] { case _ =>
      IO.raiseError(new Throwable("before hook error"))
    }

    val afterHook = AfterHook[IO] { case _ => ref.update(_ + 2) }

    val provider = ProviderImpl(evaluationProvider)
      .withHook(beforeHook)
      .withHook(afterHook)

    val expected = 0

    for {
      _ <-
        provider
          .resolveBooleanValue(
            "test-flag",
            false,
            EvaluationContext.empty
          )
          .attempt
      result <- ref.get
    } yield assertEquals(result, expected)
  }

  test("finally hooks run after successful evaluation") {
    val ref = Ref.unsafe[IO, Int](0)

    val finallyHook = FinallyHook[IO] { case _ => ref.update(_ + 2) }

    val provider = ProviderImpl(evaluationProvider)
      .withHook(finallyHook)

    val expected = 2

    for {
      _ <-
        provider
          .resolveBooleanValue(
            "test-flag",
            false,
            EvaluationContext.empty
          )
          .attempt
      result <- ref.get
    } yield assertEquals(result, expected)
  }

  test("finally hooks run when before hook throws") {
    val ref = Ref.unsafe[IO, Int](0)

    val beforeHook = BeforeHook[IO] { case _ =>
      IO.raiseError(new Throwable("before hook error"))
    }

    val finallyHook = FinallyHook[IO] { case _ => ref.update(_ + 2) }

    val provider = ProviderImpl(evaluationProvider)
      .withHook(beforeHook)
      .withHook(finallyHook)

    val expected = 2

    for {
      _ <-
        provider
          .resolveBooleanValue(
            "test-flag",
            false,
            EvaluationContext.empty
          )
          .attempt
      result <- ref.get
    } yield assertEquals(result, expected)
  }

}
