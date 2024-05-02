package io.cardell.openfeature

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

