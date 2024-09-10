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

package io.cardell.openfeature.provider.flipt

import cats.MonadThrow
import cats.syntax.all._
import scala.util.Success
import scala.util.Try

import io.cardell.ff4s.flipt.EvaluationRequest
import io.cardell.ff4s.flipt.FliptApi
import io.cardell.ff4s.flipt.model.{EvaluationReason => FliptReason}
import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.EvaluationReason
import io.cardell.openfeature.provider.FlagMetadataValue
import io.cardell.openfeature.provider.Provider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

final class FliptProvider[F[_]: MonadThrow](
    flipt: FliptApi[F],
    namespace: String
) extends Provider[F] {

  override def metadata: ProviderMetadata = ProviderMetadata(name = "flipt")

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = {
    val evalContext = mapContext(context)

    val req: EvaluationRequest = EvaluationRequest(
      namespaceKey = namespace,
      flagKey = flagKey,
      entityId = context.targetingKey,
      context = evalContext,
      reference = None
    )

    val resolution = flipt.evaluateBoolean(req).map { evaluation =>
      ResolutionDetails[Boolean](
        value = evaluation.enabled,
        errorCode = None,
        errorMessage = None,
        reason = mapReason(evaluation.reason).some,
        variant = None,
        metadata = None
      )
    }

    def default(t: Throwable) = ResolutionDetails[Boolean](
      value = defaultValue,
      errorCode = Some(ErrorCode.General),
      errorMessage = Some(t.getMessage()),
      reason = Some(EvaluationReason.Error),
      variant = None,
      metadata = None
    )

    resolution.attempt.map {
      case Right(value) => value
      case Left(error)  => default(error)
    }
  }

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = resolvePrimitive[String](
    flagKey,
    defaultValue,
    context
  )

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = resolvePrimitive[Int](
    flagKey,
    defaultValue,
    context
  )

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = resolvePrimitive[Double](
    flagKey,
    defaultValue,
    context
  )

  // override def resolveStructureValue[A: StructureDecoder](
  //     flagKey: String,
  //     defaultValue: A,
  //     context: EvaluationContext
  // ): F[ResolutionDetails[A]] = ???

  private def mapReason(evalReason: FliptReason): EvaluationReason =
    evalReason match {
      case FliptReason.Default      => EvaluationReason.Default
      case FliptReason.FlagDisabled => EvaluationReason.Disabled
      case FliptReason.Match        => EvaluationReason.TargetingMatch
      case FliptReason.Unknown      => EvaluationReason.Unknown
    }

  private def mapContext(context: EvaluationContext): Map[String, String] =
    context.values.map { case (k, v) => (k, v.stringValue) }

  private def resolvePrimitive[A: ResolveTypeConverter](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = {
    val evalContext = mapContext(context)

    val req: EvaluationRequest = EvaluationRequest(
      namespaceKey = namespace,
      flagKey = flagKey,
      entityId = context.targetingKey,
      context = evalContext,
      reference = None
    )

    val stringResolution = flipt.evaluateVariant(req).map { evaluation =>
      ResolutionDetails[String](
        value = evaluation.variantKey,
        errorCode = None,
        errorMessage = None,
        reason = mapReason(evaluation.reason).some,
        variant = Some(evaluation.variantKey),
        metadata = Some(
          Map(
            "variant-attachment" -> FlagMetadataValue
              .StringValue(evaluation.variantAttachment)
          )
        )
      )
    }

    def default(t: Throwable) = ResolutionDetails[A](
      value = defaultValue,
      errorCode = Some(ErrorCode.General),
      errorMessage = Some(t.getMessage()),
      reason = Some(EvaluationReason.Error),
      variant = None,
      metadata = None
    )

    val resolution =
      for {
        res <- stringResolution
        casted <- MonadThrow[F].fromTry(
          ResolveTypeConverter[A].convert(res.value)
        )
      } yield ResolutionDetails[A](
        value = casted,
        errorCode = None,
        errorMessage = None,
        reason = res.reason,
        variant = res.variant,
        metadata = res.metadata
      )

    resolution.attempt.map {
      case Right(value) => value
      case Left(error)  => default(error)
    }
  }

}

protected sealed trait ResolveTypeConverter[A] {
  def convert(s: String): Try[A]
}

protected object ResolveTypeConverter {

  def apply[A](implicit r: ResolveTypeConverter[A]): ResolveTypeConverter[A] =
    implicitly

  implicit val string: ResolveTypeConverter[String] =
    new ResolveTypeConverter[String] {
      def convert(s: String): Try[String] = Success(s)
    }

  implicit val int: ResolveTypeConverter[Int] =
    new ResolveTypeConverter[Int] {
      def convert(s: String): Try[Int] = Try(s.toInt)
    }

  implicit val double: ResolveTypeConverter[Double] =
    new ResolveTypeConverter[Double] {
      def convert(s: String): Try[Double] = Try(s.toDouble)
    }

}
