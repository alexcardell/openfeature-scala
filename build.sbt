// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.0" // your current series x.y

ThisBuild / organization := "io.cardell"
ThisBuild / organizationName := "Alex Cardell"
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("alexcardell", "Alex Cardell")
)

// publish to s01.oss.sonatype.org (set to true to publish to oss.sonatype.org instead)
ThisBuild / tlSonatypeUseLegacyHost := false

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / tlSiteKeepFiles := false

val Scala213 = "2.13.12"
val Scala33 = "3.3.3"
ThisBuild / crossScalaVersions := Seq(Scala213, Scala33)
ThisBuild / scalaVersion := Scala213 // the default Scala

lazy val projects = Seq(
  `flipt-sdk-server`
)

lazy val commonDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.10.0",
    "org.typelevel" %%% "cats-effect" % "3.5.3",
    "org.scalameta" %%% "munit" % "1.0.0-RC1" % "test,it",
    "org.typelevel" %%% "munit-cats-effect" % "2.0.0-M5" % "test,it"
  )
)

lazy val root = tlCrossRootProject.aggregate(projects: _*)

lazy val `flipt-sdk-server` =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Full)
    .in(file("flipt/sdk-server"))
    .settings(commonDependencies)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)
    .settings(
      name := "ff4s-flipt-sdk-server",
      libraryDependencies ++= Seq(
        "org.http4s" %%% "http4s-client" % "0.23.26",
        "org.http4s" %%% "http4s-ember-client" % "0.23.26",
        "org.http4s" %%% "http4s-circe" % "0.23.26",
        "io.circe" %%% "circe-generic" % "0.14.7",
        "com.dimafeng" %% "testcontainers-scala-munit" % "0.41.3" % "it"
      ),
      IntegrationTest / fork := true
    )

lazy val docs =
  project
    .in(file("site"))
    .enablePlugins(TypelevelSitePlugin)
    .settings(
      tlSiteHelium := {
        import laika.helium.config.IconLink
        import laika.helium.config.HeliumIcon
        import laika.ast.Path.Root
        tlSiteHelium.value.site.topNavigationBar(
          homeLink = IconLink.internal(Root / "index.md", HeliumIcon.home)
        )
      }
    )
