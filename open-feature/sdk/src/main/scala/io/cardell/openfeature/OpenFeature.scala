package io.cardell.openfeature

import cats.Monad
import cats.syntax.all.*
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.provider.Provider

trait OpenFeature[F[_]] {

  /** Create a client using the default provider
    */
  def client: F[FeatureClient[F]]
}

protected[openfeature] class OpenFeatureSdk[F[_]: Monad](provider: Provider[F])
    extends OpenFeature[F] {
  def client: F[FeatureClient[F]] =
    new OpenFeatureClient[F](provider, EvaluationContext.empty).pure[F].widen
}
