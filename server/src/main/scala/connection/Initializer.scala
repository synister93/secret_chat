package connection

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.{LengthFieldBasedFrameDecoder, LengthFieldPrepender}
import io.netty.handler.codec.bytes.{ByteArrayDecoder, ByteArrayEncoder}
import io.netty.handler.ssl.SslHandler

class Initializer extends ChannelInitializer[SocketChannel] {

  private val CHUNK_SIZE_FRAME_LENTGTH = 4

  @throws[Exception]
  override def initChannel(ch: SocketChannel) {
    val pipeline = ch.pipeline()
    val sslContext = SslConfig.initContext
    val engine = sslContext.createSSLEngine()
    engine.setUseClientMode(false)
    engine.setNeedClientAuth(true)
    pipeline.addLast("ssl", new SslHandler(engine))
    pipeline.addLast("length-decoder", new LengthFieldBasedFrameDecoder(1988993, 0, CHUNK_SIZE_FRAME_LENTGTH, 0, CHUNK_SIZE_FRAME_LENTGTH))
    pipeline.addLast("bytearray-decoder", new ByteArrayDecoder)
    pipeline.addLast("length-encoder", new LengthFieldPrepender(CHUNK_SIZE_FRAME_LENTGTH))
    pipeline.addLast("bytearray-encoder", new ByteArrayEncoder)
    pipeline.addLast(new MessagesHandler)
  }

}
