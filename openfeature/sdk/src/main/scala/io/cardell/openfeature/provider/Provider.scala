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

package io.cardell.openfeature.provider

import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.ErrorHook
import io.cardell.openfeature.Hook

trait Provider[F[_]] extends EvaluationProvider[F] {
  def beforeHooks: List[BeforeHook[F]]
  def errorHooks: List[ErrorHook[F]]

  def withHook(hook: Hook[F]): Provider[F]
  // def withHooks(hooks: List[Hook[F]]): Provider[F]

}
