val akkaV       = "2.3.9"
val akkaStreamV = "1.0-M2"

lazy val buildSettings = Seq(
  organization := "com.softwaremill",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.5",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor"                        % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"       % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental"            % akkaStreamV
  ),
  scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation") ++ Seq("encoding", "utf8")
)

lazy val root = Project(
  id = "root",
  base = file("."),
  settings = buildSettings
).aggregate(macros, examples, tests)

lazy val macros = Project(
  id = "macros",
  base = file("macros"),
  settings = buildSettings ++ Seq(
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value // scalaV
  )
)

lazy val examples = Project(
  id = "examples",
  base = file("examples"),
  settings = buildSettings ++ Seq(
    libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV
  )
).dependsOn(macros)

// Based on Macwire's (https://github.com/adamw/macwire) CompileTests
lazy val tests = Project(
  id = "tests",
  base = file("tests"),
  settings = buildSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "test",
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % "test",
      "org.scalatest" %% "scalatest"  % "2.2.1" % "test",
      "pl.net.scala" %% "akka-http-routes-guard" % version.value % "test"
    ),
    // Otherwise when running tests in sbt, the macro is not visible
    // (both macro and usages are compiled in the same compiler run)
    fork in Test := true
  )
).dependsOn(macros)
