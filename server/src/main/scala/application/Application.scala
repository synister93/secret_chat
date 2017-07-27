package application

import com.typesafe.config.ConfigFactory
import connection.NettyConnectionServer

object Application extends App {
  val fqdn = ConfigFactory.load.getString("application.fqdn")
  NettyConnectionServer.init()
  RedisClient
}
