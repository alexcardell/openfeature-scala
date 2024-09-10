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

package io.cardell.openfeature.examples

import cats.effect.IO
import org.http4s.Uri
import org.http4s.client.Client

import io.cardell.flipt.FliptApi
import io.cardell.flipt.auth.AuthenticationStrategy
import io.cardell.openfeature.OpenFeature
import io.cardell.openfeature.provider.flipt.FliptProvider

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
