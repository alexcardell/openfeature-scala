import dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "io.cardell"
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/alexcardell/ff4s"),
    "scm:git@github.com:alexcardell/ff4s.git"
  )
)
ThisBuild / licenses := List(
  "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
)
ThisBuild / developers := List(
  Developer(
    "alexcardell",
    "Alex Cardell",
    "29524087+alexcardell@users.noreply.github.com",
    url("https://github.com/alexcardell")
  )
)
ThisBuild / homepage := Some(
  url("https://github.com/alexcardell/ff4s")
)
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / versionScheme := Some("early-semver")

lazy val scala2_12 = "2.12.15"
lazy val scala2_13 = "2.13.8"
lazy val scala3_1 = "3.1.3"
lazy val scala3_2 = "3.2.0"
lazy val crossVersions = Seq(scala2_13)
ThisBuild / crossScalaVersions := crossVersions
ThisBuild / scalaVersion := scala2_13

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val V = new {
  val catsEffect = "3.3.14"
}

lazy val commonSettings = Seq(
  addCompilerPlugin(
    "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
  ),
  scalacOptions := Seq("-Wunused", "-Xlint"),
  testFrameworks += new TestFramework("weaver.framework.CatsEffect")
)

lazy val commonDepSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.8.0",
    "org.typelevel" %%% "cats-effect-kernel" % V.catsEffect
  ),
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-effect" % V.catsEffect,
    "com.disneystreaming" %%% "weaver-cats" % "0.8.0"
  ).map(_ % "test,it")
)

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "ff4s",
      moduleName := "ff4s"
    )
    .aggregate(
      Seq(
        core,
        flipt,
        fliptJavaSdk,
        examples
      ).map(_.projectRefs).flatten: _*
    )

lazy val core =
  projectMatrix
    .in(file("core"))
    .jvmPlatform(crossVersions)
    .jsPlatform(crossVersions)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)
    .settings(commonSettings: _*)
    .settings(commonDepSettings: _*)
    .settings(
      moduleName := "ff4s-core"
    )

lazy val flipt =
  projectMatrix
    .in(file("flipt"))
    .enablePlugins(Smithy4sCodegenPlugin)
    .jvmPlatform(crossVersions)
    .jsPlatform(crossVersions)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)
    .settings(commonSettings: _*)
    .settings(commonDepSettings: _*)
    .settings(
      moduleName := "ff4s-flipt",
      libraryDependencies ++= Seq(
        "com.disneystreaming.smithy4s" %%% "smithy4s-core" % smithy4sVersion.value,
        "com.disneystreaming.smithy4s" %%% "smithy4s-http4s" % smithy4sVersion.value,
        "com.disneystreaming.alloy" % "alloy-core" % "0.2.3",
        "com.disneystreaming.smithy" % "smithytranslate-traits" % "0.3.9",
        "org.http4s" %%% "http4s-ember-client" % "0.23.23"
      )
    )
    .dependsOn(core)

lazy val fliptJavaSdk =
  projectMatrix
    .in(file("flipt-java-sdk"))
    .jvmPlatform(crossVersions)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)
    .settings(commonSettings: _*)
    .settings(commonDepSettings: _*)
    .settings(
      moduleName := "ff4s-flipt-java-sdk",
      libraryDependencies ++= Seq(
        "io.flipt" % "flipt-java" % "0.2.6"
      )
    )
    .dependsOn(core)

lazy val examples =
  projectMatrix
    .in(file("examples"))
    .jvmPlatform(crossVersions)
    .configs(IntegrationTest)
    .settings(commonSettings: _*)
    .settings(commonDepSettings: _*)
    .settings(
      moduleName := "ff4s-examples",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-effect" % V.catsEffect
      )
    )
    .dependsOn(core, flipt) //, fliptJavaSdk)

addCommandAlias("fix", "scalafixAll; scalafmtAll; scalafmtSbt")
addCommandAlias("check", "scalafmtCheckAll; scalafmtSbtCheck; scalafix --check")
