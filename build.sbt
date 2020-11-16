name := "scala-challenges"

version := "0.1"

scalaVersion := "2.13.3"

lazy val AkkaVersion = "2.6.10"

libraryDependencies ++= Seq(
  // FP (Cats etc)
  "org.typelevel" %% "cats-core" % "2.0.0",

  // Akka
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.2.1",

  "de.heikoseeberger" %% "akka-http-circe" % "1.34.0",
  "io.circe" %% "circe-core" % "0.13.0",
  "io.circe" %% "circe-generic" % "0.13.0",
  "io.circe" %% "circe-generic-extras" % "0.13.0",
  "io.circe" %% "circe-parser" % "0.13.0",
  "io.circe" %% "circe-optics" % "0.13.0",

  // Config
  "com.typesafe" % "config" % "1.4.0",

  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.8"
)

ThisBuild / resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)