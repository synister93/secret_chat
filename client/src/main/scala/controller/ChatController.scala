package controller

import model.domain.Domain.{Contact, ChatMessage}
import model.service.ChatService
import view.Main

object ChatController {

  private val chatService = ChatService
  private val mainView = Main

  def closeConnection() {
    chatService.closeConnection()
  }

  def selectContact(contact: Contact): Unit = {
    chatService.selectContact(contact)
  }

  def updateContactsView(contactsList: Map[Long, Contact]): Unit = {
    mainView.updateContactsList(contactsList)
  }

  def sendMessage(text: String): Unit = {
    chatService.sendMessage(text)
  }

  def updateMessagesView(messages: List[ChatMessage]): Unit = {
    mainView.updateMessagesList(messages)
  }

}
