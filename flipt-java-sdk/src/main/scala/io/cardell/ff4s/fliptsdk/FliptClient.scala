package io.cardell.ff4s.fliptsdk

import cats.effect.kernel.Sync
import cats.syntax.all._
import com.flipt.api.{FliptApiClient => JFliptApiClient}

import io.cardell.ff4s.Flags
import io.cardell.ff4s.FlagsClient

protected class FliptClient[F[_]: Sync](
    private val fliptApiClient: JFliptApiClient,
    namespace: String
) extends FlagsClient[F] {

  def setup(): F[Flags[F]] = {
    Sync[F]
      .blocking(fliptApiClient.flags())
      .map(new FliptFlags[F](_, namespace))
  }

}

object FliptClient {

  def apply[F[_]: Sync](
      url: String,
      token: String,
      namespace: String
  ): FlagsClient[F] = {
    val fliptApiClient = JFliptApiClient.builder().url(url).token(token).build()

    new FliptClient[F](fliptApiClient, namespace)
  }

}
