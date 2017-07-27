package controller

import application.{Application, Configuration}
import model.service.ActionResults._
import model.service.AuthorizationService
import view.{Login, Main, Register}

object AuthController {

  private val loginView = Login
  private val authorizationService = AuthorizationService
  private val registerView = Register
  private val mainView = Main

  def showRegisterView(): Unit = {
    Application.setScene(registerView.getScene())
  }

  def showLoginView(): Unit = {
    Application.setScene(loginView.getScene())
  }

  def login(pid: String, password: String): ActionResult = {
    val loginResult = authorizationService.authorize(pid, password)
    if (loginResult.equals(successful)) {
      Application.setScene(mainView.getScene())
    }
    loginResult
  }

  def checkAuthorized() = {
    if (Configuration.isAuthorized) {
      Application.setScene(mainView.getScene())
    } else Application.setScene(loginView.getScene())
  }

  def register(passwordText: String, confirmPasswordText: String): ActionResult = {
    authorizationService.register(passwordText, confirmPasswordText)
  }

}
