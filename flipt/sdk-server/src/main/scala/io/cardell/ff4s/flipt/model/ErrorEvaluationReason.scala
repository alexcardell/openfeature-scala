package io.cardell.ff4s.flipt.model

import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.DecodingFailure.Reason

sealed trait ErrorEvaluationReason

object ErrorEvaluationReason {
  case object NotFound extends ErrorEvaluationReason
  case object Unknown extends ErrorEvaluationReason

  implicit val d: Decoder[ErrorEvaluationReason] = Decoder.instance { cursor =>
    val json = cursor.value

    json.asString match {
      case Some(v) if v == "NOT_FOUND_ERROR_EVALUATION_REASON" =>
        Right(NotFound)
      case Some(v) if v == "UNKNOWN_ERROR_EVALUATION_REASON" =>
        Right(Unknown)
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
