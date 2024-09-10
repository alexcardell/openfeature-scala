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

  test("can fetch boolean flag") {
    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveBooleanValue(
            "boolean-flag-1",
            false,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, true)
      }
    }
  }

  test("uses default when boolean flag missing") {
    withContainers { containers =>
      api(containers).use { flipt =>
        for {
          res <- flipt.resolveBooleanValue(
            "no-flag",
            false,
            EvaluationContext.empty
          )
        } yield assertEquals(res.value, false)
      }
    }
  }

  test("can resolve string value for flipt variant flag") {
    withContainers { containers =>
      api(containers).use { flipt =>
        val segmentContext = Map(
          "test-property" -> ContextValue.StringValue("matched-property-value")
        )

        for {
          res <- flipt.resolveStringValue(
            "string-variant-flag-1",
            "default-string",
            EvaluationContext(None, segmentContext)
          )
        } yield assertEquals(res.value, "string-variant-1")
      }
    }
  }

  test("can resolve int value for flipt variant flag") {
    val expected = 13

    withContainers { containers =>
      api(containers).use { flipt =>
        val segmentContext = Map(
          "test-property" -> ContextValue.StringValue("matched-property-value")
        )

        for {
          res <- flipt.resolveIntValue(
            "int-variant-flag-1",
            99,
            EvaluationContext(None, segmentContext)
          )
        } yield assertEquals(res.value, expected)
      }
    }
  }

  // case class TestVariant(field: String, intField: Int)
  // object TestVariant {
  //   implicit val decoder: Decoder[TestVariant] = deriveDecoder
  // }
  //
  // test("can deserialise variant match") {
  //   withContainers { containers =>
  //     api(containers).use { flipt =>
  //       val segmentContext = Map("test-property" -> "matched-property-value")
  //
  //       for {
  //         res <- flipt.evaluateStructuredVariant[TestVariant](
  //           EvaluationRequest(
  //             "default",
  //             "variant-flag-1",
  //             None,
  //             segmentContext,
  //             None
  //           )
  //         )
  //         _ <- IO.println(res)
  //         result = res.map(_.variantAttachment)
  //       } yield assertEquals(result, Right(Some(TestVariant("string", 33))))
  //     }
  //   }
  // }
  //
  // test("does not attempt variant deserialisation without a match") {
  //   withContainers { containers =>
  //     api(containers).use { flipt =>
  //       val segmentContext = Map("test-property" -> "unmatched-property-value")
  //
  //       for {
  //         res <- flipt.evaluateStructuredVariant[TestVariant](
  //           EvaluationRequest(
  //             "default",
  //             "variant-flag-1",
  //             None,
  //             segmentContext,
  //             None
  //           )
  //         )
  //         _ <- IO.println(res)
  //         result = res.map(_.variantAttachment)
  //       } yield assertEquals(result, Right(None))
  //     }
  //   }
  // }
}
