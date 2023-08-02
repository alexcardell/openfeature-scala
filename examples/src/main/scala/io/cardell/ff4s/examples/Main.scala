package io.cardell.ff4s.examples

import cats.effect.IOApp
import cats.MonadThrow
import org.http4s.implicits._
import cats.effect.IO
import fliptapi.FliptOpenapiService
import cats.effect.Resource
import org.http4s.ember.client.EmberClientBuilder
import smithy4s.http4s.SimpleRestJsonBuilder

object Main extends IOApp.Simple {
  def log(msg: String) = IO.println(msg)

  def setup: Resource[IO, FliptOpenapiService[IO]] =
    for {
      client <- EmberClientBuilder.default[IO].build
      maybeService = SimpleRestJsonBuilder(FliptOpenapiService)
        .client[IO](client)
        .uri(uri"http://localhost:8080")
        .make
      service <- Resource.eval(MonadThrow[IO].fromEither(maybeService))
    } yield service

  def run: IO[Unit] =
    setup.use { (service: FliptOpenapiService[IO]) =>
      for {
        res <- service.flagsServiceGet("default", "example-flag-1")
        _ <- log(res.toString)
        _ <- IO(assert(res.body.enabled == true))
        _ <- log("success")
      } yield ()
    }
}
