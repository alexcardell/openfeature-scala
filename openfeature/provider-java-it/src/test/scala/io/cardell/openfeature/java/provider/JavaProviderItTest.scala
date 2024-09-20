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
import dev.openfeature.contrib.providers.flagd.FlagdOptions
import dev.openfeature.contrib.providers.flagd.FlagdProvider
import java.io.File
import munit.CatsEffectSuite
import org.testcontainers.containers.wait.strategy.Wait

import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.FeatureClient
import io.cardell.openfeature.OpenFeature

class JavaProviderItTest extends CatsEffectSuite with TestContainerForAll {

  override val containerDef: ContainerDef = DockerComposeContainer.Def(
    new File("docker-compose.yaml"),
    exposedServices = Seq(
      ExposedService(
        "flagd",
        8013,
        Wait.forLogMessage("^.*watching filepath.*", 1)
      )
    ),
    tailChildContainers = true
  )

  def flagd(containers: Containers): Resource[IO, FeatureClient[IO]] = {
    val container =
      containers
        .asInstanceOf[DockerComposeContainer]
        .getContainerByServiceName("flagd")
        .get

    val provider =
      new FlagdProvider(
        FlagdOptions
          .builder()
          .host(container.getHost())
          .port(container.getMappedPort(8013).toInt)
          .build()
      )

    JavaProvider
      .resource[IO](provider)
      .map(OpenFeature[IO])
      .evalMap(_.client)
  }

  val evaluationContext = EvaluationContext.empty

  test("can fetch boolean flag") {
    val expected = true

    withContainers { containers =>
      flagd(containers)
        .use { provider =>
          for {
            res <- provider.getBooleanDetails(
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
      flagd(containers).use { provider =>
        for {
          res <- provider.getBooleanDetails(
            "no-flag",
            false,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can resolve string value for provider variant flag") {
    val expected = "string-value-1"

    withContainers { containers =>
      flagd(containers).use { provider =>
        for {
          res <- provider.getStringDetails(
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
      flagd(containers).use { provider =>
        for {
          res <- provider.getStringDetails(
            "no-flag",
            expected,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can resolve int value for provider variant flag") {
    val expected = 13

    withContainers { containers =>
      flagd(containers).use { provider =>
        for {
          res <- provider.getIntDetails(
            "int-variant-flag-1",
            99,
            evaluationContext
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  test("can resolve double value for provider variant flag") {
    val expected = 17.1

    withContainers { containers =>
      flagd(containers).use { provider =>
        for {
          res <- provider.getDoubleDetails(
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
      flagd(containers).use { provider =>
        for {
          res <- provider.getStructureDetails[TestVariant](
            "structure-variant-flag-1",
            TestVariant("a", 0),
            evaluationContext
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

}
