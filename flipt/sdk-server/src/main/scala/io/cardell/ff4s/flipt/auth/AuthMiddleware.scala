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
