package io.cardell.openfeature

sealed trait EvaluationReason

object EvaluationReason {

  /** The evaluated value is static (no dynamic evaluation). */
  case object Static extends EvaluationReason

  /** The evaluated value fell back to a pre-configured value (no dynamic
    * evaluation occurred or dynamic evaluation yielded no result).
    */
  case object Default extends EvaluationReason

  /** The evaluated value was the result of a dynamic evaluation, such as a rule
    * or specific user-targeting.
    */
  case object TargetingMatch extends EvaluationReason

  /** The evaluated value was the result of pseudorandom assignment. */
  case object Split extends EvaluationReason

  /** The evaluated value was retrieved from cache. */
  case object Cached extends EvaluationReason

  /** The evaluated value was the result of the flag being disabled in the
    * management system.
    */
  case object Disabled extends EvaluationReason

  /** The reason for the evaluated value could not be determined. */
  case object Unknown extends EvaluationReason

  /** The evaluated value is non-authoritative or possibly out of date */
  case object Stale extends EvaluationReason

  /** The evaluated value was the result of an error. */
  case object Error extends EvaluationReason

  /** Any other provider-defined reason */
  case class Other(reason: String) extends EvaluationReason
}
