package model.connection

import java.nio.charset.StandardCharsets

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import model.domain.Domain.ChatMessage
import model.service.ChatService
import model.service.ActionResults._
import org.json4s.NoTypeHints
import org.json4s.jackson.{JsonMethods, Serialization}
import org.slf4j.LoggerFactory

import scala.util.Try

class Handler extends SimpleChannelInboundHandler[Array[Byte]] {

  private val log = LoggerFactory.getLogger(this.getClass)
  private val tcpConnection = TcpConnection
  private val chatService = ChatService

  implicit val FORMATS = Serialization.formats(NoTypeHints)

  override def channelRead0(ctx: ChannelHandlerContext, msg: Array[Byte]): Unit = {
    Try {
      val header = new String(msg, 0, TcpConnection.headerLength, StandardCharsets.UTF_8.name())
      val data = new String(msg, TcpConnection.headerLength, msg.length - TcpConnection.headerLength, StandardCharsets.UTF_8.name())
      log.info("Received message {}", header)
      if (header.contains(Messages.MESSAGE.toString)) {
        log.info("serialized message is {}", data)
        val deserializedMessage = JsonMethods.parse(data).extract[ChatMessage]
        chatService.receiveMessage(deserializedMessage)
      } else {
        tcpConnection.serverResponseOption = Option(header.replaceAll(TcpConnection.headerFillingSymbol, ""))
      }
    } recover {
      case ex: Exception => handleException(ex)
    }
  }

  @throws[Exception]
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    handleException(cause)
    //ctx.close()
  }

  @throws[Exception]
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
  }

  @throws[Exception]
  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
  }
}
