import cats.data.NonEmptyChain

val x = List.empty[NonEmptyChain[Int]].reduce(_ ++ _)

x
