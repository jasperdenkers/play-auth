package com.jasperdenkers.play.auth

import play.api.mvc.{Cookie, RequestHeader, Result}

import scala.concurrent.Future

trait Authenticator[A] {

  type AuthenticatedRequestWithIdentity[B] = AuthenticatedRequest[A, B]

  def authenticatedIdentity(request: RequestHeader): Future[Option[A]]

  def notAuthenticatedResult: Future[Result]

}

trait SessionCookieAuthenticator[A] extends Authenticator[A] {

  def login(loginData: LoginData): Future[Option[Cookie]]

  def logout(request: RequestHeader, redirect: Result): Future[Result]

}