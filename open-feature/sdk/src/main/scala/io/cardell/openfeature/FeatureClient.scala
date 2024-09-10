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

package io.cardell.openfeature

import io.cardell.openfeature.provider.ProviderMetadata

trait FeatureClient[F[_]] {

  def providerMetadata: ProviderMetadata

  def evaluationContext: EvaluationContext
  def withEvaluationContext(context: EvaluationContext): FeatureClient[F]

  // def hooks: List[Hook]
  // def withHook(hook: Hook): FeatureClient[F]
  // def withHooks(hooks: List[Hook]): FeatureClient[F]

  def getBooleanValue(flagKey: String, default: Boolean): F[Boolean]

  def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[Boolean]

  def getBooleanValue(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Boolean]

  def getBooleanDetails(
      flagKey: String,
      default: Boolean
  ): F[EvaluationDetails[Boolean]]

  def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext
  ): F[EvaluationDetails[Boolean]]

  def getBooleanDetails(
      flagKey: String,
      default: Boolean,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Boolean]]

  def getStringValue(flagKey: String, default: String): F[String]

  def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[String]

  def getStringValue(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[String]

  def getStringDetails(
      flagKey: String,
      default: String
  ): F[EvaluationDetails[String]]

  def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext
  ): F[EvaluationDetails[String]]

  def getStringDetails(
      flagKey: String,
      default: String,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[String]]

  def getIntValue(flagKey: String, default: Int): F[Int]

  def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext
  ): F[Int]

  def getIntValue(
      flagKey: String,
      default: Int,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Int]

  def getIntDetails(flagKey: String, default: Int): F[EvaluationDetails[Int]]

  def getIntDetails(
      flagKey: String,
      default: Int,
      context: EvaluationContext
  ): F[EvaluationDetails[Int]]

  def getIntDetails(
      flagKey: String,
      default: Int,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Int]]

  def getDoubleValue(flagKey: String, default: Double): F[Double]

  def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[Double]

  def getDoubleValue(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[Double]

  def getDoubleDetails(
      flagKey: String,
      default: Double
  ): F[EvaluationDetails[Double]]

  def getDoubleDetails(
      flagKey: String,
      default: Double,
      context: EvaluationContext
  ): F[EvaluationDetails[Double]]

  def getDoubleDetails(
      flagKey: String,
      default: Double,
      context: EvaluationContext,
      options: EvaluationOptions
  ): F[EvaluationDetails[Double]]

  // def getStructureValue[A: StructureDecoder](
  //     flagKey: String,
  //     default: A
  // ): F[A]
  // def getStructureValue[A: StructureDecoder](
  //     flagKey: String,
  //     default: A,
  //     context: EvaluationContext
  // ): F[A]
  // def getStructureValue[A: StructureDecoder](
  //     flagKey: String,
  //     default: A,
  //     context: EvaluationContext,
  //     options: EvaluationOptions
  // ): F[A]
  //
  // def getStructureDetails[A: StructureDecoder](
  //     flagKey: String,
  //     default: A
  // ): F[EvaluationDetails[A]]
  // def getStructureDetails[A: StructureDecoder](
  //     flagKey: String,
  //     default: A,
  //     context: EvaluationContext
  // ): F[EvaluationDetails[A]]
  // def getStructureDetails[A: StructureDecoder](
  //     flagKey: String,
  //     default: A,
  //     context: EvaluationContext,
  //     options: EvaluationOptions
  // ): F[EvaluationDetails[A]]

}
