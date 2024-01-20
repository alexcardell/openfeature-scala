package io.cardell.ff4s.flipt

import org.http4s.circe.CirceEntityCodec._
import cats.effect.Concurrent
import org.http4s.client.Client
import org.http4s.Request
import org.http4s.Method
import org.http4s.Uri
import io.cardell.ff4s.flipt.model.BooleanEvaluationResponse
import io.cardell.ff4s.flipt.model.VariantEvaluationResponse
import io.cardell.ff4s.flipt.model.BatchEvaluationRequest
import io.cardell.ff4s.flipt.model.BatchEvaluationResponse

protected[flipt] class FliptApiImpl[F[_]: Concurrent](
    client: Client[F],
    baseUri: Uri
) extends FliptApi[F] {

  override def evaluateBoolean(
      request: EvaluationRequest
  ): F[BooleanEvaluationResponse] = {
    val req = Request[F](
      method = Method.POST,
      uri = baseUri / "evaluate" / "v1" / "boolean"
    ).withEntity(request)

    client.expect[BooleanEvaluationResponse](req)
  }
  override def evaluateVariant(
      request: EvaluationRequest
  ): F[VariantEvaluationResponse] = {
    val req = Request[F](
      method = Method.POST,
      uri = baseUri / "evaluate" / "v1" / "variant"
    ).withEntity(request)

    client.expect[VariantEvaluationResponse](req)
  }

  override def evaluateBatch(
      request: BatchEvaluationRequest
  ): F[BatchEvaluationResponse] = {
    val req = Request[F](
      method = Method.POST,
      uri = baseUri / "evaluate" / "v1" / "batch"
    ).withEntity(request)

    client.expect[BatchEvaluationResponse](req)
  }

}
