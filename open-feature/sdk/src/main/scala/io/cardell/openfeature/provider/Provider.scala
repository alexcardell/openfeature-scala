package io.cardell.openfeature.provider

import io.cardell.openfeature.EvaluationContext

trait Provider[F[_]] {
  def metadata: ProviderMetadata

  def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]]

  def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]]

  def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]]

  def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]]

  def resolveStructureValue[A](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]]
}
