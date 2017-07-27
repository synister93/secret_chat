package view

import javafx.scene.control._

import controller.AuthController

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Login {

  private val loginRoot = FXMLView(getClass.getResource("login.fxml"), NoDependencyResolver)
  private val loginScene = new Scene(loginRoot)
  private val authController = AuthController

  private val pidField = loginRoot.lookup("#uidField").asInstanceOf[TextField]
  private val passwordField = loginRoot.lookup("#passwordField").asInstanceOf[PasswordField]
  private val resultLabel = loginRoot.lookup("#resultLabel").asInstanceOf[Label]
  private val loginButton = loginRoot.lookup("#loginButton").asInstanceOf[Button]

  loginButton.onMouseClicked = (e: MouseEvent) => {
    val result = authController.login(pidField.getText, passwordField.getText)
    resultLabel.text = result._2
  }

  private val registerButton = loginRoot.lookup("#regButton")
  registerButton.onMouseClicked = (e: MouseEvent) => {
    authController.showRegisterView()
  }

  def getScene() = {
    loginScene
  }

}
