package io.cardell

package object openfeature {

  type EvaluationContext = Map[String, String]

  object EvaluationContext {
    def empty: EvaluationContext = Map.empty
  }

  type Providers[F[_]] = Map[String, Provider[F]]
  object Providers {
    def empty[F[_]]: Providers[F] = Map.empty
  }
}
