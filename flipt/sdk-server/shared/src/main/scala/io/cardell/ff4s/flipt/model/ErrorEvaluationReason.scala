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

package io.cardell.ff4s.flipt.model

import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.DecodingFailure.Reason

sealed trait ErrorEvaluationReason

object ErrorEvaluationReason {
  case object NotFound extends ErrorEvaluationReason
  case object Unknown  extends ErrorEvaluationReason

  implicit val d: Decoder[ErrorEvaluationReason] = Decoder.instance { cursor =>
    val json = cursor.value

    json.asString match {
      case Some(v) if v == "NOT_FOUND_ERROR_EVALUATION_REASON" =>
        Right(NotFound)
      case Some(v) if v == "UNKNOWN_ERROR_EVALUATION_REASON" => Right(Unknown)
      case Some(other) =>
        Left(
          DecodingFailure(
            Reason.CustomReason(s"Invalid enum value: ${other}"),
            cursor
          )
        )
      case None =>
        Left(
          DecodingFailure(Reason.WrongTypeExpectation("string", json), cursor)
        )
    }
  }

}
