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

case class ClientMetadata()

trait Hook

case class EvaluationDetails[A](value: A)

trait FeatureClient[F[_]] {

  def metadata: ClientMetadata
  def providerMetadata: ProviderMetadata

  def evaluationContext: EvaluationContext
  def withEvaluationContext(context: EvaluationContext): FeatureClient[F]

  def hooks: List[Hook]
  def withHook(hook: Hook): FeatureClient[F]
  def withHooks(hooks: List[Hook]): FeatureClient[F]

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

  override def metadata: ClientMetadata = ???

  override def providerMetadata: ProviderMetadata = provider.metadata

  override def evaluationContext: EvaluationContext = clientEvaluationContext

  override def withEvaluationContext(
      context: EvaluationContext
  ): FeatureClient[F] =
    new OpenFeatureClient[F](provider, clientEvaluationContext ++ context)

  override def hooks: List[Hook] = ???

  override def withHook(hook: Hook): FeatureClient[F] = ???

  override def withHooks(hooks: List[Hook]): FeatureClient[F] = ???

  override def getBooleanValue(flagKey: String, default: Boolean): F[Boolean] =
    provider
      .getBooleanEvaluation(flagKey, default, EvaluationContext.empty)
      .map(_.value)

  override def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[Boolean] = provider
    .getBooleanEvaluation(flagKey, default, evaluationContext ++ context)
    .map(_.value)

  override def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Boolean] = ???

  override def getStringValue(flagKey: String, default: String): F[String] = ???

  override def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[String] = ???

  override def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[String] = ???

  override def getIntValue(flagKey: String, default: Int): F[Int] = ???

  override def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext
  ): F[Int] = ???

  override def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Int] = ???

  override def getDoubleValue(flagKey: String, default: Double): F[Double] = ???

  override def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[Double] = ???

  override def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Double] = ???

  override def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A
  ): F[A] = ???

  override def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext
  ): F[A] = ???

  override def getObjectValue[A: ObjectDecoder](
      flagKey: String,
      default: A,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[A] = ???
}
