package connection

import java.security.KeyStore
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import scala.util.Try

object SslConfig {

  private val PROTOCOL = "TLSv1.2"
  private val ALGORITHM = "SunX509"
  private val INSTANCE = "JKS"
  private val KEYSTORE = "/keystore_server"
  private val PASSWORD = "123321"


  def initContext: SSLContext = {
    val serverContext: SSLContext = SSLContext.getInstance(PROTOCOL)
    Try {
      val keystore = KeyStore.getInstance(INSTANCE)
      val keystoreStream = getClass.getResource(KEYSTORE).openStream()
      keystore.load(keystoreStream, PASSWORD.toCharArray)
      val kmf: KeyManagerFactory = KeyManagerFactory.getInstance(ALGORITHM)
      kmf.init(keystore, PASSWORD.toCharArray)
      val ts = TrustManagerFactory.getInstance(ALGORITHM)
      ts.init(keystore)
      keystoreStream.close()
      serverContext.init(kmf.getKeyManagers, ts.getTrustManagers, null)
    } recover {
      case ex: Exception => ex.printStackTrace()
    }
    serverContext
  }
}
