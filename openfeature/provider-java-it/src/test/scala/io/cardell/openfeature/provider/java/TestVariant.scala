package io.cardell.openfeature.provider.java

import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureDecoderError
import io.cardell.openfeature.StructureEncoder
import io.cardell.openfeature.StructureEncoderError
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.FlagValue.{StringValue, IntValue}

case class TestVariant(field: String, intField: Int)

object TestVariant {

  implicit val sd: StructureDecoder[TestVariant] =
    new StructureDecoder[TestVariant] {

      def decodeStructure(
          string: String
      ): Either[StructureDecoderError, TestVariant] = ???

    }

  implicit val se: StructureEncoder[TestVariant] =
    new StructureEncoder[TestVariant] {

      def encodeStructure(
          in: TestVariant
      ): Either[StructureEncoderError, Map[String, FlagValue]] = Right(
        Map(
          "field"    -> StringValue(in.field),
          "intField" -> IntValue(in.intField)
        )
      )

    }

}
