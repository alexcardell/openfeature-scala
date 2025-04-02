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

import dev.openfeature.sdk.ErrorCode.FLAG_NOT_FOUND
import dev.openfeature.sdk.ErrorCode.GENERAL
import dev.openfeature.sdk.ErrorCode.INVALID_CONTEXT
import dev.openfeature.sdk.ErrorCode.PARSE_ERROR
import dev.openfeature.sdk.ErrorCode.PROVIDER_NOT_READY
import dev.openfeature.sdk.ErrorCode.TARGETING_KEY_MISSING
import dev.openfeature.sdk.ErrorCode.TYPE_MISMATCH
import dev.openfeature.sdk.Reason.CACHED
import dev.openfeature.sdk.Reason.DEFAULT
import dev.openfeature.sdk.Reason.DISABLED
import dev.openfeature.sdk.Reason.ERROR
import dev.openfeature.sdk.Reason.SPLIT
import dev.openfeature.sdk.Reason.STATIC
import dev.openfeature.sdk.Reason.TARGETING_MATCH
import dev.openfeature.sdk.Reason.UNKNOWN
import dev.openfeature.sdk.{ErrorCode => JErrorCode}
import dev.openfeature.sdk.{ImmutableContext => JContext}
import dev.openfeature.sdk.{ImmutableStructure => JStructure}
import dev.openfeature.sdk.{ProviderEvaluation => JEvaluation}
import dev.openfeature.sdk.{Reason => JReason}
import dev.openfeature.sdk.{Value => JValue}
import scala.jdk.CollectionConverters._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import io.cardell.openfeature.ContextValue
import io.cardell.openfeature.ContextValue.BooleanValue
import io.cardell.openfeature.ContextValue.DoubleValue
import io.cardell.openfeature.ContextValue.IntValue
import io.cardell.openfeature.ContextValue.StringValue
import io.cardell.openfeature.ErrorCode
import io.cardell.openfeature.EvaluationContext
import io.cardell.openfeature.EvaluationReason
import io.cardell.openfeature.FlagValue
import io.cardell.openfeature.Structure
import io.cardell.openfeature.provider.ResolutionDetails

object ToJavaConverters {

  def contextValue(value: ContextValue): JValue =
    value match {
      case BooleanValue(b) => new JValue(b)
      case DoubleValue(d)  => new JValue(d)
      case IntValue(i)     => new JValue(i)
      case StringValue(s)  => new JValue(s)
    }

  def evaluationContext(ec: EvaluationContext): JContext = {
    val values = ec.values.map { case (k, v) => (k, contextValue(v)) }.asJava
    ec.targetingKey.fold(new JContext(values))(tk => new JContext(tk, values))
  }

  def structure(structure: Structure): JStructure = {
    val values =
      structure.values.map { case (k, v) => (k, flagValue(v)) }.asJava

    new JStructure(values)
  }

  def flagValue(value: FlagValue): JValue =
    value match {
      case FlagValue.BooleanValue(b)   => new JValue(b)
      case FlagValue.IntValue(i)       => new JValue(i)
      case FlagValue.DoubleValue(d)    => new JValue(d)
      case FlagValue.StringValue(s)    => new JValue(s)
      case FlagValue.StructureValue(s) => new JValue(structure(s))
    }

}

object FromJavaConverters {

  def errorCode(ec: JErrorCode): ErrorCode =
    ec match {
      case GENERAL               => ErrorCode.General
      case PARSE_ERROR           => ErrorCode.ParseError
      case TYPE_MISMATCH         => ErrorCode.TypeMismatch
      case TARGETING_KEY_MISSING => ErrorCode.TargetingKeyMissing
      case PROVIDER_NOT_READY    => ErrorCode.ProviderNotReady
      case INVALID_CONTEXT       => ErrorCode.InvalidContext
      case FLAG_NOT_FOUND        => ErrorCode.FlagNotFound
    }

  def reason(reason: JReason): EvaluationReason =
    reason match {
      case DEFAULT         => EvaluationReason.Default
      case TARGETING_MATCH => EvaluationReason.TargetingMatch
      case DISABLED        => EvaluationReason.Disabled
      case UNKNOWN         => EvaluationReason.Unknown
      case ERROR           => EvaluationReason.Error
      case SPLIT           => EvaluationReason.Split
      case CACHED          => EvaluationReason.Cached
      case STATIC          => EvaluationReason.Static
    }

  def evaluation[A, B](ev: JEvaluation[A])(
      f: A => B
  ): ResolutionDetails[B] = {
    val value     = ev.getValue()
    val converted = f(value)

    ResolutionDetails[B](
      value = converted,
      reason = Option(ev.getReason())
        .map(JReason.valueOf)
        .map(FromJavaConverters.reason),
      errorCode = Option(ev.getErrorCode())
        .map(FromJavaConverters.errorCode),
      errorMessage = Option(ev.getErrorMessage()),
      variant = Option(ev.getVariant()),
      metadata = None
    )
  }

  def evaluation[A](ev: JEvaluation[A]): ResolutionDetails[A] =
    evaluation[A, A](ev)(identity[A] _)

  def structure(structure: JStructure): Structure = {
    val values = Map.from(
      structure
        .asMap()
        .asScala
        .map { case (k, v) => (k, value(v)) }
    )

    Structure(values)
  }

  def value(value: JValue): FlagValue =
    value match {
      case v if v.isBoolean() => FlagValue.BooleanValue(v.asBoolean())
      case v if v.isNumber() =>
        Try(v.asInteger()) match {
          case Success(i) => FlagValue.IntValue(i)
          case Failure(_) => FlagValue.DoubleValue(v.asDouble())
        }
      case v if v.isString() => FlagValue.StringValue(v.asString())
      case v if v.isStructure() =>
        FlagValue.StructureValue(
          structure(new JStructure(v.asStructure().asMap()))
        )
      case _ => ??? // TODO handle
    }

}
