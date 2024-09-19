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

import io.cardell.openfeature.FlagValue.BooleanValue
import io.cardell.openfeature.FlagValue.DoubleValue
import io.cardell.openfeature.FlagValue.IntValue
import io.cardell.openfeature.FlagValue.StringValue
import io.cardell.openfeature.FlagValue.StructureValue

trait HasFlagValue[A] {
  def toFlagValue(a: A): FlagValue
}

object HasFlagValue {
  def apply[A](implicit h: HasFlagValue[A]): HasFlagValue[A] = implicitly

  implicit val boolean: HasFlagValue[Boolean]     = BooleanValue.apply _
  implicit val int: HasFlagValue[Int]             = IntValue.apply _
  implicit val double: HasFlagValue[Double]       = DoubleValue.apply _
  implicit val string: HasFlagValue[String]       = StringValue.apply _
  implicit val structure: HasFlagValue[Structure] = StructureValue.apply _

}
