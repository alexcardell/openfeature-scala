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
import io.circe.Json
import io.circe.JsonObject

import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.FlagValue._
import io.cardell.openfeature.Structure

// TODO rename
object JsonStructureConverters {

  def jsonToStructure(json: JsonObject): Structure = {
    val structure = json.toMap.mapFilter {
      case o if o.isObject =>
        o.asObject.map(jsonToStructure).map(StructureValue(_))
      case n if n.isNumber  => n.asNumber.map(_.toDouble).map(DoubleValue(_))
      case s if s.isString  => s.asString.map(StringValue(_))
      case b if b.isBoolean => b.asBoolean.map(BooleanValue(_))
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
