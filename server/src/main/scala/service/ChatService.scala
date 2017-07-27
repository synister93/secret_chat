package service

import application.RedisClient
import connection.MessagesHandler
import to.ChatMessage

object ChatService {

  private val redisClient = RedisClient
  private val messagesHandler = MessagesHandler

  def putMessageToReceiverNode(chatMessage: ChatMessage): Unit = {
    redisClient.pubMessageToReceiverServer(chatMessage)
  }

  def sendMessage(serializedMessage: String): Unit = {
    messagesHandler.sendMessage(serializedMessage)
  }

}
