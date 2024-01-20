package io.cardell.ff4s.flipt.auth

sealed trait AuthenticationStrategy

object AuthenticationStrategy {
  case class ClientToken(token: String) extends AuthenticationStrategy
  case class JWT(token: String) extends AuthenticationStrategy
}
