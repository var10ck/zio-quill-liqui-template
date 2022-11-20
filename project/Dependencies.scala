import sbt._

object Dependencies {

    object V {
        val kindProjector = "0.10.3"
        val scalamacros = "2.1.1"
        val http4s = "0.23.12"
        val zio = "2.0.2"
        val zhttp = "2.0.0-RC11"
        val zioJson = "0.3.0-RC10"
        val zioInteropCats = "3.3.0"
        val circe = "0.14.3"
        val scalaTest = "3.0.8"
        val randomDataGenerator = "2.8"
        val logback = "1.2.3"
        val h2database = "1.4.200"
        val quill = "4.6.0"
        val tapir = "1.1.2"
        val tapirZioHttpServer = "1.1.2"
        val openapiCirceYaml = "0.2.1"
        val scalaMigrations = "1.1.1"
        val postgres = "42.2.8"
        val liquibase = "3.4.2"
        val zioConfig = "3.0.1"
        val zioMagic = "0.3.11"
        val bcrypt = "4.3.0"
        val testContainers = "0.40.11"
        val zioPrelude = "1.0.0-RC16"
        val poi = "5.2.2"
    }

    lazy val kindProjector =
        "org.typelevel" %% "kind-projector" % V.kindProjector


    lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
    lazy val catsCore = "org.typelevel" %% "cats-core"  % "2.3.0"

//  lazy val catsEffect: Seq[ModuleID] = Seq(
//    "org.typelevel" %% "cats-effect" % CatsEffectVersion,
//    "org.typelevel" %% "cats-effect-kernel" % CatsEffectVersion,
//    "org.typelevel" %% "cats-effect-std" % CatsEffectVersion
//  )

//  lazy val fs2: Seq[ModuleID] = Seq(
//    "co.fs2" %% "fs2-core" % FS2Version,
//    "co.fs2" %% "fs2-io" % FS2Version
//  )

//  lazy val http4s: Seq[ModuleID] = Seq(
//    "org.http4s" %% "http4s-dsl" % Http4sVersion,
//    "org.http4s" %% "http4s-circe" % Http4sVersion,
//    "org.http4s" %% "http4s-ember-server" % Http4sVersion,
//    "org.http4s" %% "http4s-ember-client" % Http4sVersion
//  )

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core" % V.circe,
    "io.circe" %% "circe-parser" % V.circe,
    "io.circe" %% "circe-generic" % V.circe,
    "io.circe" %% "circe-literal" % V.circe
  )

    lazy val poi = "org.apache.poi" % "poi-ooxml" % V.poi

    lazy val zio: Seq[ModuleID] = Seq(
      "dev.zio" %% "zio"          % V.zio,
      "dev.zio" %% "zio-test"     % V.zio,
      "dev.zio" %% "zio-test-sbt" % V.zio,
      "dev.zio" %% "zio-macros"   % V.zio
    )

    lazy val zioConfig: Seq[ModuleID] = Seq(
      "dev.zio" %% "zio-config"          % V.zioConfig,
      "dev.zio" %% "zio-config-magnolia" % V.zioConfig,
      "dev.zio" %% "zio-config-typesafe" % V.zioConfig
    )

    lazy val liquibase = Seq("org.liquibase" % "liquibase-core" % V.liquibase)

    lazy val testContainers = Seq(
      "com.dimafeng" %% "testcontainers-scala-postgresql" % V.testContainers % Test,
      "com.dimafeng" %% "testcontainers-scala-scalatest"  % V.testContainers % Test
    )

    lazy val quill = Seq(
      "io.getquill" %% "quill-jdbc-zio"     % V.quill,
      "io.github.kitlangton" %% "zio-magic" % V.zioMagic,
      "org.postgresql"                      % "postgresql" % V.postgres
    )

    // http4s
//  lazy val http4sServer: Seq[ModuleID] = Seq(
//    "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
//    "org.http4s" %% "http4s-blaze-client" % Http4sVersion
//  )

    lazy val logback = "ch.qos.logback" % "logback-classic" % V.logback

    lazy val zioHttp = "io.d11" %% "zhttp" % V.zhttp

    lazy val zioJson = "dev.zio" %% "zio-json" % V.zioJson

    lazy val zioPrelude = "dev.zio" %% "zio-prelude" % V.zioPrelude

    lazy val bcrypt = "com.github.t3hnar"    %% "scala-bcrypt"   % V.bcrypt

//    lazy val tapir = Seq(
//      "com.softwaremill.sttp.tapir" %% "tapir-core"                      % V.tapir,
//      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"             % V.tapir,
//      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"           % V.tapirZioHttpServer,
//      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"         % V.tapir,
//      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"              % V.tapir,
//      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"            % V.openapiCirceYaml,
//      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"                % V.tapir,
//      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.8.2"
//    )

}
