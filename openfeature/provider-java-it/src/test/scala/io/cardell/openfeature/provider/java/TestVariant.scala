package io.cardell.openfeature.provider.java

import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureDecoderError

case class TestVariant(field: String, intField: Int)

object TestVariant {

  implicit val sd: StructureDecoder[TestVariant] =
    new StructureDecoder[TestVariant] {

      def decodeStructure(
          string: String
      ): Either[StructureDecoderError, TestVariant] = ???

    }

}

