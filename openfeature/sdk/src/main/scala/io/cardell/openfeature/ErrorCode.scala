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
