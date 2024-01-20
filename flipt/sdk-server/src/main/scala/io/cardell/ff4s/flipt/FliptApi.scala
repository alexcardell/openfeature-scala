package io.cardell.ff4s.flipt

import cats.effect.Concurrent
import org.http4s.client.Client
import io.cardell.ff4s.flipt.auth.AuthenticationStrategy
import io.cardell.ff4s.flipt.auth.AuthMiddleware
import org.http4s.Uri
import io.cardell.ff4s.flipt.model.BooleanEvaluationResponse
import io.cardell.ff4s.flipt.model.VariantEvaluationResponse
import io.cardell.ff4s.flipt.model.BatchEvaluationRequest
import io.cardell.ff4s.flipt.model.BatchEvaluationResponse

trait FliptApi[F[_]] {
  def evaluateBoolean(request: EvaluationRequest): F[BooleanEvaluationResponse]
  def evaluateVariant(request: EvaluationRequest): F[VariantEvaluationResponse]
  def evaluateBatch(request: BatchEvaluationRequest): F[BatchEvaluationResponse]
}

object FliptApi {
  def apply[F[_]: Concurrent](
      client: Client[F],
      uri: Uri,
      strategy: AuthenticationStrategy
  ): FliptApi[F] =
    new FliptApiImpl[F](AuthMiddleware(client, strategy), uri)
}
