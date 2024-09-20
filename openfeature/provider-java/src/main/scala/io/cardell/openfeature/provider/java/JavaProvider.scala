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
import dev.openfeature.sdk.{FeatureProvider => JProvider}
import dev.openfeature.sdk.{ImmutableContext => JContext}
import dev.openfeature.sdk.{Reason => JReason}
import dev.openfeature.sdk.{Value => JValue}
import java.lang.{Boolean => JBoolean}
import java.lang.{Double => JDouble}

import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.StructureCodec
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.StructureEncoder
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

final class JavaProvider[F[_]: Sync] private (provider: JProvider)
    extends EvaluationProvider[F] {

  override def metadata: ProviderMetadata = {
    val name =
      for {
        m <- Option(provider.getMetadata())
        n <- Option(m.getName())
      } yield n

    ProviderMetadata(name = name.getOrElse("unknown-java-provider"))
  }

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = {
    val jContext = ToJavaConverters.evaluationContext(context)
    val providerResolution = Sync[F].blocking(
      provider.getBooleanEvaluation(flagKey, defaultValue, jContext)
    )
    providerResolution.map(
      FromJavaConverters.evaluation[JBoolean, Boolean](_)(_.booleanValue())
    )
  }

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = {
    val jContext = ToJavaConverters.evaluationContext(context)

    val providerResolution = Sync[F].blocking(
      provider.getStringEvaluation(flagKey, defaultValue, jContext)
    )

    providerResolution.map(FromJavaConverters.evaluation[String](_))
  }

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = {
    val jContext = ToJavaConverters.evaluationContext(context)

    val providerResolution = Sync[F].blocking(
      provider.getIntegerEvaluation(flagKey, defaultValue, jContext)
    )

    providerResolution.map(
      FromJavaConverters.evaluation[Integer, Int](_)(_.toInt)
    )

  }

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = {
    val jContext = ToJavaConverters.evaluationContext(context)

    val providerResolution = Sync[F].blocking(
      provider.getDoubleEvaluation(flagKey, defaultValue, jContext)
    )

    providerResolution.map(
      FromJavaConverters.evaluation[JDouble, Double](_)(_.toDouble)
    )
  }

  override def resolveStructureValue[A: StructureCodec](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = {
    val jContext         = ToJavaConverters.evaluationContext(context)
    val defaultStructure = StructureEncoder[A].encodeStructure(defaultValue)
    val jStructure       = ToJavaConverters.structure(defaultStructure)

    val providerResolution = Sync[F].blocking(
      provider.getObjectEvaluation(flagKey, new JValue(jStructure), jContext)
    )

    providerResolution.map { case jResolution =>
      val flagValue = FromJavaConverters.value(jResolution.getValue())

      val maybeStructure =
        flagValue match {
          case FlagValue.StructureValue(structure) => Right(structure)
          // StructureDecoder[A].decodeStructure(structure)
          case otherType => Left(otherType.valueType)

        }

      maybeStructure match {
        case Left(wrongType) =>
          ResolutionDetails.error[A](
            defaultValue,
            s"Wrong type, received ${wrongType}",
            ErrorCode.TypeMismatch
          )
        case Right(struct) =>
          val maybeValue = StructureDecoder[A].decodeStructure(struct)

          maybeValue match {
            case Left(error) =>
              ResolutionDetails
                .fromThrowable[A](
                  defaultValue,
                  error.cause,
                  ErrorCode.TypeMismatch
                )
            case Right(value) =>
              ResolutionDetails[A](
                value = value,
                reason = Option(jResolution.getReason())
                  .map(JReason.valueOf)
                  .map(FromJavaConverters.reason),
                errorCode = Option(jResolution.getErrorCode())
                  .map(FromJavaConverters.errorCode),
                errorMessage = Option(jResolution.getErrorMessage()),
                variant = Option(jResolution.getVariant()),
                metadata = None
              )
          }
      }
    }
  }

}

object JavaProvider {

  def resource[F[_]: Sync](provider: JProvider): Resource[F, JavaProvider[F]] =
    Resource
      .make[F, JProvider] {
        Sync[F].blocking(provider.initialize(new JContext())).as(provider)
      } { provider =>
        Sync[F].blocking(provider.shutdown())
      }
      .map(new JavaProvider[F](_))

}
