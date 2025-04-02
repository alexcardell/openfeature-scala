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

package io.cardell.openfeature.log4cats

import cats.MonadThrow
import cats.syntax.all._
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.StructuredLogger

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.StructureCodec
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

class LoggedEvaluationProvider[F[_]: MonadThrow](
    provider: EvaluationProvider[F]
)(implicit logger: StructuredLogger[F])
    extends EvaluationProvider[F] {

  override def metadata: ProviderMetadata = provider.metadata

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] =
    logEvaluation("boolean", flagKey)(
      provider.resolveBooleanValue(
        flagKey,
        defaultValue,
        context
      )
    )

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] =
    logEvaluation("string", flagKey)(
      provider.resolveStringValue(
        flagKey,
        defaultValue,
        context
      )
    )

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] =
    logEvaluation("int", flagKey)(
      provider.resolveIntValue(
        flagKey,
        defaultValue,
        context
      )
    )

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] =
    logEvaluation("double", flagKey)(
      provider.resolveDoubleValue(
        flagKey,
        defaultValue,
        context
      )
    )

  override def resolveStructureValue[A: StructureCodec](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] =
    logEvaluation("structure", flagKey)(
      provider.resolveStructureValue(
        flagKey,
        defaultValue,
        context
      )
    )

  private def flagAttributes(
      flagKey: String
  ): Map[String, String] = Map(
    "event"                      -> "feature_flag.evaluation",
    "feature_flag.key"           -> flagKey,
    "feature_flag.provider_name" -> metadata.name
  )

  private def variantAttributes(
      maybeVariant: Option[String]
  ): Option[(String, String)] =
    maybeVariant match {
      case Some(value) => Some("feature_flag.result.variant" -> value)
      case None        => None
    }

  private def logEvaluation[A](flagType: String, flagKey: String)(
      fa: F[ResolutionDetails[A]]
  ): F[ResolutionDetails[A]] = {
    val attrs = flagAttributes(flagKey)

    for {
      res <- fa.onError(logError(flagKey, flagType, attrs, _))
      variantAttrs = variantAttributes(res.variant).fold(attrs)(attrs + _)
      _ <- logger.info(variantAttrs)(s"Evaluated ${flagType} flag ${flagKey}")
    } yield res
  }

  private def logError(
      flagType: String,
      flagKey: String,
      attrs: Map[String, String],
      t: Throwable
  ) =
    logger.error(attrs, t)(
      s"Error occurred evaluating ${flagType} flag ${flagKey}"
    )

}

object LoggedEvaluationProvider {

  def apply[F[_]: MonadThrow: LoggerFactory](
      provider: EvaluationProvider[F]
  ): LoggedEvaluationProvider[F] = {
    implicit val logger: StructuredLogger[F] = LoggerFactory[F].getLogger
    new LoggedEvaluationProvider[F](provider)
  }

  def make[F[_]: MonadThrow: LoggerFactory](
      provider: EvaluationProvider[F]
  ): F[LoggedEvaluationProvider[F]] = LoggerFactory[F].create.map {
    implicit logger =>
      new LoggedEvaluationProvider[F](provider)
  }

}
