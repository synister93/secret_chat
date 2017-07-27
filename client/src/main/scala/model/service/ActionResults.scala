package model.service

import model.connection.Messages
import org.slf4j.LoggerFactory

object ActionResults {

  type ActionResult = (Int, String)

  val successful: ActionResult = (0, "Успешное выполнение!")
  val systemError: ActionResult = (-1, "System error")
  val passwordEmpty: ActionResult = (1, "Введите пароль!")
  val confirmPasswordEmpty: ActionResult = (2, "Повторите ввод пароля!")
  val passwordsNotEqual: ActionResult = (3, "Введенные пароли не совпадают!")
  val pidEmpty: ActionResult = (1, "Введите PID!")
  val passwordForPidEmpty: ActionResult = (2, "Введите пароль!")
  val passwordIncorrect: ActionResult = (3, "Неправильный пароль!")
  val pidNotExists: ActionResult = (4, "Введенный PID не существует!")

  private val authorizationErrorNotValidPassword = Messages.AUTHORIZATION_ERROR_NOT_VALID_PASSWORD.toString -> passwordIncorrect
  private val pidNotExistsEntry = Messages.AUTHORIZATION_ERROR_PID_NOT_EXISTS.toString -> pidNotExists
  private val success = Messages.SUCCESS.toString -> successful
  private val systemErrorEntry = Messages.SYSTEM_ERROR.toString -> systemError

  private val log = LoggerFactory.getLogger(this.getClass)

  val messageResultMap = Map(authorizationErrorNotValidPassword, success, systemErrorEntry, pidNotExistsEntry)

  def handleException(ex: Throwable): Unit = {
    log.error(systemError._2, ex)
  }
}