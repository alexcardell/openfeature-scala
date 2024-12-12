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

package io.cardell.openfeature.otel4s

import cats.MonadThrow
import cats.syntax.all._
import org.typelevel.otel4s.Attributes
import org.typelevel.otel4s.trace.StatusCode
import org.typelevel.otel4s.trace.Tracer

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.StructureCodec
import io.cardell.openfeature.otel4s.FeatureFlagAttributes.FeatureFlagKey
import io.cardell.openfeature.otel4s.FeatureFlagAttributes.FeatureFlagProviderName
import io.cardell.openfeature.otel4s.FeatureFlagAttributes.FeatureFlagVariant
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

class TracedProvider[F[_]: Tracer: MonadThrow](
    provider: EvaluationProvider[F]
) extends EvaluationProvider[F] {

  override def metadata: ProviderMetadata = provider.metadata

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] =
    trace("boolean", flagKey)(
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
    trace("string", flagKey)(
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
    trace("int", flagKey)(
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
    trace("double", flagKey)(
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
    trace("structure", flagKey)(
      provider.resolveStructureValue(
        flagKey,
        defaultValue,
        context
      )
    )

  private def flagAttributes(flagKey: String): Attributes = Attributes(
    FeatureFlagKey(flagKey),
    FeatureFlagProviderName(metadata.name)
  )

  private def variantAttributes(maybeVariant: Option[String]): Attributes =
    Attributes.empty.concat(FeatureFlagVariant.maybe(maybeVariant))

  private def trace[A](flagType: String, flagKey: String)(
      fa: F[ResolutionDetails[A]]
  ): F[ResolutionDetails[A]] = Tracer[F]
    .span(s"evaluate-${flagType}-flag")
    .use { span =>
      for {
        _   <- span.addAttributes(flagAttributes(flagKey))
        res <- fa.onError(span.recordException(_))
        _   <- span.addAttributes(variantAttributes(res.variant))
        _   <- span.setStatus(StatusCode.Ok)
      } yield res
    }

}
