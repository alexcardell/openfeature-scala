// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.1" // your current series x.y

ThisBuild / organization     := "io.cardell"
ThisBuild / organizationName := "Alex Cardell"
ThisBuild / startYear        := Some(2023)
ThisBuild / licenses         := Seq(License.Apache2)

ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("alexcardell", "Alex Cardell")
)

// publish to s01.oss.sonatype.org (set to true to publish to oss.sonatype.org instead)
ThisBuild / tlSonatypeUseLegacyHost := false

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / tlSiteKeepFiles     := false

val Scala213 = "2.13.12"
val Scala33  = "3.3.3"
ThisBuild / crossScalaVersions := Seq(Scala213, Scala33)
ThisBuild / scalaVersion       := Scala213 // the default Scala

// hack until integration tests can run in parallel
// ThisBuild / Test / parallelExecution  := false
Global / concurrentRestrictions += Tags.limit(Tags.Test, 1)

lazy val projects = Seq(
  `flipt-sdk-server`,
  `flipt-sdk-server-it`,
  `open-feature-sdk`,
  `open-feature-provider-flipt`,
  `open-feature-provider-flipt-it`,
  docs
)

lazy val commonDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core"         % "2.10.0",
    "org.typelevel" %%% "cats-effect"       % "3.5.3",
    "org.scalameta" %%% "munit"             % "1.0.0-RC1" % Test,
    "org.typelevel" %%% "munit-cats-effect" % "2.0.0-M5"  % Test
  )
)

lazy val root = tlCrossRootProject.aggregate(projects: _*)

lazy val `flipt-sdk-server` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Full)
  .in(file("flipt/sdk-server"))
  .settings(commonDependencies)
  .settings(
    name := "ff4s-flipt-sdk-server",
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-client" % "0.23.26",
      "org.http4s" %%% "http4s-circe"  % "0.23.26",
      "io.circe"   %%% "circe-core"    % "0.14.7",
      "io.circe"   %%% "circe-parser"  % "0.14.7",
      "io.circe"   %%% "circe-generic" % "0.14.7"
    )
  )

lazy val `flipt-sdk-server-it` = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("flipt/sdk-server-it"))
  .settings(commonDependencies)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"  %%% "http4s-ember-client"        % "0.23.26",
      "com.dimafeng" %% "testcontainers-scala-munit" % "0.41.3" % Test
    )
  )
  .dependsOn(`flipt-sdk-server`)

lazy val `open-feature-sdk` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("open-feature/sdk"))
  .enablePlugins(NoPublishPlugin)
  .settings(commonDependencies)
  .settings(
    name := "ff4s-open-feature-sdk",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-generic" % "0.14.7"
    )
  )

lazy val `open-feature-provider-flipt` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("open-feature/provider-flipt"))
  .enablePlugins(NoPublishPlugin)
  .settings(commonDependencies)
  .settings(
    name := "ff4s-open-feature-provider-flipt"
  )
  .dependsOn(
    `open-feature-sdk`,
    `flipt-sdk-server`
  )

lazy val `open-feature-provider-flipt-it` = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("open-feature/provider-flipt-it"))
  .enablePlugins(NoPublishPlugin)
  .settings(commonDependencies)
  .settings(
    name := "ff4s-open-feature-provider-flipt-it",
    libraryDependencies ++= Seq(
      "org.http4s"  %%% "http4s-ember-client"        % "0.23.26",
      "com.dimafeng" %% "testcontainers-scala-munit" % "0.41.3" % Test
    )
  )
  .dependsOn(
    `open-feature-provider-flipt`
  )

lazy val examples = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("examples"))
  .enablePlugins(NoPublishPlugin)
  .dependsOn(`open-feature-provider-flipt`)

lazy val docs = project
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
    },
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-ember-client" % "0.23.26"
    )
  )
  .dependsOn(`open-feature-provider-flipt`.jvm)

addCommandAlias("fix", "headerCreateAll;scalafixAll;scalafmtAll;scalafmtSbt")
