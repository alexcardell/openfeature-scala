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

