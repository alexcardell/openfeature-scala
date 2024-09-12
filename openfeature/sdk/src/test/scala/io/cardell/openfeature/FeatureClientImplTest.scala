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

}
