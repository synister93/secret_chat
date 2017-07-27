package model.connection

import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManager, X509TrustManager}
import java.security.KeyStore
import java.security.cert.X509Certificate

import scala.util.Try

object SslConfig {

  def initSslContext(): SSLContext = {
    val sslContext = SSLContext.getInstance("TLSv1.2")
    Try {
      val certFile = getClass.getResource("/keystore_client")
      val keyStoreStream = certFile.openStream()
      val keyStore = KeyStore.getInstance("JKS")
      keyStore.load(keyStoreStream, "123321".toCharArray)
      keyStoreStream.close()
      val kmf = KeyManagerFactory.getInstance("SunX509")
      kmf.init(keyStore, "123321".toCharArray)
      sslContext.init(kmf.getKeyManagers, emptyTrustingManager, null)
    } recover {
      case ex: Exception => ex.printStackTrace()
    }
    sslContext
  }

  private def emptyTrustingManager: Array[TrustManager] = {
    Array[TrustManager](new X509TrustManager {
      override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}

      override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}

      override def getAcceptedIssuers: Array[X509Certificate] = null
    })
  }
}
