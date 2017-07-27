package model.connection
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.ssl.SslHandler
import javax.net.ssl.SSLEngine

class NettyClientInitializer extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline
    val engine = SslConfig.initSslContext.createSSLEngine
    engine.setUseClientMode(true)
    pipeline.addLast("ssl", new SslHandler(engine))
    pipeline.addLast("length-decoder", new LengthFieldBasedFrameDecoder(16388, 0, 4, 0, 4))
    pipeline.addLast("bytearray-decoder", new ByteArrayDecoder)
    pipeline.addLast("length-encoder", new LengthFieldPrepender(4))
    pipeline.addLast("bytearray-encoder", new ByteArrayEncoder)
    pipeline.addLast(new Handler)
  }

}
