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

package io.cardell.openfeature.provider.java

import cats.effect.IO
import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.DockerComposeContainer
import com.dimafeng.testcontainers.ExposedService
import com.dimafeng.testcontainers.munit.TestContainerForAll
import java.io.File
import munit.CatsEffectSuite
import org.testcontainers.containers.wait.strategy.Wait

import dev.openfeature.contrib.providers.flipt.FliptProvider
import io.flipt.api.authentication.ClientTokenAuthenticationStrategy
import dev.openfeature.contrib.providers.flipt.FliptProviderConfig
import io.flipt.api.FliptClient
import io.cardell.openfeature.ContextValue
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FeatureClientImpl
import io.cardell.openfeature.EvaluationReason
import dev.openfeature.contrib.providers.flagd.FlagdOptions
import dev.openfeature.contrib.providers.flagd.FlagdProvider

// see docker-compose features.yaml for flag test data
class JavaProviderItTest extends CatsEffectSuite with TestContainerForAll {

  override val containerDef: ContainerDef = DockerComposeContainer.Def(
    new File("docker-compose.yaml"),
    exposedServices = Seq(
      ExposedService("flipt", 8080, Wait.forLogMessage("^UI: http.*", 1))
    ),
    tailChildContainers = true
  )

  def flagd(containers: Containers): Resource[IO, JavaProvider[IO]] = {
    val flagd =
      containers
        .asInstanceOf[DockerComposeContainer]
        .getContainerByServiceName("flagd")
        .get

    val flagdProvder =
      new FlagdProvider(
        FlagdOptions
          .builder()
          .host(flagd.getHost())
          .port(flagd.getMappedPort(8013).toInt)
          .build()
      )

    JavaProvider.resource[IO](flagdProvder)
  }

  def provider(containers: Containers): Resource[IO, JavaProvider[IO]] = {
    val flipt =
      containers
        .asInstanceOf[DockerComposeContainer]
        .getContainerByServiceName("flipt")
        .get

      val url = s"http://${flipt.getHost()}:${flipt.getMappedPort(8080)}"

    val client = FliptClient
      .builder()
      .url(url)
      .authentication(
        new ClientTokenAuthenticationStrategy("dummy")
      )

    val config = FliptProviderConfig.builder().fliptClientBuilder(client).build
    val fliptProvider = new FliptProvider(config)

    JavaProvider.resource[IO](fliptProvider)
  }

  def featureClient(containers: Containers) = provider(containers).map(
    FeatureClientImpl[IO](_)
  )

  val segmentContext = Map(
    "test-property" -> ContextValue.StringValue("matched-property-value")
  )

  val evaluationContext = EvaluationContext(None, segmentContext)

  test("can fetch boolean flag") {
    val expected = true

    withContainers { containers =>
      provider(containers).use { flipt =>
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

  test("can resolve string flag for variant") {
    val expected = "string-variant-1"

    withContainers { containers =>
      provider(containers).use { flipt =>
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

  test("can resolve int flag") {
    val expected = 13

    withContainers { containers =>
      provider(containers).use { flipt =>
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

  test("can resolve double flag") {
    val expected = 17.1

    withContainers { containers =>
      provider(containers).use { flipt =>
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

  test("can resolve structure".only) {
    val expected = TestVariant("string", 33)

    withContainers { containers =>
      flagd(containers).use { flagd =>
        for {
          res <- flagd.resolveStructureValue(
            "variant-flag-1",
            TestVariant("a", 0),
            evaluationContext
          )
          _ <- IO.println(res)
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("feature client handles error when flag not found") {
    val expected = true

    withContainers { containers =>
      featureClient(containers).use { client =>
        for {
          res <- client.getBooleanDetails(
            "missing-flag",
            expected
          )
        } yield {
          assertEquals(res.value, expected)
          assertEquals(res.reason, Some(EvaluationReason.Error))
        }
      }
    }
  }

}
