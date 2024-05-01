package io.cardell.ff4s.flipt

import cats.effect.IO
import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.DockerComposeContainer
import com.dimafeng.testcontainers.ExposedService
import com.dimafeng.testcontainers.munit.TestContainerForAll
import io.cardell.ff4s.flipt.auth.AuthenticationStrategy
import munit.CatsEffectSuite
import org.http4s.Uri
import org.http4s.ember.client.EmberClientBuilder
import org.testcontainers.containers.wait.strategy.Wait

import java.io.File

class FliptApiImplItTest extends CatsEffectSuite with TestContainerForAll {

  override val containerDef: ContainerDef = DockerComposeContainer.Def(
    new File("../../../docker-compose.yaml"),
    exposedServices = Seq(
      ExposedService("flipt", 8080, Wait.forLogMessage("^UI: http.*", 1))
    ),
    tailChildContainers = true
  )

  def api(containers: Containers): Resource[IO, FliptApi[IO]] = {
    val flipt = containers
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
        val segmentContext = Map("test-property" -> "test-property-value")
        for {
          res <- flipt.evaluateVariant[String](
            EvaluationRequest(
              "default",
              "variant-flag-1",
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
          res <- flipt.evaluateVariant[String](
            EvaluationRequest(
              "default",
              "variant-flag-1",
              None,
              segmentContext,
              None
            )
          )
        } yield assertEquals(res.`match`, false)
      }
    }
  }
}
