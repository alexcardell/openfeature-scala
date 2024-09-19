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

package io.cardell.openfeature

trait StructureDecoder2[A] {
  def decodeStructure(structure: Structure): Either[StructureDecoderError, A]
}

object StructureDecoder2 {

  def apply[A](implicit sd: StructureDecoder2[A]): StructureDecoder2[A] =
    implicitly

}

trait StructureEncoder[A] {
  def encodeStructure(value: A): Structure
}

object StructureEncoder {

  def apply[A](implicit sd: StructureEncoder[A]): StructureEncoder[A] =
    implicitly

}

trait StructureCodec[A] extends StructureEncoder[A] with StructureDecoder2[A]

object StructureCodec {

  def apply[A](implicit sd: StructureCodec[A]): StructureCodec[A] = implicitly

  def apply[A](
      implicit e: StructureEncoder[A],
      d: StructureDecoder2[A]
  ): StructureCodec[A] =
    new StructureCodec[A] {

      def encodeStructure(value: A): Structure = e.encodeStructure(value)

      def decodeStructure(
          structure: Structure
      ): Either[StructureDecoderError, A] = d.decodeStructure(structure)

    }

}

@deprecated(message = "", since = "")
trait StructureDecoder[A] {
  def decodeStructure(string: String): Either[StructureDecoderError, A]
}

object StructureDecoder {

  def apply[A](implicit sd: StructureDecoder[A]): StructureDecoder[A] =
    implicitly

}
