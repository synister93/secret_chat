package view

import javafx.scene.control.{Button, Label, PasswordField}

import controller.AuthController

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Register {

  private val root = FXMLView(getClass.getResource("register.fxml"), NoDependencyResolver)
  private val scene = new Scene(root)
  private val authController = AuthController

  private val registerButton = root.lookup("#registrationButton").asInstanceOf[Button]
  private val cancelButton = root.lookup("#cancelButton").asInstanceOf[Button]
  private val passwordField = root.lookup("#registerPasswordField").asInstanceOf[PasswordField]
  private val resultLabel = root.lookup("#registrationResultLabel").asInstanceOf[Label]
  private val confirmRegisterPasswordField = root.lookup("#confirmRegisterPasswordField").asInstanceOf[PasswordField]

  registerButton.onMouseClicked = (e: MouseEvent) => {
    val passwordText = passwordField.getText
    resultLabel.text = ""
    val confirmPasswordText = confirmRegisterPasswordField.getText
    val registrationResult = authController.register(passwordText, confirmPasswordText)
    resultLabel.text = registrationResult._2
  }

  cancelButton.onMouseClicked = (e: MouseEvent) => {
    authController.showLoginView()
  }

  def getScene() = {
    scene
  }

}
