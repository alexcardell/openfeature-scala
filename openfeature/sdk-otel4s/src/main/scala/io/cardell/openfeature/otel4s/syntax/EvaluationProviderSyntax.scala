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

package io.cardell.openfeature.otel4s.syntax

import cats.MonadThrow
import org.typelevel.otel4s.trace.Tracer

import io.cardell.openfeature.otel4s.TracedProvider
import io.cardell.openfeature.provider.EvaluationProvider

class EvaluationProviderOps[F[_]: Tracer: MonadThrow](
    provider: EvaluationProvider[F]
) {
  def withTracing: EvaluationProvider[F] = new TracedProvider[F](provider)
}

trait EvaluationProviderSyntax {

  implicit def ops[F[_]: Tracer: MonadThrow](
      provider: EvaluationProvider[F]
  ): EvaluationProviderOps[F] = new EvaluationProviderOps[F](provider)

}
