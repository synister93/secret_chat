package application

import java.net.InetSocketAddress

import akka.actor.Props
import com.typesafe.config.ConfigFactory
import org.json4s.{Extraction, NoTypeHints}
import org.json4s.jackson.JsonMethods.pretty
import org.json4s.jackson.Serialization
import org.slf4j.LoggerFactory
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}
import service.ChatService
import to.ChatMessage

import scala.util.{Failure, Success}

object RedisClient {

  implicit val akkaSystem = akka.actor.ActorSystem()
  implicit val redisPool = akkaSystem.dispatchers.lookup("contexts.redis-connection")
  implicit val FORMATS = Serialization.formats(NoTypeHints)

  private val log = LoggerFactory.getLogger(this.getClass)
  private val connectionHost = ConfigFactory.load.getString("redis.host")
  private val connectionPort = ConfigFactory.load.getInt("redis.port")
  private val connectedUsersMap = "connectedUsersMap:"
  private val messagesQueue = Application.fqdn
  private val messagesWaitingUserMap = "messagesWaitingUserMap:"
  private val channels = Seq(messagesQueue)
  private val patterns = Seq.empty[String]
  private val client = redis.RedisClient(host = connectionHost, port = connectionPort)
  private val chatService = ChatService

  akkaSystem.actorOf(Props(classOf[SubscribeActor], channels, patterns))

  def online(pid: Long): Unit = {
    val userKey = getUserKey(pid)
    client.set(userKey, Application.fqdn) onComplete {
      case Success(_) => log.info("Connected user {}", pid)
      case Failure(ex) => ResultsHandler.handleException(ex)
    }
  }

  def offline(pid: Long): Unit = {
    val userKey = getUserKey(pid)
    client.del(userKey) onComplete {
      case Success(_) => log.info("Offline user {}", pid)
      case Failure(f) => ResultsHandler.handleException(f)
    }
  }

  def pubMessageToReceiverServer(chatMessage: ChatMessage): Unit = {
    val userKey = getUserKey(chatMessage.receiverPid)
    val serializedMessage = pretty(Extraction.decompose(chatMessage))
    client.get(userKey) map (userHost => client.publish(userHost.head.utf8String, serializedMessage))
  }

  private class SubscribeActor(channels: Seq[String] = Nil, patterns: Seq[String] = Nil) extends RedisSubscriberActor(
    new InetSocketAddress(connectionHost, connectionPort), channels, patterns, onConnectStatus = _ => None) {

    def onMessage(message: Message) {
      log.info("Subscribed message {}", message.data.utf8String)
      chatService.sendMessage(message.data.utf8String)
    }

    def onPMessage(pmessage: PMessage) {}
  }

  private def getUserKey(pid: Long): String = {
    connectedUsersMap + pid.toString
  }

}
