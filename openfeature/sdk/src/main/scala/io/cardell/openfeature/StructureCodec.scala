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

trait StructureCodec[A] extends StructureEncoder[A] with StructureDecoder[A]

object StructureCodec {

  def apply[A](implicit instance: StructureCodec[A]): StructureCodec[A] =
    instance

  def from[A](
      e: StructureEncoder[A],
      d: StructureDecoder[A]
  ): StructureCodec[A] =
    new StructureCodec[A] {

      def encodeStructure(value: A): Structure = e.encodeStructure(value)

      def decodeStructure(
          structure: Structure
      ): Either[StructureDecoderError, A] = d.decodeStructure(structure)

    }

  implicit def codec[A](
      implicit e: StructureEncoder[A],
      d: StructureDecoder[A]
  ): StructureCodec[A] = StructureCodec.from[A](e, d)

}
