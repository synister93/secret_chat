name := "Chat client"

version := "1.0.0.0"

scalaVersion := "2.11.11"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.3"
libraryDependencies += "commons-io" % "commons-io" % "2.5"
libraryDependencies += "io.netty" % "netty-all" % "4.1.11.Final"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.4"
libraryDependencies += "com.typesafe.play" % "play-logback_2.11" % "2.5.4"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.4.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xcheckinit", "-encoding", "utf8", "-feature")
mainClass in assembly := Some("application.Application")