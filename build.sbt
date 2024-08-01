ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-typed" % "2.8.0"


lazy val root = (project in file("."))
  .settings(
    name := "Test"
  )
