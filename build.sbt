import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "TestTechniqueScala",
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

parallelExecution in Test := false

wartremoverWarnings ++= Warts.all

//scalacOptions ++= Seq(
//  "-deprecation",
//  "-encoding",
//  "UTF-8", // yes, this is 2 args
//  "-feature",
//  "-language:existentials",
//  "-language:higherKinds",
//  "-language:implicitConversions",
//  "-unchecked",
//  "-Xfatal-warnings",
//  "-Xlint",
//  "-Yno-adapted-args",
//  "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
//  "-Ywarn-numeric-widen",
//  "-Ywarn-value-discard",
//  "-Xfuture",
//  "-Ywarn-unused-import", // 2.11 only
//  "-Ypartial-unification"
//)