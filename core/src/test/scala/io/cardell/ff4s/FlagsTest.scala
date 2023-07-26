package io.cardell.ff4s

import cats.effect.IO
import weaver.SimpleIOSuite

object FlagsTest extends SimpleIOSuite {

  test("Matching API test") {
    // just an example to test the API

    val expected = 0

    def myDefaultOp: IO[Int] = IO.pure(expected)
    def myFlagOnOp: IO[Int] = IO.pure(1)

    val flags = new StubFlagsOff[IO]

    val result = flags.get("my_key").flatMap {
      case FlagCase.Off => myDefaultOp
      case _            => myFlagOnOp
    }

    for {
      r <- result
    } yield expect(r == expected)

  }
}
