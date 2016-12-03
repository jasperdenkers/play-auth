package com.jasperdenkers.play.auth

case class LoginData(identifier: String, plaintextPassword: String, remember: Boolean)
