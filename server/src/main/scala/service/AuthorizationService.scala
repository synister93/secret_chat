package service

import java.util.concurrent.ThreadLocalRandom

import application.{RedisClient, ResultsHandler}
import application.ResultsHandler._
import domain.PidsRepository

import scala.util.Try

object AuthorizationService {

  private val pidsRepository = PidsRepository
  private val redisClient = RedisClient

  def registerPid(password: String): ActionResult = {
    Try {
      val newPid = generateNewPid()
      pidsRepository.registerPid(newPid, password)
    } recover {
      case ex: Exception => {
        handleException(ex)
        systemError
      }
    }
    successResult
  }

  def authorize(checkingPid: Long, password: String): ResultTo = {
    var result = successResult
    var pidResult = 0L
    Try {
      val pidOption = pidsRepository.findPid(checkingPid)
      pidOption match {
        case Some(pid) => {
          if (!pid.password.equals(password)) {
            result = incorrectPassword
          } else {
            pidResult = pid.pid
          }
        }
        case None => result = pidNotExists
      }
    } recover {
      case ex: Exception => {
        ResultsHandler.handleException(ex)
        systemError
      }
    }
    ResultTo(result, Option(pidResult))
  }

  def offline (pid: Long): Unit = {
    redisClient.offline(pid)
  }

  def online(pid: Long): Unit = {
    redisClient.online(pid)
  }

  private def generateNewPid(): Long = {
    ThreadLocalRandom.current().nextLong(100000000L, 1000000000L)
  }
}