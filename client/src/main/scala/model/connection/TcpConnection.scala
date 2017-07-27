package model.connection

import java.nio.charset.StandardCharsets
import java.util.StringJoiner
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import model.domain.Domain.ChatMessage
import model.service.ActionResults._
import org.json4s.{Extraction, NoTypeHints}
import org.json4s.jackson.JsonMethods.pretty
import org.json4s.jackson.Serialization
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

//TODO Что с реконнектом???
//TODO Что делать в случае, если попытка выполнить действие при протухшем коннекте?
object TcpConnection {

  var serverResponseOption = Option.empty[String]
  val splitPattern = "&"
  val headerFillingSymbol = " "
  val headerLength = 50

  @volatile
  private var channel: Channel = _
  private val connectionActor = "Connection"
  private val logger = LoggerFactory.getLogger(this.getClass)

  implicit val FORMATS = Serialization.formats(NoTypeHints)

  connectFuture()

  private def connectFuture(): Unit = {
    val actorSystem = ActorSystem(connectionActor)
    implicit val connecting = actorSystem.dispatchers.lookup("contexts.netty-connection")
    val connectionThread = Future(connect())
    connectionThread.onComplete {
      case Success(_) => logger.info("Connection future completed")
      case Failure(f) => handleException(f)
    }
    actorSystem.terminate()
  }

  private def connect(): Unit = {
    val group = new NioEventLoopGroup
    Try {
      val bootstrap = new Bootstrap
      bootstrap.group(group).channel(classOf[NioSocketChannel]).handler(new NettyClientInitializer)
      channel = bootstrap.connect("localhost", 10500).channel()
      channel.closeFuture().sync()
    } recover {
      case ex: Exception => handleException(ex)
    }
    group.shutdownGracefully()
  }

  def sendMessage(message: ChatMessage): Unit = {
    val serializedMessage = pretty(Extraction.decompose(message))
    logger.info("Sending serialized message {}", serializedMessage)
    val messageForSend = createMessageToSend(Messages.MESSAGE, serializedMessage)
    sendCreatedMessage(messageForSend)
  }

  def authorize(pid: String, password: String): ActionResult = {
    val action: ActionResult = {
      val content = new StringBuilder().append(pid).append(splitPattern).append(password).toString()
      val message = createMessageToSend(Messages.AUTHORIZATION, content)
      sendCreatedMessage(message)
      handleResponse
    }
    tryResult(action)
  }

  def register(password: String): ActionResult = {
    val action: ActionResult = {
      sendCreatedMessage(createMessageToSend(Messages.REGISTER, password))
      handleResponse
    }
    tryResult(action)
  }

  def checkPidExisting(checkingPid: Long): Unit = {

  }

  def close() = {
    channel.close()
  }

  private def tryResult(action: ActionResult): ActionResult = {
    val tryAction = Try(action)
    tryAction match {
      case Success(result) => result
      case Failure(exception) => {
        handleException(exception)
        systemError
      }
    }
  }

  private def sendCreatedMessage(msg: Array[Byte]): Unit = {
    channel.writeAndFlush(msg)
  }

  //TODO Ограничить ожидание
  private def handleResponse: ActionResult = {
    while (serverResponseOption.isEmpty) {
      TimeUnit.MILLISECONDS.sleep(10)
    }
    val serverResponse = serverResponseOption.get
    val result = messageResultMap(serverResponse)
    serverResponseOption = Option.empty[String]
    result
  }

  private def createMessageToSend(message: Messages.MessagesEnum, content: String): Array[Byte] = {
    val header = getHeader(message.toString)
    val messageBuilder = new StringBuilder
    messageBuilder.append(header).append(content).toString().getBytes(StandardCharsets.UTF_8.name())

  }

  private def getHeader(message: String): String = {
    val messageLength = message.length
    val diff = headerLength - messageLength
    message + headerFillingSymbol * diff
  }
}
