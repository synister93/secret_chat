package domain

import application.ResultsHandler._
import slick.driver.PostgresDriver.api._
import slick.lifted.Tag
import slick.profile.SqlProfile.ColumnOption.NotNull

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

object AppDomain {

  val configName = "Data"
  val pidsQuery = TableQuery[Pids]

  init()

  private def init(): Unit = {
    val db = Database.forConfig(configName)
    Try {
      Await.result(db.run(pidsQuery.schema.create), Duration.Inf)
    } recover {
      case ex: Exception => handleException(ex)
    }
    db.close()
  }

  class Pids(tag: Tag) extends Table[Pid](tag, "PIDS") {

    def pid = column[Long]("pid", O.PrimaryKey)

    def password = column[String]("password", NotNull)

    def * = (pid, password) <> (Pid.tupled, Pid.unapply)

  }

  case class Pid(pid: Long, password: String)

}