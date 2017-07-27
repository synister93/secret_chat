package application

object Configuration {

  var currentPid: Long = 0L

  def isAuthorized = {
    currentPid != 0L
  }
}
