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

package io.cardell.openfeature.otel4s

import cats.effect.IO
import munit.CatsEffectSuite
import org.typelevel.otel4s.sdk.testkit.trace.TracesTestkit
import org.typelevel.otel4s.trace.StatusCode

import io.cardell.openfeature.AfterHook
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.provider.ProviderImpl
import io.cardell.openfeature.provider.memory.MemoryProvider

class TraceHookTest extends CatsEffectSuite {

  val setupProvider = MemoryProvider[IO](
    Map("boolean-flag" -> FlagValue.BooleanValue(true))
  )

  val setupTestkit = TracesTestkit.inMemory[IO]()

  test("span is applied") {
    val expectedFlagResult = true
    val expectedSpanName   = "resolve-flag"
    val expectedSpanCount  = 1
    val expectedSpanStatus = StatusCode.Ok

    setupTestkit.use { kit =>
      val setupTracer = kit.tracerProvider.tracer("name").get

      setupTracer.flatMap { implicit tracer =>
        for {
          hooks    <- TraceHooks.ioLocal
          provider <- setupProvider.map(ProviderImpl[IO])
          hookedProvider = hooks.foldLeft(provider)(_ withHook _)
          flagResolution <- hookedProvider.resolveBooleanValue(
            "boolean-flag",
            false,
            EvaluationContext.empty
          )
          flagResult = flagResolution.value
          spans <- kit.finishedSpans
          spanCount  = spans.size
          headSpan   = spans.headOption
          spanName   = headSpan.map(_.name)
          spanStatus = headSpan.map(_.status.status)
          spanEnded  = headSpan.map(_.hasEnded)
          spanAttrs  = headSpan.map(_.attributes.elements)
          flagKeyAttrExists = spanAttrs.map(
            _.exists(_ == FeatureFlagAttributes.FeatureFlagKey("boolean-flag"))
          )
        } yield {
          assertEquals(flagResult, expectedFlagResult)
          assertEquals(spanCount, expectedSpanCount)
          assertEquals(spanName, Some(expectedSpanName))
          assertEquals(spanStatus, Some(expectedSpanStatus))
          assertEquals(spanEnded, Some(true))
          assertEquals(flagKeyAttrExists, Some(true))
        }

      }
    }

  }

  test("span is recorded as exception when exception thrown in after hook") {
    val expectedSpanName   = "resolve-flag"
    val expectedSpanCount  = 1
    val expectedSpanStatus = StatusCode.Error

    val throwingHook = AfterHook[IO] { case _ =>
      IO.raiseError(new Throwable("throwing hook"))
    }

    setupTestkit.use { kit =>
      val setupTracer = kit.tracerProvider.tracer("name").get

      setupTracer.flatMap { implicit tracer =>
        for {
          traceHooks <- TraceHooks.ioLocal
          (before, others) = (traceHooks.head, traceHooks.tail)
          hooks            = List(before, throwingHook) ++ others
          provider <- setupProvider.map(ProviderImpl[IO])
          hookedProvider = hooks.foldLeft(provider)(_ withHook _)
          _ <-
            hookedProvider
              .resolveBooleanValue(
                "boolean-flag",
                false,
                EvaluationContext.empty
              )
              .attempt
          spans <- kit.finishedSpans
          spanCount  = spans.size
          headSpan   = spans.headOption
          spanName   = headSpan.map(_.name)
          spanStatus = headSpan.map(_.status.status)
          spanEnded  = headSpan.map(_.hasEnded)
          spanAttrs  = headSpan.map(_.attributes.elements)
          flagKeyAttrExists = spanAttrs.map(
            _.exists(_ == FeatureFlagAttributes.FeatureFlagKey("boolean-flag"))
          )
        } yield {
          assertEquals(spanCount, expectedSpanCount)
          assertEquals(spanName, Some(expectedSpanName))
          assertEquals(spanStatus, Some(expectedSpanStatus))
          assertEquals(spanEnded, Some(true))
          assertEquals(flagKeyAttrExists, Some(true))
        }
      }
    }

  }

}
