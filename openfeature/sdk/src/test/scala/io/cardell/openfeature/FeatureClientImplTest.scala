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
import munit.CatsEffectSuite

import io.cardell.openfeature.provider.ProviderImpl
import io.cardell.openfeature.provider.StubEvaluationProvider
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails
import cats.effect.kernel.Ref

class FeatureClientImplTest extends CatsEffectSuite {
  val evalProvider = new StubEvaluationProvider[IO]()
  val provider     = ProviderImpl[IO](evalProvider)

  val beforeHook1 =
    new BeforeHook[IO] {
      def apply(ctx: HookContext, hints: HookHints) = IO.pure(None)
    }

  test("Default apply method has no before hooks") {
    val expected = List.empty[BeforeHook[IO]]

    val result = FeatureClientImpl[IO](provider).beforeHooks

    assertEquals(expected, result)
  }

  test("withHook adds new before hook to empty list") {
    val expected = List(beforeHook1)

    val client = FeatureClientImpl[IO](provider)

    val result = client.withHook(beforeHook1).beforeHooks

    assertEquals(expected, result)
  }

  test("before hooks run on boolean evaluation") {
    val ref = Ref.unsafe[IO, Int](0)

    val h1 = BeforeHook[IO] { case _ => ref.update(_ + 1).as(None) }
    val h2 = BeforeHook[IO] { case _ => ref.update(_ + 2).as(None) }

    val client = FeatureClientImpl[IO](provider).withHook(h1).withHook(h2)

    val expected = 3

    for {
      _      <- client.getBooleanValue("test-flag", false)
      result <- ref.get
    } yield assertEquals(result, expected)
  }

  test("error hooks run on before hook error") {
    val ref = Ref.unsafe[IO, Int](0)

    val beforeHook = BeforeHook[IO] { case _ =>
      IO.raiseError(new Throwable("before hook error"))
    }

    val errorHook = ErrorHook[IO] { case _ => ref.update(_ + 2) }

    val client = FeatureClientImpl[IO](provider)
      .withHook(beforeHook)
      .withHook(errorHook)

    val expectedCount = 2
    val expectedFlag  = false

    for {
      result   <- client.getBooleanValue("test-flag", expectedFlag)
      refCount <- ref.get
    } yield {
      assertEquals(refCount, expectedCount)
      assertEquals(result, expectedFlag)
    }
  }

  test("error hooks run on evaluation error") {
    val ref = Ref.unsafe[IO, Int](0)

    val errorHook = ErrorHook[IO] { case _ => ref.update(_ + 2) }

    val client = FeatureClientImpl[IO](ThrowingEvaluationProvider)
      .withHook(errorHook)

    val expectedCount = 2
    val expectedFlag  = false

    for {
      result   <- client.getBooleanValue("test-flag", expectedFlag)
      refCount <- ref.get
    } yield {
      assertEquals(refCount, expectedCount)
      assertEquals(result, expectedFlag)
    }
  }

}

object ThrowingEvaluationProvider extends EvaluationProvider[IO] {

  override def metadata: ProviderMetadata = ProviderMetadata(
    name = "throwing-provider"
  )

  val error    = new Throwable("ThrowingEvaluationProvider error")
  def raise[A] = IO.raiseError[A](error)

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): IO[ResolutionDetails[Boolean]] = raise

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): IO[ResolutionDetails[String]] = raise

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): IO[ResolutionDetails[Int]] = raise

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): IO[ResolutionDetails[Double]] = raise

  override def resolveStructureValue[A: StructureDecoder](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): IO[ResolutionDetails[A]] = raise

}
