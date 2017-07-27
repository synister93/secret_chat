package model.domain

object Domain {

  case class Contact(pid: Long, var name: String, var messages: List[ChatMessage])

  case class ChatMessage(senderPid: Long, receiverPid: Long, text: String, timestamp: Long)

}
