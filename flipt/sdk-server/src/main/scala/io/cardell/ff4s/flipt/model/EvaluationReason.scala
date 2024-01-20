package io.cardell.ff4s.flipt.model

import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.DecodingFailure.Reason

sealed trait EvaluationReason

object EvaluationReason {
  case object Unknown extends EvaluationReason
  case object FlagDisabled extends EvaluationReason
  case object Match extends EvaluationReason
  case object Default extends EvaluationReason

  implicit val d: Decoder[EvaluationReason] = Decoder.instance { cursor =>
    val json = cursor.value

    json.asString match {
      case Some(v) if v == "UNKNOWN_EVALUATION_REASON" =>
        Right(Unknown)
      case Some(v) if v == "FLAG_DISABLED_EVALUATION_REASON" =>
        Right(FlagDisabled)
      case Some(v) if v == "MATCH_EVALUATION_REASON" =>
        Right(Match)
      case Some(v) if v == "DEFAULT_EVALUATION_REASON" =>
        Right(Default)
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
