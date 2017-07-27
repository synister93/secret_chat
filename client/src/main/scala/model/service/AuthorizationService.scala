package model.service

import application.Configuration
import model.connection.TcpConnection
import model.service.ActionResults._

object AuthorizationService {

  var currentPid = 0L

  private val tcpConnection = TcpConnection

  def authorize(pid: String, password: String): ActionResult = {
    if (pid == null || pid.length == 0) {
      pidEmpty
    } else if (password == null || password.length == 0) {
      passwordForPidEmpty
    } else {
      val result = tcpConnection.authorize(pid, password)
      if (result.equals(successful)) {
        Configuration.currentPid = pid.toLong
      }
      result
    }
  }

  def register(passwordText: String, confirmPasswordText: String): ActionResult = {
    if (passwordText == null || passwordText.length == 0) {
      passwordEmpty
    } else if (confirmPasswordText == null || confirmPasswordText.length == 0) {
      confirmPasswordEmpty
    } else if (!passwordText.equals(confirmPasswordText)) {
      passwordsNotEqual
    } else {
      tcpConnection.register(passwordText)
    }
  }
}
