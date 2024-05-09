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

package io.cardell.ff4s.flipt

import cats.effect.Concurrent
import cats.syntax.all.*
import io.cardell.ff4s.flipt.model.AttachmentDecodingError
import io.cardell.ff4s.flipt.model.BatchEvaluationRequest
import io.cardell.ff4s.flipt.model.BatchEvaluationResponse
import io.cardell.ff4s.flipt.model.BooleanEvaluationResponse
import io.cardell.ff4s.flipt.model.StructuredVariantEvaluationResponse
import io.cardell.ff4s.flipt.model.VariantEvaluationResponse
import io.circe.Decoder
import org.http4s.Method
import org.http4s.Request
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client

protected[flipt] class FliptApiImpl[F[_]: Concurrent](
    client: Client[F],
    baseUri: Uri
) extends FliptApi[F] {

  private val evalUri = baseUri / "evaluate" / "v1"

  override def evaluateBoolean(
      request: EvaluationRequest
  ): F[BooleanEvaluationResponse] = {
    val req = Request[F](
      method = Method.POST,
      uri = evalUri / "boolean"
    ).withEntity(request)

    client.expect[BooleanEvaluationResponse](req)
  }

  override def evaluateVariant(
      request: EvaluationRequest
  ): F[VariantEvaluationResponse] = {
    val req = Request[F](
      method = Method.POST,
      uri = evalUri / "variant"
    ).withEntity(request)

    client.expect[VariantEvaluationResponse](req)
  }

  override def evaluateStructuredVariant[A: Decoder](
      request: EvaluationRequest
  ): F[
    Either[AttachmentDecodingError, StructuredVariantEvaluationResponse[A]]
  ] = {
    evaluateVariant(request).map(StructuredVariantEvaluationResponse[A](_))
  }

  override def evaluateBatch(
      request: BatchEvaluationRequest
  ): F[BatchEvaluationResponse] = {
    val req = Request[F](
      method = Method.POST,
      uri = evalUri / "batch"
    ).withEntity(request)

    client.expect[BatchEvaluationResponse](req)
  }

}
