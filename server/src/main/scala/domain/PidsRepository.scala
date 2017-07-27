package domain

import domain.AppDomain.Pid
import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object PidsRepository {

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def registerPid(newPid: Long, password: String) = {
    val pid = Pid(newPid, password)
    val db = Database.forConfig(AppDomain.configName)
    Await.result(db.run(AppDomain.pidsQuery.insertOrUpdate(pid).transactionally), Duration.Inf)
    db.close()
  }

  def findPid(checkingPid: Long): Option[Pid] = {
    val db = Database.forConfig(AppDomain.configName)
    val pidsQuery = AppDomain.pidsQuery
    val result = Await.result(db.run(pidsQuery.filter(_.pid === checkingPid).result), Duration.Inf).headOption
    db.close()
    result
  }
}
