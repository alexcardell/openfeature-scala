package io.cardell.ff4s.examples

import cats.effect.IO

import io.cardell.ff4s.FlagCase
import io.cardell.ff4s.Flags
import io.cardell.ff4s.fliptsdk.FliptClient

object FliptExample {

  def example(): IO[String] = {

    val flipt =
      FliptClient[IO]("https://try.flipt.io", "apitoken", "ff4s-example")

    for {
      flags <- flipt.setup()
      res <- exampleFunc(flags)
    } yield res

  }

  def exampleFunc(flags: Flags[IO]) = flags.get("flag_key").flatMap {
    case FlagCase.On(_) => IO.pure("on")
    case FlagCase.Off   => IO.pure("off")
  }

}
