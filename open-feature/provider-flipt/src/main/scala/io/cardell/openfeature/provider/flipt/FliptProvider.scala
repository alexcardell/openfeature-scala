package io.cardell.openfeature.provider.flipt

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.provider.Provider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails
import io.cardell.ff4s.flipt.FliptApi
import io.cardell.ff4s.flipt.EvaluationRequest
import io.cardell.ff4s.flipt.model.{EvaluationReason => FliptReason}
import io.cardell.openfeature.EvaluationReason
import cats.Monad
import cats.syntax.all.*

class FliptProvider[F[_]: Monad](flipt: FliptApi[F], namespace: String)
    extends Provider[F] {

  override def metadata: ProviderMetadata = ???

  private def mapReason(evalReason: FliptReason): EvaluationReason =
    evalReason match {
      case FliptReason.Default      => EvaluationReason.Default
      case FliptReason.FlagDisabled => EvaluationReason.Disabled
      case FliptReason.Match        => EvaluationReason.TargetingMatch
      case FliptReason.Unknown      => EvaluationReason.Unknown
    }

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = {
    val evalContext = context.values.map { case (k, v) => (k, v.stringValue) }

    val req: EvaluationRequest = EvaluationRequest(
      namespaceKey = namespace,
      flagKey = flagKey,
      entityId = context.targetingKey,
      context = evalContext,
      reference = None
    )

    flipt.evaluateBoolean(req).map { evaluation =>
      ResolutionDetails[Boolean](
        value = evaluation.enabled,
        errorCode = None,
        errorMessage = None,
        reason = mapReason(evaluation.reason).some,
        variant = None,
        metadata = None
      )
    }
  }

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = ???

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = ???

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = ???

  override def resolveStructureValue[A: StructureDecoder](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = ???

}
