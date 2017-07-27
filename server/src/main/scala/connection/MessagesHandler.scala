package connection

import java.nio.charset.StandardCharsets
import java.util.StringJoiner

import application.ResultsHandler
import application.ResultsHandler._
import connection.MessagesHandler._
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import org.json4s.NoTypeHints
import org.json4s.jackson.{JsonMethods, Serialization}
import org.slf4j.LoggerFactory
import service.{AuthorizationService, ChatService}
import to.ChatMessage

class MessagesHandler extends SimpleChannelInboundHandler[Array[Byte]] {

  implicit val FORMATS = Serialization.formats(NoTypeHints)

  private val chatService = ChatService
  private val authorizationService = AuthorizationService
  private val logger = LoggerFactory.getLogger(this.getClass)
  private var currentPid: Long = 0L

  override def channelRead0(ctx: ChannelHandlerContext, msg: Array[Byte]): Unit = {
    val header = new String(msg, 0, headerLength, StandardCharsets.UTF_8.name())
    val data = new String(msg, headerLength, msg.length - headerLength, StandardCharsets.UTF_8.name())
    if (header.contains(Messages.REGISTER.toString)) {
      val password = data.split(NettyConnectionServer.splitPattern)(0)
      registerPid(password, ctx)
    } else if (header.contains(Messages.AUTHORIZATION.toString)) {
      val splittedData = data.split(NettyConnectionServer.splitPattern)
      val pid = splittedData(0)
      val password = splittedData(1)
      authorize(pid, password, ctx)
    } else if (header.contains(Messages.MESSAGE.toString)) {
      log.info("!!!!Serialized message is {}", data)
      val deserializedMessage = JsonMethods.parse(data).extract[ChatMessage]
      if (userOnline(deserializedMessage.receiverPid)) {
        logger.info("publishing message for user online {}", deserializedMessage.receiverPid)
        chatService.putMessageToReceiverNode(deserializedMessage)
      }
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    ResultsHandler.handleException(cause)
    //ctx.close()
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    offlineUser()
  }

  private def offlineUser(): Unit = {
    if (currentPid != 0L) {
      offline(currentPid)
    }
  }

  private def registerPid(password: String, ctx: ChannelHandlerContext): Unit = {
    val result = authorizationService.registerPid(password)
    val messToSend = createMessageToSend(messageResultMap(result))
    logger.info("Message to send length is {}", messToSend.length)
    ctx.writeAndFlush(messToSend)
  }

  private def authorize(pid: String, password: String, ctx: ChannelHandlerContext): Unit = {
    val resultTo = authorizationService.authorize(pid.toLong, password)
    val result = resultTo.result
    val currentPidVal = resultTo.data.get.asInstanceOf[Long]
    currentPid = currentPidVal
    if (currentPid != 0) {
      online(currentPid, ctx)
    }
    ctx.writeAndFlush(createMessageToSend(messageResultMap(result)))
  }
}

object MessagesHandler {

  val headerLength = 50

  implicit val FORMATS = Serialization.formats(NoTypeHints)

  private var onlineUsers = Map[Long, ChannelHandlerContext]()
  private val authorizationService = AuthorizationService
  private val log = LoggerFactory.getLogger(this.getClass)

  def online(pid: Long, ctx: ChannelHandlerContext): Unit = {
    onlineUsers += pid -> ctx
    authorizationService.online(pid)
  }

  def offline(pid: Long): Unit = {
    onlineUsers -= pid
    authorizationService.offline(pid)
  }

  def userOnline(pid: Long): Boolean = {
    onlineUsers.contains(pid)
  }

  def sendMessage(serializedMessage: String): Unit = {
    val deserializedMessage = JsonMethods.parse(serializedMessage).extract[ChatMessage]
    log.info("Sending deserialized message {}", deserializedMessage)
    val receiverUser = onlineUsers.get(deserializedMessage.receiverPid)
    receiverUser match {
      case Some(user) => {
        user.writeAndFlush(createMessageToSend(Messages.MESSAGE, serializedMessage))
      }
      case None => ???   //TODO Положить в мапу ожидающих отправки
    }
  }

  def createMessageToSend(message: Messages.MessagesEnum, content: String = ""): Array[Byte] = {
    val header = getHeader(message.toString)
    val messageBuilder = new StringBuilder
    messageBuilder.append(header).append(content).toString().getBytes(StandardCharsets.UTF_8.name())

  }

  def getHeader(message: String): String = {
    val messageLength = message.length
    val diff = headerLength - messageLength
    message + " " * diff
  }

}
