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

package io.cardell.openfeature.provider.flipt

import cats.effect.IO
import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.DockerComposeContainer
import com.dimafeng.testcontainers.ExposedService
import com.dimafeng.testcontainers.munit.TestContainerForAll
import java.io.File
import munit.CatsEffectSuite
import org.http4s.Uri
import org.http4s.ember.client.EmberClientBuilder
import org.testcontainers.containers.wait.strategy.Wait

import io.cardell.ff4s.flipt.FliptApi
import io.cardell.ff4s.flipt.auth.AuthenticationStrategy
import io.cardell.openfeature.ContextValue
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.circe._

// see docker-compose features.yaml for flag test data
class FliptProviderItTest extends CatsEffectSuite with TestContainerForAll {

  override val containerDef: ContainerDef = DockerComposeContainer.Def(
    new File("docker-compose.yaml"),
    exposedServices = Seq(
      ExposedService("flipt", 8080, Wait.forLogMessage("^UI: http.*", 1))
    ),
    tailChildContainers = true
  )

  def api(containers: Containers): Resource[IO, FliptProvider[IO]] = {
    val flipt =
      containers
        .asInstanceOf[DockerComposeContainer]
        .getContainerByServiceName("flipt")
        .get

    val url = Uri
      .fromString(
        s"http://${flipt.getHost()}:${flipt.getMappedPort(8080)}"
      )
      .getOrElse(throw new Exception("invalid url"))

    EmberClientBuilder
      .default[IO]
      .build
      .map { client =>
        val flipt = FliptApi[IO](
          client,
          url,
          AuthenticationStrategy.ClientToken("token")
        )

        new FliptProvider(flipt, "default")
      }
  }

  val segmentContext = Map(
    "test-property" -> ContextValue.StringValue("matched-property-value")
  )

  val evaluationContext = EvaluationContext(None, segmentContext)

  test("can fetch boolean flag") {
    val expected = true

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveBooleanValue(
            "boolean-flag-1",
            false,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("uses default when boolean flag missing") {
    val expected = false

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveBooleanValue(
            "no-flag",
            false,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can resolve string value for flipt variant flag") {
    val expected = "string-variant-1"

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveStringValue(
            "string-variant-flag-1",
            "default-string",
            evaluationContext
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("uses default when string flag missing") {
    val expected = "some-string"

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveStringValue(
            "no-flag",
            expected,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can resolve int value for flipt variant flag") {
    val expected = 13

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveIntValue(
            "int-variant-flag-1",
            99,
            evaluationContext
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can resolve double value for flipt variant flag") {
    val expected = 17.1

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveDoubleValue(
            "double-variant-flag-1",
            99.9,
            evaluationContext
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can deserialise variant match") {
    val expected = TestVariant("string", 33)

    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveStructureValue[TestVariant](
            "variant-flag-1",
            TestVariant("a", 0),
            evaluationContext
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

}
