package io.cardell.openfeature

import io.cardell.openfeature.provider.ProviderMetadata

trait OpenFeature[F[_]] {
  def client: FeatureClient[F]
  def providerMetadata: ProviderMetadata
}
