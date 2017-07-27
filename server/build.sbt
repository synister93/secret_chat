name := "server"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies += "io.netty" % "netty-all" % "4.1.11.Final"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.10"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.1.0"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1206-jdbc42"
libraryDependencies += "com.typesafe.play" % "play-logback_2.11" % "2.5.4"
libraryDependencies += "com.github.etaty" %% "rediscala" % "1.8.0"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.4.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xcheckinit", "-encoding", "utf8", "-feature")
        