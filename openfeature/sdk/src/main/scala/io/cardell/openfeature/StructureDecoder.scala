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

trait StructureDecoder[A] {
  def decodeStructure(string: String): Either[StructureDecoderError, A]
}

object StructureDecoder {

  def apply[A](implicit sd: StructureDecoder[A]): StructureDecoder[A] =
    implicitly

}

trait StructureEncoder[A] {

  def encodeStructure(
      in: A
  ): Either[StructureEncoderError, Map[String, FlagValue]]

}

trait StructureCodec[A] extends StructureDecoder[A] with StructureEncoder[A]

object StructureCodec {

  implicit def codec[A](
      implicit d: StructureDecoder[A],
      e: StructureEncoder[A]
  ): StructureCodec[A] =
    new StructureCodec[A] {

      def encodeStructure(
          in: A
      ): Either[StructureEncoderError, Map[String, FlagValue]] = e
        .encodeStructure(in)

      def decodeStructure(s: String): Either[StructureDecoderError, A] = d
        .decodeStructure(s)

    }

  def apply[A](implicit sc: StructureCodec[A]): StructureCodec[A] = implicitly
}
