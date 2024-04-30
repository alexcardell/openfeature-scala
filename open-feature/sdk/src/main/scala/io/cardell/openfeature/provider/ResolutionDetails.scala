package io.cardell.openfeature.provider

sealed trait ProviderStatus

object ProviderStatus {

  /** The provider has not been initialized. */
  case object NotReady extends ErrorCode

  /** The provider has been initialized, and is able to reliably resolve flag
    * values.
    */
  case object Ready extends ErrorCode

  /** The provider is initialized but is not able to reliably resolve flag
    * values.
    */
  case object Error extends ErrorCode

  /** The provider's cached state is no longer valid and may not be up-to-date
    * with the source of truth.
    */
  case object Stale extends ErrorCode

  /** The provider has entered an irrecoverable error state. */
  case object Fatal extends ErrorCode

  /* The provider is reconciling its state with a context change. */
  // client-side only
  // case object Reconciling extends ErrorCode
}

sealed trait ErrorCode

object ErrorCode {

  /** The value was resolved before the provider was initialized. */
  case object ProviderNotReady extends ErrorCode

  /** The flag could not be found. */
  case object FlagNotFound extends ErrorCode

  /** An error was encountered parsing data, such as a flag configuration. */
  case object ParseError extends ErrorCode

  /** The type of the flag value does not match the expected type. */
  case object TypeMismatch extends ErrorCode

  /** The provider requires a targeting key and one was not provided in the
    * evaluation context.
    */
  case object TargetingKeyMissing extends ErrorCode

  /** The evaluation context does not meet provider requirements. */
  case object InvalidContext extends ErrorCode

  /** The provider has entered an irrecoverable error state. */
  case object ProviderFatal extends ErrorCode

  /** The error was for a reason not enumerated above. */
  case object General extends ErrorCode
}

trait FlagMetadata

sealed trait Reason

object Reason {

  /** The resolved value is static (no dynamic evaluation). */
  case object Static extends Reason

  /** The resolved value fell back to a pre-configured value (no dynamic
    * evaluation occurred or dynamic evaluation yielded no result).
    */
  case object Default extends Reason

  /** The resolved value was the result of a dynamic evaluation, such as a rule
    * or specific user-targeting.
    */
  case object TargetingMatch extends Reason

  /** The resolved value was the result of pseudorandom assignment. */
  case object Split extends Reason

  /** The resolved value was retrieved from cache. */
  case object Cached extends Reason

  /** The resolved value was the result of the flag being disabled in the
    * management system.
    */
  case object Disabled extends Reason

  /** The reason for the resolved value could not be determined. */
  case object Unknown extends Reason

  /** The resolved value is non-authoritative or possibly out of date */
  case object Stale extends Reason

  /** The resolved value was the result of an error. */
  case object Error extends Reason

  /** Any other provider-defined reason */
  case class Other(reason: String) extends Reason
}

trait Resolvable[A] {}

case class ResolutionDetails[A: Resolvable](
    value: A,
    errorCode: Option[ErrorCode],
    errorMessage: Option[String],
    reason: Option[Reason],
    variant: Option[String],
    metadata: Option[FlagMetadata]
)
