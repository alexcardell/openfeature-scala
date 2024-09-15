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

package io.cardell.openfeature.provider.memory

import cats.effect.IO
import munit.CatsEffectSuite

import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureDecoderError

class MemoryProviderTest extends CatsEffectSuite {

  case class TestStructure(s: String, i: Int)
  case class OtherTestStructure(d: Double)

  implicit val a: StructureDecoder[TestStructure] =
    new StructureDecoder[TestStructure] {

      def decodeStructure(
          string: String
      ): Either[StructureDecoderError, TestStructure] = ???

    }

  test("can return boolean values") {
    val expected = true

    val flag  = FlagValue.BooleanValue(expected)
    val key   = "boolean-flag-key"
    val state = Map(key -> flag)

    val default = false

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveBooleanValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution.map(_.value)
      } yield assertEquals(result, expected)
    }
  }

  test("can return string values") {
    val expected = "string"

    val flag  = FlagValue.StringValue(expected)
    val key   = "string-flag-key"
    val state = Map(key -> flag)

    val default = "default"

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveStringValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution.map(_.value)
      } yield assertEquals(result, expected)
    }
  }

  test("can return int values when type is as expected") {
    val expected = 33

    val flag  = FlagValue.IntValue(expected)
    val key   = "int-flag-key"
    val state = Map(key -> flag)

    val default = 0

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveIntValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution.map(_.value)
      } yield assertEquals(result, expected)
    }
  }

  test("can return double values when type is as expected") {
    val expected = 40.0

    val flag  = FlagValue.DoubleValue(expected)
    val key   = "double-flag-key"
    val state = Map(key -> flag)

    val default = 0.0

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveDoubleValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution.map(_.value)
      } yield assertEquals(result, expected)
    }
  }

  test("receives type mismatch error when boolean not received") {
    val expectedValue     = false
    val expectedErrorCode = Some(ErrorCode.TypeMismatch)

    val flag  = FlagValue.DoubleValue(0.0)
    val key   = "boolean-flag-key"
    val state = Map(key -> flag)

    val default = expectedValue

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveBooleanValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution
        resultValue = result.value
        errorCode   = result.errorCode
      } yield {
        assertEquals(resultValue, expectedValue)
        assertEquals(errorCode, expectedErrorCode)
      }
    }
  }

  test("receives type mismatch error when string not received") {
    val expectedValue     = "default"
    val expectedErrorCode = Some(ErrorCode.TypeMismatch)

    val flag  = FlagValue.DoubleValue(0.0)
    val key   = "string-flag-key"
    val state = Map(key -> flag)

    val default = expectedValue

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveStringValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution
        resultValue = result.value
        errorCode   = result.errorCode
      } yield {
        assertEquals(resultValue, expectedValue)
        assertEquals(errorCode, expectedErrorCode)
      }
    }
  }

  test("receives type mismatch error when int not received") {
    val expectedValue     = 33
    val expectedErrorCode = Some(ErrorCode.TypeMismatch)

    val flag  = FlagValue.DoubleValue(0.0)
    val key   = "int-flag-key"
    val state = Map(key -> flag)

    val default = expectedValue

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveIntValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution
        resultValue = result.value
        errorCode   = result.errorCode
      } yield {
        assertEquals(resultValue, expectedValue)
        assertEquals(errorCode, expectedErrorCode)
      }
    }
  }

  test("receives type mismatch error when double not received") {
    val expectedValue     = 40.0
    val expectedErrorCode = Some(ErrorCode.TypeMismatch)

    val flag  = FlagValue.IntValue(0)
    val key   = "double-flag-key"
    val state = Map(key -> flag)

    val default = expectedValue

    MemoryProvider[IO](state).flatMap { provider =>
      val resolution = provider.resolveDoubleValue(
        key,
        default,
        EvaluationContext.empty
      )

      for {
        result <- resolution
        resultValue = result.value
        errorCode   = result.errorCode
      } yield {
        assertEquals(resultValue, expectedValue)
        assertEquals(errorCode, expectedErrorCode)
      }
    }
  }

}
