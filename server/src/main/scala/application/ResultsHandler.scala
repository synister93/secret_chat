package application

import connection.Messages
import org.slf4j.LoggerFactory

object ResultsHandler {

  private val log = LoggerFactory.getLogger(this.getClass)

  type ActionResult = (Int, String)

  val successResult: ActionResult = (0, "")
  val systemError: ActionResult = (-1, "System error")
  val pidNotExists: ActionResult = (1, "Pid not exists")
  val incorrectPassword: ActionResult = (2, "Incorrect password")

  private val successEntry = successResult -> Messages.SUCCESS
  private val authorizationErrorNotValidPassword = incorrectPassword -> Messages.AUTHORIZATION_ERROR_NOT_VALID_PASSWORD
  private val pidNotExistsEntry = pidNotExists -> Messages.AUTHORIZATION_ERROR_PID_NOT_EXISTS
  private val systemErrorEntry = systemError -> Messages.SYSTEM_ERROR

  val messageResultMap = Map(authorizationErrorNotValidPassword, successEntry, pidNotExistsEntry, systemErrorEntry)

  def handleException(ex: Throwable): Unit = {
    log.error(systemError._2, ex)
  }

  case class ResultTo(result: ActionResult, data: Option[Any])

}
