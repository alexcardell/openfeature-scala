package io.cardell.openfeature.provider

import io.cardell.openfeature.EvaluationContext

trait Provider[F[_]] {
  def metadata: ProviderMetadata

  def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[Boolean]
}
