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

package io.cardell.openfeature.provider

sealed trait ProviderStatus

object ProviderStatus {

  /** The provider has not been initialized. */
  case object NotReady extends ProviderStatus

  /** The provider has been initialized, and is able to reliably resolve flag
    * values.
    */
  case object Ready extends ProviderStatus

  /** The provider is initialized but is not able to reliably resolve flag
    * values.
    */
  case object Error extends ProviderStatus

  /** The provider's cached state is no longer valid and may not be up-to-date
    * with the source of truth.
    */
  case object Stale extends ProviderStatus

  /** The provider has entered an irrecoverable error state. */
  case object Fatal extends ProviderStatus

  /* The provider is reconciling its state with a context change. */
  // client-side only
  // case object Reconciling extends ProviderStatus
}
