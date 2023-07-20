package io.cardell.ff4s.flags2

import cats.data.Validated
import cats.data.ValidatedNec
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import Condition.ConditionResult
import Condition.ConditionResultNec
import ConditionValue._
import scala.collection.immutable

sealed trait AttributeValue

case class Condition(key: String, op: Combinator) {
  def run(attributeValue: AttributeValue): ConditionResultNec =
    ???
}

object Condition {
  type ConditionResult = Validated[ConditionFailure, Unit]
  type ConditionResultNec = ValidatedNec[ConditionFailure, Unit]

}

sealed trait ConditionValue
object ConditionValue {
  case class StringValue(s: String) extends ConditionValue
  case class NumberValue(d: Double) extends ConditionValue
  case class NestedCondition(c: Condition) extends ConditionValue
}

sealed trait Combinator
object Combinator {
  case class Or(conds: List[Condition]) extends Combinator
  case class Nor(conds: List[Condition]) extends Combinator
  case class And(conds: List[Condition]) extends Combinator
  case class Not(conds: List[Condition]) extends Combinator

  def or(
      attributeValue: AttributeValue,
      conds: List[Condition]
  ): ConditionResult = {
    val partitioned = conds.map(_.run(attributeValue)).partitionMap {
      case Invalid(e) => Left(e)
      case Valid(_) => Right(())
    }
    val partitionedLeftConcat = partitioned._1.reduce(_ ++ _)

    ???
  }
}

sealed trait ConditionFailure

sealed trait Operator
object Operator {
  case class Eq(expected: ConditionValue) extends Operator
  case class Neq(excluded: ConditionValue) extends Operator
  case class Lt(n: NumberValue) extends Operator
  case class Lte(n: NumberValue) extends Operator
  case class Gt(n: NumberValue) extends Operator
  case class Gte(n: NumberValue) extends Operator
  case class Regex(s: StringValue) extends Operator
  case class In(values: List[ConditionValue]) extends Operator
  case class Nin(values: List[ConditionValue]) extends Operator
  case object Exists extends Operator
  case object Type extends Operator
}
