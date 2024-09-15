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

package io.cardell.openfeature.provider.memory

import cats.MonadThrow
import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.syntax.all._

import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.EvaluationReason
import io.cardell.openfeature.StructureDecoder
import io.cardell.openfeature.provider.EvaluationProvider
import io.cardell.openfeature.provider.ProviderMetadata
import io.cardell.openfeature.provider.ResolutionDetails

sealed trait MemoryFlagState

object MemoryFlagState {
  case class BooleanFlagState(value: Boolean) extends MemoryFlagState
  case class StringFlagState(value: String)   extends MemoryFlagState
  case class IntFlagState(value: Int)         extends MemoryFlagState
  case class DoubleFlagState(value: Double)   extends MemoryFlagState
}

final class MemoryProvider[F[_]: MonadThrow](
    ref: Ref[F, Map[String, MemoryFlagState]]
) extends EvaluationProvider[F] {

  import MemoryFlagState._

  override def metadata: ProviderMetadata = ProviderMetadata("memory")

  private def missing[A](
      flagKey: String,
      defaultValue: A
  ): ResolutionDetails[A] = ResolutionDetails(
    value = defaultValue,
    errorCode = Some(ErrorCode.FlagNotFound),
    errorMessage = Some(s"${flagKey} not found"),
    reason = Some(EvaluationReason.Error),
    variant = None,
    metadata = None
  )

  private def typeMismatch[A](
      flagKey: String,
      defaultValue: A
  ): ResolutionDetails[A] = ResolutionDetails(
    value = defaultValue,
    errorCode = Some(ErrorCode.TypeMismatch),
    errorMessage = Some(s"${flagKey} was unexpected type"),
    reason = Some(EvaluationReason.Error),
    variant = None,
    metadata = None
  )

  private def resolution[A](value: A): ResolutionDetails[A] = ResolutionDetails(
    value = value,
    errorCode = None,
    errorMessage = None,
    reason = Some(EvaluationReason.Static),
    variant = None,
    metadata = None
  )

  override def resolveBooleanValue(
      flagKey: String,
      defaultValue: Boolean,
      context: EvaluationContext
  ): F[ResolutionDetails[Boolean]] = ref.get.map { state =>
    state.get(flagKey) match {
      case None => missing[Boolean](flagKey, defaultValue)
      case Some(BooleanFlagState(value)) => resolution[Boolean](value)
      case Some(_)                       => typeMismatch(flagKey, defaultValue)
    }
  }

  override def resolveStringValue(
      flagKey: String,
      defaultValue: String,
      context: EvaluationContext
  ): F[ResolutionDetails[String]] = ref.get.map { state =>
    state.get(flagKey) match {
      case None => missing[String](flagKey, defaultValue)
      case Some(StringFlagState(value)) => resolution[String](value)
      case Some(_)                      => typeMismatch(flagKey, defaultValue)
    }
  }

  override def resolveIntValue(
      flagKey: String,
      defaultValue: Int,
      context: EvaluationContext
  ): F[ResolutionDetails[Int]] = ref.get.map { state =>
    state.get(flagKey) match {
      case None                      => missing[Int](flagKey, defaultValue)
      case Some(IntFlagState(value)) => resolution[Int](value)
      case Some(_)                   => typeMismatch(flagKey, defaultValue)
    }
  }

  override def resolveDoubleValue(
      flagKey: String,
      defaultValue: Double,
      context: EvaluationContext
  ): F[ResolutionDetails[Double]] = ref.get.map { state =>
    state.get(flagKey) match {
      case None => missing[Double](flagKey, defaultValue)
      case Some(DoubleFlagState(value)) => resolution[Double](value)
      case Some(_)                      => typeMismatch(flagKey, defaultValue)
    }
  }

  override def resolveStructureValue[A: StructureDecoder](
      flagKey: String,
      defaultValue: A,
      context: EvaluationContext
  ): F[ResolutionDetails[A]] = MonadThrow[F].raiseError(
    new NotImplementedError(
      "Structure values not implemented in in-memory provider"
    )
  )
  // {
  //   val resolved = ref.get.map { state =>
  //     val x = state.get(flagKey) match {
  //       case None => missing[A](flagKey, defaultValue)
  //       case Some(StructureFlagState(value)) => resolution[A](value.asInstanceOf[A])
  //       case Some(_) => typeMismatch(flagKey, defaultValue)
  //     }
  //     @nowarn
  //     val value: A = x.value
  //     x
  //   }
  //
  //   resolved.handleError { case _ => missing[A](flagKey, defaultValue) }
  // }

}

object MemoryProvider {

  def apply[F[_]: Sync](
      state: Map[String, MemoryFlagState]
  ): F[MemoryProvider[F]] =
    for {
      ref <- Ref.of[F, Map[String, MemoryFlagState]](state)
    } yield new MemoryProvider[F](ref)

}
