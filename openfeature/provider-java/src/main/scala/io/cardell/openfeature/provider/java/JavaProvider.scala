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

package io.cardell.openfeature.provider.java

import cats.effect.kernel.Resource
import cats.effect.kernel.Sync
import cats.syntax.all._
import dev.openfeature.sdk.{ErrorCode => JErrorCode}
import dev.openfeature.sdk.{FeatureProvider => JProvider}
import dev.openfeature.sdk.{ImmutableContext => JContext}
import dev.openfeature.sdk.{ProviderEvaluation => JEvaluation}
import dev.openfeature.sdk.{Reason => JReason}
import dev.openfeature.sdk.{Value => JValue}
import scala.jdk.CollectionConverters.MapHasAsJava
import scala.util.Try

import io.cardell.openfeature.ContextValue
import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.EvaluationReason
import io.cardell.openfeature.StructureCodec
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

private[java] class JavaProvider[F[_]: Sync](jProvider: JProvider)
    extends EvaluationProvider[F] {

  private def toJContext(context: EvaluationContext): JContext = {
    val jValues =
      context.values.map {
        case (k, ContextValue.BooleanValue(b)) => (k, new JValue(b))
        case (k, ContextValue.IntValue(i))     => (k, new JValue(i))
        case (k, ContextValue.DoubleValue(d))  => (k, new JValue(d))
        case (k, ContextValue.StringValue(s))  => (k, new JValue(s))
      }.asJava

    context.targetingKey match {
      case Some(targetingKey) => new JContext(targetingKey, jValues)
      case None               => new JContext(jValues)
    }
  }

  private def mapErrorCode(ec: JErrorCode): ErrorCode =
    ec match {
      case JErrorCode.INVALID_CONTEXT       => ErrorCode.InvalidContext
      case JErrorCode.FLAG_NOT_FOUND        => ErrorCode.FlagNotFound
      case JErrorCode.PARSE_ERROR           => ErrorCode.ParseError
      case JErrorCode.GENERAL               => ErrorCode.General
      case JErrorCode.PROVIDER_NOT_READY    => ErrorCode.ProviderNotReady
      case JErrorCode.TARGETING_KEY_MISSING => ErrorCode.TargetingKeyMissing
      case JErrorCode.TYPE_MISMATCH         => ErrorCode.TypeMismatch
    }

  private def mapReason(reason: JReason): EvaluationReason =
    reason match {
      case JReason.TARGETING_MATCH => EvaluationReason.TargetingMatch
      case JReason.STATIC          => EvaluationReason.Static
      case JReason.DISABLED        => EvaluationReason.Disabled
      case JReason.ERROR           => EvaluationReason.Error
      case JReason.CACHED          => EvaluationReason.Cached
      case JReason.UNKNOWN         => EvaluationReason.Unknown
      case JReason.DEFAULT         => EvaluationReason.Default
      case JReason.SPLIT           => EvaluationReason.Split
    }

  private def toResolutionDetails[A, B](
      evaluation: JEvaluation[A],
      transformer: A => B
  ): ResolutionDetails[B] = {
    val evaluationReason = Option(evaluation.getReason()).map { jReason =>
      Try(JReason.valueOf(jReason)).toEither match {
        case Right(r) => mapReason(r)
        case Left(_)  => EvaluationReason.Unknown
      }
    }
    ResolutionDetails[B](
      value = transformer(evaluation.getValue()),
      errorCode = Option(evaluation.getErrorCode()).map(mapErrorCode),
      errorMessage = Option(evaluation.getErrorMessage()),
      reason = evaluationReason,
      variant = Option(evaluation.getVariant()),
      metadata = None
    )
  }

  override def metadata: ProviderMetadata = {
    lazy val default = ProviderMetadata(name = "java-default")

    val providerMetadata =
      for {
        metadata <- Option(jProvider.getMetadata())
        name     <- Option(metadata.getName())
      } yield ProviderMetadata(name = name)

    providerMetadata.getOrElse(default)
  }

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = Sync[F]
    .blocking(
      jProvider.getBooleanEvaluation(flagKey, defaultValue, toJContext(context))
    )
    .map(
      toResolutionDetails[java.lang.Boolean, Boolean](
        _,
        (b: java.lang.Boolean) => b.booleanValue()
      )
    )

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = Sync[F]
    .blocking(
      jProvider.getStringEvaluation(flagKey, defaultValue, toJContext(context))
    )
    .map(toResolutionDetails[java.lang.String, String](_, identity))

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = Sync[F]
    .blocking(
      jProvider.getIntegerEvaluation(flagKey, defaultValue, toJContext(context))
    )
    .map(toResolutionDetails[java.lang.Integer, Int](_, identity))

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = Sync[F]
    .blocking(
      jProvider.getDoubleEvaluation(flagKey, defaultValue, toJContext(context))
    )
    .map(toResolutionDetails[java.lang.Double, Double](_, identity))

  override def resolveStructureValue[A: StructureCodec](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = {
    // TODO figure out how to get any case class to be a Value
    // is this a use case for json
    val value = new JValue(defaultValue)

    Sync[F]
      .blocking(
        jProvider.getObjectEvaluation(
          flagKey,
          value,
          toJContext(context)
        )
      )
      .map(toResolutionDetails[JValue, A](_, _.asObject().asInstanceOf[A]))

    ???
  }

}

object JavaProvider {

  def resource[F[_]: Sync](
      provider: JProvider
  ): Resource[F, JavaProvider[F]] = {
    val resource =
      Resource.make[F, JProvider](
        Sync[F].blocking(provider.initialize(new JContext())).as(provider)
      )(provider => Sync[F].blocking(provider.shutdown()))

    resource.map(new JavaProvider[F](_))
  }

}
