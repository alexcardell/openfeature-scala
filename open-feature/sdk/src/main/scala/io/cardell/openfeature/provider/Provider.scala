package io.cardell.openfeature.provider

import io.cardell.openfeature.EvaluationContext

trait Provider[F[_]] {
  def metadata: ProviderMetadata

  def getBooleanEvaluation(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ProviderEvaluation[Boolean]]

  def getStringEvaluation(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ProviderEvaluation[String]]

  def getIntEvaluation(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ProviderEvaluation[Int]]

  def getDoubleEvaluation(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ProviderEvaluation[Double]]

  def getObjectEvaluation[A](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ProviderEvaluation[A]]
}
