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

package io.cardell.ff4s.flipt.auth

import org.http4s.headers.Authorization
import cats.effect.kernel.MonadCancelThrow
import org.typelevel.ci._
import org.http4s.client.Client
import org.http4s.Credentials
import org.http4s.AuthScheme

object AuthMiddleware {
  def apply[F[_]: MonadCancelThrow](
      client: Client[F],
      strategy: AuthenticationStrategy
  ): Client[F] = Client { req =>
    val authed = req.withHeaders(authHeader(strategy))
    client.run(authed)
  }

  private def authHeader(
      strategy: AuthenticationStrategy
  ): Authorization = {
    val credentials = strategy match {
      case AuthenticationStrategy.ClientToken(token) =>
        Credentials.Token(AuthScheme.Bearer, token)
      case AuthenticationStrategy.JWT(token) =>
        Credentials.Token(ci"JWT", token)
    }

    Authorization(credentials)
  }
}
