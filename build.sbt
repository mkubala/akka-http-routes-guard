name := "akka-http-routes-guard"

organization := "pl.net.scala"

version := "0.1-SNAPSHOT"

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation") ++ Seq("encoding", "utf8")

scalaVersion in ThisBuild := scalaV

lazy val scalaV = "2.11.5"

lazy val buildSettings = Seq(
  libraryDependencies ++= {
    val akkaV       = "2.3.9"
    val akkaStreamV = "1.0-M2"
    Seq(
      "com.typesafe.akka" %% "akka-actor"                        % akkaV,
      "com.typesafe.akka" %% "akka-stream-experimental"          % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-core-experimental"       % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-experimental"            % akkaStreamV,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV
    )
  }
)

lazy val root = Project(
  id = "root",
  base = file("."),
  settings = buildSettings
).aggregate(macros, examples)

lazy val macros = Project(
  id = "macros",
  base = file("macros"),
  settings = buildSettings ++ Seq(
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaV
  )
)

lazy val examples = Project(
  id = "examples",
  base = file("examples"),
  settings = buildSettings
).dependsOn(macros)

lazy val tests = Project(
  id = "tests",
  base = file("tests"),
  settings = buildSettings
).dependsOn(macros)
