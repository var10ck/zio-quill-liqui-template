ThisBuild / scalaVersion     := "2.13.9"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.var10ck"
ThisBuild / organizationName := "var10ck"
ThisBuild / name             := "zio-quill-liqui-template"

lazy val root = (project in file("."))
    .settings(
        name := "zio-quill-liqui-template",
        libraryDependencies ++= Dependencies.zio ++
            Dependencies.zioConfig ++
            Dependencies.liquibase ++
            Dependencies.testContainers ++
            Dependencies.quill ++
            Dependencies.circe ++
            //          Dependencies.tapir ++
            Seq(
                Dependencies.zioHttp,
                Dependencies.scalaTest,
                //            Dependencies.catsCore,
                Dependencies.logback,
                Dependencies.zioJson,
                Dependencies.bcrypt,
                Dependencies.zioPrelude,
                Dependencies.poi
            ),
        testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )


enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

Docker / packageName        := "zio-quill-liqui-template"