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

package io.cardell.openfeature.circe

import cats.syntax.all._
import io.circe.Decoder
import io.circe.Encoder

import io.cardell.openfeature.Structure
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureDecoderError
import io.cardell.openfeature.StructureEncoder

trait CirceStructureDecoder {

  implicit def circeStructureDecode[A](
      implicit d: Decoder[A]
  ): StructureDecoder[A] =
    new StructureDecoder[A] {

      override def decodeStructure(
          structure: Structure
      ): Either[StructureDecoderError, A] = {
        val jsonObject = JsonStructureConverters.structureToJson(structure)

        jsonObject.toJson.as[A].leftMap(CirceDecodeError)
      }

    }

}

trait CirceStructureEncoder {

  implicit def circeStructureEncoder[A](
      implicit encoder: Encoder.AsObject[A]
  ): StructureEncoder[A] =
    new StructureEncoder[A] {

      override def encodeStructure(value: A): Structure =
        JsonStructureConverters
          .jsonToStructure(encoder.encodeObject(value))

    }

}
