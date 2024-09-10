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

import cats.effect.IO
import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.DockerComposeContainer
import com.dimafeng.testcontainers.ExposedService
import com.dimafeng.testcontainers.munit.TestContainerForAll
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import java.io.File
import munit.CatsEffectSuite
import org.http4s.Uri
import org.http4s.ember.client.EmberClientBuilder
import org.testcontainers.containers.wait.strategy.Wait

import io.cardell.ff4s.flipt.auth.AuthenticationStrategy
import com.dimafeng.testcontainers.GenericContainer
import org.testcontainers.containers.BindMode

class FliptApiImplItTest extends CatsEffectSuite with TestContainerForAll {

  override val containerDef: ContainerDef = GenericContainer.Def(
    "flipt/flipt:v1.49.2",
    Seq(8080),
    env = Map(
      "FLIPT_STORAGE_TYPE"       -> "local",
      "FLIPT_STORAGE_LOCAL_PATH" -> "/config"
    ),
    fileSystemBind = Seq(
      GenericContainer.FileSystemBind(
        "./docker/flipt/features.yaml",
        "/config/features.yaml",
        BindMode.READ_ONLY
      )
    ),
    waitStrategy = Wait.forLogMessage("^UI: http.*", 1)
  )

  def api(containers: Containers): Resource[IO, FliptApi[IO]] = {
    val flipt = containers.asInstanceOf[GenericContainer]

    val url = Uri
      .fromString(
        s"http://${flipt.host}:${flipt.mappedPort(8080)}"
      )
      .getOrElse(throw new Exception("invalid url"))

    EmberClientBuilder
      .default[IO]
      .build
      .map(client =>
        FliptApi[IO](client, url, AuthenticationStrategy.ClientToken("token"))
      )
  }

  test("can fetch boolean flag") {
    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.evaluateBoolean(
            EvaluationRequest(
              "default",
              "boolean-flag-1",
              None,
              Map.empty,
              None
            )
          )
        } yield assertEquals(res.enabled, true)
      }
    }
  }

  test("receives variant match when in segment rule") {
    withContainers { containers =>
      api(containers).use { flipt =>
        val segmentContext = Map("test-property" -> "matched-property-value")
        for {
          res <- flipt.evaluateVariant(
            EvaluationRequest(
              "default",
              "json-variant-flag-1",
              None,
              segmentContext,
              None
            )
          )
        } yield assertEquals(res.`match`, true)
      }
    }
  }

  test("receives no variant match when not in segment rule") {
    withContainers { containers =>
      api(containers).use { flipt =>
        val segmentContext = Map("test-property" -> "unmatched-property-value")
        for {
          res <- flipt.evaluateVariant(
            EvaluationRequest(
              "default",
              "json-variant-flag-1",
              None,
              segmentContext,
              None
            )
          )
        } yield assertEquals(res.`match`, false)
      }
    }
  }

  case class TestVariant(field: String, intField: Int)

  object TestVariant {
    implicit val decoder: Decoder[TestVariant] = deriveDecoder
  }

  test("can deserialise variant match") {
    withContainers { containers =>
      api(containers).use { flipt =>
        val segmentContext = Map("test-property" -> "matched-property-value")

        for {
          res <- flipt.evaluateStructuredVariant[TestVariant](
            EvaluationRequest(
              "default",
              "json-variant-flag-1",
              None,
              segmentContext,
              None
            )
          )
          _ <- IO.println(res)
          result = res.map(_.variantAttachment)
        } yield assertEquals(result, Right(Some(TestVariant("string", 33))))
      }
    }
  }

  test("does not attempt variant deserialisation without a match") {
    withContainers { containers =>
      api(containers).use { flipt =>
        val segmentContext = Map("test-property" -> "unmatched-property-value")

        for {
          res <- flipt.evaluateStructuredVariant[TestVariant](
            EvaluationRequest(
              "default",
              "json-variant-flag-1",
              None,
              segmentContext,
              None
            )
          )
          _ <- IO.println(res)
          result = res.map(_.variantAttachment)
        } yield assertEquals(result, Right(None))
      }
    }
  }

}
