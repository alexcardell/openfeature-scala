package io.cardell.openfeature

import cats.Applicative
import cats.Monad
import cats.syntax.all._

case class HookContext(
    flagKey: String,
    evaluationContext: EvaluationContext,
    defaultValue: FlagValue
)
