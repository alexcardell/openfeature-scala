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

package io.cardell.openfeature.provider.java

import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.FlagValue.IntValue
import io.cardell.openfeature.FlagValue.StringValue
import io.cardell.openfeature.Structure
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureDecoderError
import io.cardell.openfeature.StructureEncoder

case class TestVariant(field: String, intField: Int)

object TestVariant {

  implicit val sd: StructureDecoder[TestVariant] =
    new StructureDecoder[TestVariant] {

      def decodeStructure(
          s: Structure
      ): Either[StructureDecoderError, TestVariant] = {
        val maybeStruct =
          for {
            field    <- s.values.get("field")
            intField <- s.values.get("intField")
            variant <-
              (field, intField) match {
                case (StringValue(s), IntValue(i)) => Some(TestVariant(s, i))
                case _                             => None
              }
          } yield variant

        maybeStruct match {
          case None =>
            Left(
              StructureDecoderError(
                new Throwable(
                  "some fields missing converting TestVariant to Structure"
                )
              )
            )
          case Some(value) => Right(value)
        }

      }

    }

  implicit val se: StructureEncoder[TestVariant] =
    new StructureEncoder[TestVariant] {

      def encodeStructure(
          in: TestVariant
      ): Structure = Structure(
        Map(
          "field"    -> StringValue(in.field),
          "intField" -> IntValue(in.intField)
        )
      )

    }

}
