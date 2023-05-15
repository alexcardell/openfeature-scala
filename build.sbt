import dependencies._

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

lazy val commonSettings = Seq(
  addCompilerPlugin(
    "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
  ),
  scalacOptions := Seq("-Wunused")
)

lazy val commonDepSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.8.0"
  ),
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-effect" % "3.3.14",
    "com.disneystreaming" %%% "weaver-cats" % "0.8.0"
  ).map(_ % Test)
)

lazy val root = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .aggregate(core, mtl)
  .settings(
    name := "ff4s",
    moduleName := "ff4s"
  )

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(commonSettings: _*)
  .settings(commonDepSettings: _*)
  .settings(
    moduleName := "ff4s-core",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )

lazy val mtl = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("mtl"))
  .settings(commonSettings: _*)
  .settings(commonDepSettings: _*)
  .settings(
    moduleName := "ff4s-mtl",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-mtl" % "1.3.0"
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    publishArtifact := false
  )
  .dependsOn(core)
