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

package io.cardell.openfeature

import cats.Monad
import cats.syntax.all._

import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata

protected[openfeature] final class FeatureClientImpl[F[_]](
    provider: EvaluationProvider[F],
    val clientEvaluationContext: EvaluationContext,
    val beforeHooks: List[BeforeHook[F]]
)(implicit M: Monad[F])
    extends FeatureClient[F] {

  override def providerMetadata: ProviderMetadata = provider.metadata

  override def evaluationContext: EvaluationContext = clientEvaluationContext

  override def withEvaluationContext(
      context: EvaluationContext
  ): FeatureClient[F] =
    new FeatureClientImpl[F](
      provider,
      clientEvaluationContext ++ context,
      beforeHooks
    )

  override def withHook(hook: Hook[F]): FeatureClient[F] =
    hook match {
      case h: BeforeHook[F] =>
        new FeatureClientImpl[F](
          provider,
          clientEvaluationContext,
          beforeHooks.appended(h)
        )
    }

  override def getBooleanValue(flagKey: String, default: Boolean): F[Boolean] =
    getBooleanValue(flagKey, default, EvaluationContext.empty)

  override def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[Boolean] = getBooleanValue(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions // TODO handle options
  ): F[Boolean] = getBooleanDetails(flagKey, default, context, options)
    .map(_.value)

  override def getBooleanDetails(
      flagKey: String,
      default: Boolean
  ): F[EvaluationDetails[Boolean]] = getBooleanDetails(
    flagKey,
    default,
    EvaluationContext.empty
  )

  override def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[EvaluationDetails[Boolean]] = getBooleanDetails(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Boolean]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(
            flagKey,
            clientEvaluationContext ++ context,
            FlagValue(default)
          ),
          HookHints.empty
        )
      resolution <- provider
        .resolveBooleanValue(
          flagKey,
          default,
          newContext
        )
    } yield EvaluationDetails[Boolean](flagKey, resolution)

  override def getStringValue(flagKey: String, default: String): F[String] =
    getStringValue(flagKey, default, EvaluationContext.empty)

  override def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[String] = getStringValue(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[String] = getStringDetails(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  ).map(_.value)

  override def getStringDetails(
      flagKey: String,
      default: String
  ): F[EvaluationDetails[String]] = getStringDetails(
    flagKey,
    default,
    EvaluationContext.empty
  )

  override def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[EvaluationDetails[String]] = getStringDetails(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[String]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(
            flagKey,
            clientEvaluationContext ++ context,
            FlagValue(default)
          ),
          HookHints.empty
        )
      resolution <- provider.resolveStringValue(flagKey, default, newContext)
    } yield EvaluationDetails[String](flagKey, resolution)

  override def getIntValue(flagKey: String, default: Int): F[Int] = getIntValue(
    flagKey,
    default,
    EvaluationContext.empty
  )

  override def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext
  ): F[Int] = getIntValue(flagKey, default, context, EvaluationOptions.Defaults)

  override def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Int] = getIntDetails(flagKey, default, context, options)
    .map(_.value)

  override def getIntDetails(
      flagKey: String,
      default: Int
  ): F[EvaluationDetails[Int]] = getIntDetails(
    flagKey,
    default,
    EvaluationContext.empty
  )

  override def getIntDetails(
      flagKey: String,
      default: Int,
      context: EvaluationContext
  ): F[EvaluationDetails[Int]] = getIntDetails(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getIntDetails(
      flagKey: String,
      default: Int,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Int]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(
            flagKey,
            clientEvaluationContext ++ context,
            FlagValue(default)
          ),
          HookHints.empty
        )
      resolution <- provider.resolveIntValue(flagKey, default, newContext)
    } yield EvaluationDetails[Int](flagKey, resolution)

  override def getDoubleValue(flagKey: String, default: Double): F[Double] =
    getDoubleValue(flagKey, default, EvaluationContext.empty)

  override def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[Double] = getDoubleValue(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Double] = getDoubleDetails(flagKey, default, context).map(_.value)

  override def getDoubleDetails(
      flagKey: String,
      default: Double
  ): F[EvaluationDetails[Double]] = getDoubleDetails(
    flagKey,
    default,
    EvaluationContext.empty
  )

  override def getDoubleDetails(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[EvaluationDetails[Double]] = getDoubleDetails(
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getDoubleDetails(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Double]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(
            flagKey,
            clientEvaluationContext ++ context,
            FlagValue(default)
          ),
          HookHints.empty
        )
      resolution <- provider.resolveDoubleValue(flagKey, default, newContext)
    } yield EvaluationDetails[Double](flagKey, resolution)

  override def getStructureValue[A: StructureDecoder](
      flagKey: String,
      default: A
  ): F[A] = getStructureValue[A](flagKey, default, EvaluationContext.empty)

  override def getStructureValue[A: StructureDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext
  ): F[A] = getStructureValue[A](
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getStructureValue[A: StructureDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[A] = getStructureDetails[A](flagKey, default, context)
    .map(_.value)

  override def getStructureDetails[A: StructureDecoder](
      flagKey: String,
      default: A
  ): F[EvaluationDetails[A]] = getStructureDetails[A](
    flagKey,
    default,
    EvaluationContext.empty
  )

  override def getStructureDetails[A: StructureDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext
  ): F[EvaluationDetails[A]] = getStructureDetails[A](
    flagKey,
    default,
    context,
    EvaluationOptions.Defaults
  )

  override def getStructureDetails[A: StructureDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[A]] =
    for {
      newContext <-
        Hooks.runBefore[F](beforeHooks)(
          HookContext(
            flagKey,
            clientEvaluationContext ++ context,
            FlagValue(default)
          ),
          HookHints.empty
        )
      resolution <- provider
        .resolveStructureValue[A](flagKey, default, newContext)
    } yield EvaluationDetails(flagKey, resolution)

}

object FeatureClientImpl {

  def apply[F[_]: Monad](
      provider: EvaluationProvider[F]
  ): FeatureClientImpl[F] =
    new FeatureClientImpl[F](
      provider = provider,
      clientEvaluationContext = EvaluationContext.empty,
      beforeHooks = List.empty[BeforeHook[F]]
    )

}
