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
import io.circe.Encoder
import io.circe.Json
import io.circe.JsonNumber
import io.circe.JsonObject
import io.circe.syntax._
import scala.util.Try

import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.StructureEncoder
import io.cardell.openfeature.StructureEncoderError

trait CirceStructureEncoder {

  implicit def encoder[A](
      implicit e: Encoder.AsObject[A]
  ): StructureEncoder[A] =
    new StructureEncoder[A] {

      def encodeStructure(
          in: A
      ): Either[StructureEncoderError, Map[String, FlagValue]] = {
        Try {
          val encoded = encodeJsonObject(in.asJsonObject)

          encoded
          // TODO error
        }.toEither.leftMap(_ => new StructureEncoderError {})
      }

      private def encodeJsonObject(
          obj: JsonObject
      ): Map[String, FlagValue] = {
        obj.toMap.map {
          case (k, j) if j.isString =>
            (k, FlagValue.StringValue(j.asString.get))
          case (k, j) if j.isBoolean =>
            (k, FlagValue.BooleanValue(j.asBoolean.get))
          case (k, j) if j.isNumber =>
            val number = j.asNumber.get
            number.toInt match {
              case Some(i) => (k, FlagValue.IntValue(i))
              case None    => (k, FlagValue.DoubleValue(number.toDouble))
            }
          case (k, j) if j.isObject =>
            val inner = encodeJsonObject(j.asObject.get)
            (k, FlagValue.StructureValue(inner))
          case (k, j) =>
            throw new Throwable(
              s"Could not handle key ${k} with value ${j.toString()}"
            )
        }
      }

    }

}
