ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"


val Http4sVersion = "1.0.0-M4"
val ZioVersion = "1.0.8"

lazy val root = (project in file("."))
  .settings(
    name := "cinema-fuentes-backend",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      // JSON Mapping
      "io.circe" %% "circe-generic" % "0.15.0-M1",
      "io.circe" %% "circe-literal" % "0.15.0-M1",
      // ZIO stuff
      "dev.zio" %% "zio" % ZioVersion,
      "dev.zio" %% "zio-interop-cats" % "2.4.0.0",
      "joda-time" % "joda-time" % "2.10.13"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

