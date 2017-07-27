package model.connection

object Messages extends Enumeration {

  type MessagesEnum = Value
  val SUCCESS, MESSAGE, REGISTER, AUTHORIZATION, SYSTEM_ERROR, AUTHORIZATION_ERROR_NOT_VALID_PASSWORD, AUTHORIZATION_ERROR_PID_NOT_EXISTS = Value
}
