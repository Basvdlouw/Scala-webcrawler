import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.7",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "Webcrawler",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0-M3" ,
    libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3",
    resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
    resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
  )

//    libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4-SNAPSHOT",
