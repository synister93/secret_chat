package connection

import akka.actor.ActorSystem
import application.ResultsHandler
import com.typesafe.config.ConfigFactory
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.Try

object NettyConnectionServer {

  val splitPattern = "&"

  private val connectionActor = "Connection"
  private val actorSystem = ActorSystem(connectionActor)
  private val port = ConfigFactory.load.getInt("netty.port")
  private val log = LoggerFactory.getLogger(this.getClass)

  def init(): Unit = {
    implicit val connecting = actorSystem.dispatchers.lookup("contexts.netty-connection")
    Future(connect()) onComplete (_ => log.info("Connection future completed"))
    actorSystem.terminate()
  }

  @throws[Exception]
  private def connect(): Unit = {
    val bossGroup = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup(Runtime.getRuntime.availableProcessors() * 2)
    Try {
      val serverBootstrap = new ServerBootstrap
      serverBootstrap.group(bossGroup, workerGroup).channel(classOf[NioServerSocketChannel]).childHandler(new Initializer)
      log.info("Server connected")
      serverBootstrap.bind(port).channel().closeFuture().sync()
    } recover {
      case ex: Exception => ResultsHandler.handleException(ex)
    }
    bossGroup.shutdownGracefully()
    workerGroup.shutdownGracefully()
  }
}