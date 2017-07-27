package view

import javafx.beans.value.ObservableValue
import javafx.scene.control._
import javafx.scene.input.KeyCode
import javafx.util.Callback

import controller.ChatController
import model.domain.Domain.{ChatMessage, Contact}
import model.service.ChatService

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.input.{KeyEvent, MouseEvent}
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Main {

  private val mainRoot = FXMLView(getClass.getResource("main.fxml"), NoDependencyResolver)
  private val mainScene = new Scene(mainRoot)
  private val sendButton = mainRoot.lookup("#sendButton").asInstanceOf[Button]
  private val messagesBox = mainRoot.lookup("#messagesView").asInstanceOf[ListView[ChatMessage]]
  private val newMessageArea = mainRoot.lookup("#newMessageArea").asInstanceOf[TextArea]
  private val mainMenuBar = mainRoot.lookup("#menuBar").asInstanceOf[MenuBar]
  private val clearMessagesButton = mainRoot.lookup("#removeMessagesButton").asInstanceOf[Button]
  private val contactsView = mainRoot.lookup("#contactsView").asInstanceOf[ListView[Contact]]
  private val fileMenu = mainMenuBar.getMenus.get(0)
  private val exitMenuItem = fileMenu.getItems.get(0)
  private val chatController = ChatController
  private val addNewContactButton = mainRoot.lookup("#addNewContactButton").asInstanceOf[Button]

  exitMenuItem.onAction = (e: ActionEvent) => System.exit(0)
  clearMessagesButton.onMouseClicked = (e: MouseEvent) => ???
  sendButton.onMouseClicked = (e: MouseEvent) => sendMessage(newMessageArea.getText)
  newMessageArea.onKeyPressed = (e: KeyEvent) => {
    if (e.getCode.equals(KeyCode.ENTER)) {
      sendMessage(newMessageArea.getText)
      e.consume
    }
  }

  addNewContactButton.onMouseClicked = (e: MouseEvent) => {
    ChatService.addContact(111901961, "Настюша")
  }

  contactsView.onMouseClicked = (e: MouseEvent) => {
    val selectedContact = contactsView.getSelectionModel.getSelectedItem
    if (selectedContact != null) {
      chatController.selectContact(selectedContact)
    }
  }

  contactsView.setCellFactory(new Callback[ListView[Contact], ListCell[Contact]]() {
    override def call(p: ListView[Contact]): ListCell[Contact] = {
      val cell: ListCell[Contact] = new ListCell[Contact]() {
        override def updateItem(t: Contact, bln: Boolean) {
          super.updateItem(t, bln);
          if (t != null) {
            setText(t.name)
          }
        }
      }
      cell
    }
  })

  private def sendMessage(message: String): Unit = {
    if (message != null && message.length != 0) {
      chatController.sendMessage(message)
    }
  }

  def getScene() = {
    mainScene
  }

  def updateContactsList(contactsList: Map[Long, Contact]): Unit = {
    contactsView.getItems.clear()
    val currentContacts = contactsView.getItems
    contactsList.values foreach (contact => currentContacts += contact)
  }

  def updateMessagesList(messages: List[ChatMessage]) = {
    messagesBox.getItems.clear()
    messages foreach (message => messagesBox.getItems += message)
  }

}
