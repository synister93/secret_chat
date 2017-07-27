package model.service

import java.util.Date

import application.Configuration
import controller.ChatController
import model.connection.TcpConnection
import model.domain.Domain.{ChatMessage, Contact}
import org.slf4j.LoggerFactory


object ChatService {

  private val tcpConnection = TcpConnection
  private val chatController = ChatController
  private val log = LoggerFactory.getLogger(this.getClass)

  private var contactsList = Map[Long, Contact]()
  private var selectedContact = Option.empty[Contact]

  def addContact(pid: Long, name: String): Unit = {
    contactsList += (pid -> Contact(pid, name, List[ChatMessage]()))
    chatController.updateContactsView(contactsList)
  }

  def removeContact(pid: Long): Unit = {
    contactsList -= pid
    chatController.updateContactsView(contactsList)
  }

  def sendMessage(text: String): Unit = {
    val receiverPid = selectedContact.get.pid
    val message = ChatMessage(Configuration.currentPid, receiverPid, text, new Date().getTime)
    log.info("Sending message: {}", message)
    if (contactsList.get(receiverPid).isEmpty) {
      contactsList += (receiverPid -> Contact(receiverPid, "", List[ChatMessage]()))
    }
    val oldMessgesList = contactsList(receiverPid).messages
    contactsList(receiverPid).messages = oldMessgesList :+ message
    tcpConnection.sendMessage(message)
    chatController.updateMessagesView(selectedContact.get.messages)
  }

  def receiveMessage(message: ChatMessage): Unit = {
    val senderPid = message.senderPid
    if (!contactsList.contains(senderPid)) {
      addContact(senderPid, senderPid.toString)
    }
    val oldMessgesList = contactsList(senderPid).messages
    contactsList(senderPid).messages = oldMessgesList :+ message
    log.info("Sender messages are {}", contactsList)
    chatController.updateContactsView(contactsList)
    if (selectedContact.isDefined) {
      chatController.updateMessagesView(selectedContact.get.messages)
    }
  }

  def removeChatWith(pid: Long): Unit = {
    contactsList(pid).messages = List.empty[ChatMessage]
    chatController.updateMessagesView(selectedContact.get.messages)
  }

  def closeConnection(): Unit = {
    tcpConnection.close()
  }

  def selectContact(contact: Contact): Unit = {
    selectedContact = Option(contact)
    chatController.updateMessagesView(contact.messages)
  }

  def renameContact(contact: Contact, newName: String): Unit = {
    contact.name = newName
    chatController.updateContactsView(contactsList)
  }
}
