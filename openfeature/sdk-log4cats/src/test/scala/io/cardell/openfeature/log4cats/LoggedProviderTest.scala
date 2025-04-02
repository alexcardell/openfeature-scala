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

package io.cardell.openfeature.log4cats

import cats.effect.IO
import munit.CatsEffectSuite
import org.typelevel.log4cats.StructuredLogger
import org.typelevel.log4cats.testing.StructuredTestingLogger

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.provider.memory.MemoryProvider

class LoggedProviderTest extends CatsEffectSuite {

  val flagKey   = "my-boolean-flag"
  val flagValue = true

  val boolState = Map(flagKey -> FlagValue.BooleanValue(flagValue))

  def setupProvider(
      l: StructuredTestingLogger[IO],
      state: Map[String, FlagValue] = boolState
  ) =
    for {
      provider <- MemoryProvider[IO](state)
    } yield {
      implicit val il: StructuredLogger[IO] = l
        .asInstanceOf[StructuredLogger[IO]]
      new LoggedProvider[IO](provider)
    }

  def setupLogger = StructuredTestingLogger.impl[IO]()

  test("Successful evaluation logs message") {
    val logger: StructuredTestingLogger[IO] = setupLogger

    for {
      provider <- setupProvider(logger)
      _ <- provider.resolveBooleanValue(flagKey, false, EvaluationContext.empty)
      logs <- logger.logged.map(_.toList)
    } yield {
      assertEquals(logs.length, 1)
      assert(logs.head.throwOpt.isEmpty)
      assert(logs.head.ctx.get("feature_flag.result.variant").isEmpty)
    }
  }

  test("Failed variant evaluation logs message with error") {
    implicit val logger: StructuredTestingLogger[IO] = setupLogger

    val provider = {
      new LoggedProvider[IO](
        new ThrowingEvaluationProvider[IO]()
      )
    }

    for {
      _ <-
        provider
          .resolveBooleanValue(flagKey, false, EvaluationContext.empty)
          .attempt
      logs <- logger.logged.map(_.toList)
    } yield {
      assertEquals(logs.length, 1)
      assert(logs.head.throwOpt.isDefined)
    }
  }

}
