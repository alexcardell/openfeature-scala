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

import io.cardell.openfeature.ContextValue.BooleanValue
import io.cardell.openfeature.ContextValue.DoubleValue
import io.cardell.openfeature.ContextValue.IntValue
import io.cardell.openfeature.ContextValue.StringValue

sealed trait ContextValue {

  def stringValue: String =
    this match {
      case StringValue(value)  => value
      case BooleanValue(value) => value.toString()
      case IntValue(value)     => value.toString()
      case DoubleValue(value)  => value.toString()
    }

}

object ContextValue {
  case class BooleanValue(value: Boolean) extends ContextValue
  case class StringValue(value: String)   extends ContextValue
  case class IntValue(value: Int)         extends ContextValue
  case class DoubleValue(value: Double)   extends ContextValue
}

case class EvaluationContext(
    targetingKey: Option[String],
    values: Map[String, ContextValue]
) {

  def ++(other: EvaluationContext): EvaluationContext = EvaluationContext(
    other.targetingKey.orElse(targetingKey),
    // TODO check override order in spec
    values ++ other.values
  )

}

object EvaluationContext {
  def empty: EvaluationContext = EvaluationContext(None, Map.empty)
}
