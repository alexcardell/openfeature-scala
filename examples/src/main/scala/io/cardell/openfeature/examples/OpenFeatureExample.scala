package io.cardell.openfeature.examples

import io.cardell.ff4s.flipt.FliptApiImpl
import cats.effect.IO
import org.http4s.client.Client
import io.cardell.ff4s.flipt.FliptApi
import org.http4s.Uri
import io.cardell.ff4s.flipt.auth.AuthenticationStrategy
import io.cardell.openfeature.provider.flipt.FliptProvider
import io.cardell.openfeature.OpenFeature

object OpenFeatureExample {

  def run(client: Client[IO], uri: Uri) = {
    val auth: AuthenticationStrategy = AuthenticationStrategy.ClientToken(
      "dummy"
    )
    val flipt = FliptApi[IO](client, uri, auth)

    val featureSdk = OpenFeature[IO](
      new FliptProvider[IO](flipt, namespace = "example")
    )

    featureSdk.client.flatMap { client => 
      client.getBooleanValue("boolean-flag", false)
    }
  }

}
