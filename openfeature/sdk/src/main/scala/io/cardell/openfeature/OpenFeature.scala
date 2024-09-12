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

trait OpenFeature[F[_]] {

  /** Create a client using the default provider
    */
  def client: F[FeatureClient[F]]
}

object OpenFeature {

  def apply[F[_]: Monad](provider: EvaluationProvider[F]): OpenFeature[F] =
    new OpenFeature[F] {

      def client: F[FeatureClient[F]] =
        new FeatureClientImpl[F](provider, EvaluationContext.empty)
          .pure[F]
          .widen[FeatureClient[F]]

    }

}
