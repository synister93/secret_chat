package application

import javafx.stage.WindowEvent

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.event.EventHandler

import controller.{AuthController, ChatController}

object Application extends JFXApp {

  private val chatController = ChatController
  private val authController = AuthController

  stage = new PrimaryStage() {
    title = "Secret talker"
  }

  stage.setOnCloseRequest(new EventHandler[WindowEvent]() {
    override def handle(we: WindowEvent): Unit = {
      chatController.closeConnection()
    }
  })

  authController.checkAuthorized()

  def setScene(scene: Scene) {
    stage.scene = scene
    stage.centerOnScreen
  }
}