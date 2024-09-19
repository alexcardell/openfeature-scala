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
import io.circe.Json
import io.circe.Json.JArray
import io.circe.Json.JBoolean
import io.circe.Json.JNull
import io.circe.Json.JNumber
import io.circe.Json.JObject
import io.circe.Json.JString
import io.circe.JsonObject
import io.circe.syntax._

import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.FlagValue._
import io.cardell.openfeature.Structure
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureDecoder2
import io.cardell.openfeature.StructureDecoderError
import io.cardell.openfeature.StructureEncoder

trait CirceStructureDecoder {

  implicit def circeStructureDecode[A](
      implicit d: Decoder[A]
  ): StructureDecoder2[A] =
    new StructureDecoder2[A] {

      override def decodeStructure(
          structure: Structure
      ): Either[StructureDecoderError, A] = {
        val jsonObject = CirceStuff.structureToJson(structure)

        jsonObject.toJson.as[A].leftMap(CirceDecodeError)
      }

    }

}

trait CirceStructureEncoder {

  implicit def circeStructureEncoder[A](
      implicit encoder: Encoder.AsObject[A]
  ): StructureEncoder[A] =
    new StructureEncoder[A] {

      override def encodeStructure(value: A): Structure = CirceStuff
        .jsonToStructure(encoder.encodeObject(value))

    }

}

// TODO rename
object CirceStuff {

  def jsonToStructure(json: JsonObject): Structure = {
    val structure = json.toMap.mapFilter {
      case o if o.isObject =>
        o.asObject.map(jsonToStructure).map(StructureValue)
      case n if n.isNumber  => n.asNumber.map(_.toDouble).map(DoubleValue)
      case s if s.isString  => s.asString.map(StringValue)
      case b if b.isBoolean => b.asBoolean.map(BooleanValue)
      case n if n.isNull    => none[FlagValue]
      case _                => none[FlagValue]
    }

    Structure(structure)
  }

  def structureToJson(structure: Structure): JsonObject = {
    val json = structure.values.map { case (k, v) =>
      val value =
        v match {
          case BooleanValue(b)   => Json.fromBoolean(b)
          case DoubleValue(d)    => Json.fromDoubleOrNull(d)
          case StringValue(s)    => Json.fromString(s)
          case IntValue(i)       => Json.fromInt(i)
          case StructureValue(s) => Json.fromJsonObject(structureToJson(s))
        }

      k -> value
    }

    JsonObject.fromMap(json)
  }

}
