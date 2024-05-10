package io.cardell.openfeature

import cats.syntax.all._
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.Provider
import cats.Monad

// TODO implement circe
trait ObjectDecoder[A]

case class EvaluationOptions()
object EvaluationOptions {
  val Defaults: EvaluationOptions = EvaluationOptions()
}

trait FeatureClient[F[_]] {

  def providerMetadata: ProviderMetadata

  def evaluationContext: EvaluationContext
  def withEvaluationContext(context: EvaluationContext): FeatureClient[F]

  // def hooks: List[Hook]
  // def withHook(hook: Hook): FeatureClient[F]
  // def withHooks(hooks: List[Hook]): FeatureClient[F]

  def getBooleanValue(flagKey: String, default: Boolean): F[Boolean]
  def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[Boolean]
  def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Boolean]

  def getBooleanDetails(
      flagKey: String,
      default: Boolean
  ): F[EvaluationDetails[Boolean]]
  def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[EvaluationDetails[Boolean]]
  def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Boolean]]

  def getStringValue(flagKey: String, default: String): F[String]
  def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[String]
  def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[String]

  def getStringDetails(
      flagKey: String,
      default: String
  ): F[EvaluationDetails[String]]
  def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[EvaluationDetails[String]]
  def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[String]]

  def getIntValue(flagKey: String, default: Int): F[Int]
  def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext
  ): F[Int]
  def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Int]

  def getDoubleValue(flagKey: String, default: Double): F[Double]
  def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[Double]
  def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Double]

  def getObjectValue[A: ObjectDecoder](flagKey: String, default: A): F[A]
  def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext
  ): F[A]
  def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[A]
}

protected final class OpenFeatureClient[F[_]: Monad](
    provider: Provider[F],
    clientEvaluationContext: EvaluationContext
) extends FeatureClient[F] {

  override def providerMetadata: ProviderMetadata = provider.metadata

  override def evaluationContext: EvaluationContext = clientEvaluationContext

  override def withEvaluationContext(
      context: EvaluationContext
  ): FeatureClient[F] =
    new OpenFeatureClient[F](provider, clientEvaluationContext ++ context)

  // override def hooks: List[Hook] = ???
  // override def withHook(hook: Hook): FeatureClient[F] = ???
  // override def withHooks(hooks: List[Hook]): FeatureClient[F] = ???

  override def getBooleanValue(flagKey: String, default: Boolean): F[Boolean] =
    getBooleanValue(flagKey, default, EvaluationContext.empty)

  override def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[Boolean] =
    getBooleanValue(
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
  ): F[Boolean] =
    getBooleanDetails(flagKey, default, context, options)
      .map(_.value)

  override def getBooleanDetails(
      flagKey: String,
      default: Boolean
  ): F[EvaluationDetails[Boolean]] =
    getBooleanDetails(flagKey, default, EvaluationContext.empty)

  override def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[EvaluationDetails[Boolean]] =
    getBooleanDetails(flagKey, default, context, EvaluationOptions.Defaults)

  override def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Boolean]] =
    provider
      .resolveBooleanValue(
        flagKey,
        default,
        clientEvaluationContext ++ context
      )
      .map(EvaluationDetails[Boolean](flagKey, _))

  override def getStringValue(flagKey: String, default: String): F[String] =
    getStringValue(flagKey, default, EvaluationContext.empty)

  override def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[String] =
    getStringValue(
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
  ): F[String] =
    getStringDetails(flagKey, default, context, EvaluationOptions.Defaults)
      .map(_.value)

  override def getStringDetails(
      flagKey: String,
      default: String
  ): F[EvaluationDetails[String]] =
    getStringDetails(flagKey, default, EvaluationContext.empty)

  override def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[EvaluationDetails[String]] =
    getStringDetails(flagKey, default, context, EvaluationOptions.Defaults)

  override def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[String]] =
    provider
      .resolveStringValue(flagKey, default, clientEvaluationContext ++ context)
      .map(EvaluationDetails[String](flagKey, _))

  override def getIntValue(flagKey: String, default: Int): F[Int] =
    getIntValue(flagKey, default, EvaluationContext.empty)

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
  ): F[Int] =
    provider
      .resolveIntValue(
        flagKey,
        default,
        clientEvaluationContext ++ context
      )
      .map(_.value)

  override def getDoubleValue(flagKey: String, default: Double): F[Double] =
    getDoubleValue(flagKey, default, EvaluationContext.empty)

  override def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[Double] =
    getDoubleValue(flagKey, default, context, EvaluationOptions.Defaults)

  override def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Double] =
    provider
      .resolveDoubleValue(
        flagKey,
        default,
        clientEvaluationContext ++ context
      )
      .map(_.value)

  override def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A
  ): F[A] =
    getObjectValue[A](flagKey, default, EvaluationContext.empty)

  override def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext
  ): F[A] =
    getObjectValue[A](flagKey, default, context, EvaluationOptions.Defaults)

  override def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[A] =
    getObjectValue[A](flagKey, default, clientEvaluationContext ++ context)
}
