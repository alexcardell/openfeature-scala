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

import cats.MonadThrow
import cats.syntax.all._

import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

protected[openfeature] final class FeatureClientImpl[F[_]](
    provider: EvaluationProvider[F],
    val clientEvaluationContext: EvaluationContext,
    val beforeHooks: List[BeforeHook[F]],
    val errorHooks: List[ErrorHook[F]],
    val afterHooks: List[AfterHook[F]],
    val finallyHooks: List[FinallyHook[F]]
)(implicit M: MonadThrow[F])
    extends FeatureClient[F] {

  override def providerMetadata: ProviderMetadata = provider.metadata

  override def evaluationContext: EvaluationContext = clientEvaluationContext

  override def withEvaluationContext(
      context: EvaluationContext
  ): FeatureClient[F] =
    new FeatureClientImpl[F](
      provider,
      clientEvaluationContext ++ context,
      beforeHooks,
      errorHooks,
      afterHooks,
      finallyHooks
    )

  override def withHook(hook: Hook[F]): FeatureClient[F] =
    hook match {
      case h: BeforeHook[F] =>
        new FeatureClientImpl[F](
          provider,
          clientEvaluationContext,
          beforeHooks.appended(h),
          errorHooks,
          afterHooks,
          finallyHooks
        )
      case h: ErrorHook[F] =>
        new FeatureClientImpl[F](
          provider,
          clientEvaluationContext,
          beforeHooks,
          errorHooks.appended(h),
          afterHooks,
          finallyHooks
        )
      case h: AfterHook[F] =>
        new FeatureClientImpl[F](
          provider,
          clientEvaluationContext,
          beforeHooks,
          errorHooks,
          afterHooks.appended(h),
          finallyHooks
        )
      case h: FinallyHook[F] =>
        new FeatureClientImpl[F](
          provider,
          clientEvaluationContext,
          beforeHooks,
          errorHooks,
          afterHooks,
          finallyHooks.appended(h)
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
    hookedEvaluate(flagKey, default, clientEvaluationContext ++ context) {
      newContext =>
        provider
          .resolveBooleanValue(flagKey, default, newContext)
          .map(EvaluationDetails[Boolean](flagKey, _))
    }

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
    hookedEvaluate(flagKey, default, clientEvaluationContext ++ context) {
      newContext =>
        provider
          .resolveStringValue(flagKey, default, newContext)
          .map(EvaluationDetails[String](flagKey, _))
    }

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
    hookedEvaluate(flagKey, default, clientEvaluationContext ++ context) {
      newContext =>
        provider
          .resolveIntValue(flagKey, default, newContext)
          .map(EvaluationDetails[Int](flagKey, _))
    }

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
    hookedEvaluate(flagKey, default, clientEvaluationContext ++ context) {
      newContext =>
        provider
          .resolveDoubleValue(flagKey, default, newContext)
          .map(EvaluationDetails[Double](flagKey, _))
    }

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
    hookedEvaluate[A](flagKey, default, clientEvaluationContext ++ context) {
      newContext =>
        provider
          .resolveStructureValue[A](flagKey, default, newContext)
          .map(EvaluationDetails(flagKey, _))
    }

  private def hookedEvaluate[A](
      flagKey: String,
      default: A,
      context: EvaluationContext
  )(
      evaluate: (EvaluationContext) => F[EvaluationDetails[A]]
  ): F[EvaluationDetails[A]] = {
    val hookContext = HookContext(
      flagKey,
      clientEvaluationContext ++ context,
      FlagValue(default)
    )

    val hookHints = HookHints.empty

    val run =
      for {
        newContext <- Hooks.runBefore[F](beforeHooks)(hookContext, hookHints)
        evaluation <- evaluate(newContext)
        _ <-
          Hooks.runAfter[F](afterHooks)(
            hookContext.copy(evaluationContext = newContext),
            hookHints
          )
      } yield evaluation

    val runFinally = Hooks.runFinally(finallyHooks)(hookContext, hookHints)
    def runError(e: Throwable) =
      Hooks.runErrors(errorHooks)(hookContext, hookHints, e)

    def errorEvaluation(e: Throwable) = EvaluationDetails(
      flagKey,
      ResolutionDetails.error(default, e)
    )

    run.attempt
      .flatMap {
        case Right(value) => runFinally.as(value)
        case Left(error) =>
          runError(error).attempt.flatMap { case _ =>
            runFinally.as(errorEvaluation(error))
          }
      }

  }

}

object FeatureClientImpl {

  def apply[F[_]: MonadThrow](
      provider: EvaluationProvider[F]
  ): FeatureClientImpl[F] =
    new FeatureClientImpl[F](
      provider = provider,
      clientEvaluationContext = EvaluationContext.empty,
      beforeHooks = List.empty[BeforeHook[F]],
      errorHooks = List.empty[ErrorHook[F]],
      afterHooks = List.empty[AfterHook[F]],
      finallyHooks = List.empty[FinallyHook[F]]
    )

}
