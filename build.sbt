import build.V

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / tlBaseVersion := "0.5"

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
val Scala33  = "3.3.4"
ThisBuild / crossScalaVersions := Seq(Scala213, Scala33)
ThisBuild / scalaVersion       := Scala213 // the default Scala

// hack until integration tests can run in parallel
// ThisBuild / Test / parallelExecution  := false
Global / concurrentRestrictions += Tags.limit(Tags.Test, 1)

lazy val projects = Seq(
  `flipt-sdk-server`,
  `flipt-sdk-server-it`,
  `openfeature-sdk`,
  `openfeature-sdk-circe`,
  `openfeature-sdk-otel4s`,
  `openfeature-provider-memory`,
  `openfeature-provider-java`,
  `openfeature-provider-java-it`,
  `openfeature-provider-flipt`,
  `openfeature-provider-flipt-it`,
  examples,
  docs
)

lazy val commonDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core"         % V.cats,
    "org.typelevel" %%% "cats-effect"       % V.catsEffect,
    "org.scalameta" %%% "munit"             % V.munit           % Test,
    "org.typelevel" %%% "munit-cats-effect" % V.munitCatsEffect % Test
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
    name := "flipt-sdk-server",
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-client" % V.http4s,
      "org.http4s" %%% "http4s-circe"  % V.http4s,
      "io.circe"   %%% "circe-core"    % V.circe,
      "io.circe"   %%% "circe-parser"  % V.circe,
      "io.circe"   %%% "circe-generic" % V.circe
    )
  )

lazy val `flipt-sdk-server-it` = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .enablePlugins(NoPublishPlugin)
  .in(file("flipt/sdk-server-it"))
  .settings(commonDependencies)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"  %%% "http4s-ember-client"        % V.http4s,
      "com.dimafeng" %% "testcontainers-scala-munit" % V.testcontainers % Test
    )
  )
  .dependsOn(`flipt-sdk-server`)

lazy val `openfeature-sdk` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("openfeature/sdk"))
  .settings(commonDependencies)
  .settings(
    name := "openfeature-sdk"
  )

lazy val `openfeature-sdk-circe` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("openfeature/sdk-circe"))
  .settings(commonDependencies)
  .settings(
    name := "openfeature-sdk-circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core"   % V.circe,
      "io.circe" %%% "circe-parser" % V.circe
    )
  )
  .dependsOn(`openfeature-sdk`)

lazy val `openfeature-sdk-otel4s` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("openfeature/sdk-otel4s"))
  .settings(commonDependencies)
  .settings(
    name := "openfeature-sdk-otel4s",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "otel4s-core-trace"  % V.otel4s,
      "org.typelevel" %%% "otel4s-sdk-testkit" % V.otel4s % Test
    )
  )
  .dependsOn(
    `openfeature-sdk`,
    `openfeature-provider-memory` % "test->test"
  )

lazy val `openfeature-provider-memory` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("openfeature/provider-memory"))
  .settings(commonDependencies)
  .settings(
    name := "openfeature-provider-memory"
  )
  .dependsOn(`openfeature-sdk`)

lazy val `openfeature-provider-java` = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("openfeature/provider-java"))
  .settings(commonDependencies)
  .settings(
    libraryDependencies ++= Seq(
      "dev.openfeature" % "sdk" % "1.10.0"
    )
  )
  .dependsOn(`openfeature-sdk`)

lazy val `openfeature-provider-java-it` = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("openfeature/provider-java-it"))
  .enablePlugins(NoPublishPlugin)
  .settings(commonDependencies)
  .settings(
    libraryDependencies ++= Seq(
      "dev.openfeature.contrib.providers" % "flagd" % "0.8.9" % Test,
      "com.dimafeng" %% "testcontainers-scala-munit" % V.testcontainers % Test
    )
  )
  .dependsOn(
    `openfeature-sdk-circe`,
    `openfeature-provider-java`
  )

lazy val `openfeature-provider-flipt` = crossProject(
  JVMPlatform,
  JSPlatform,
  NativePlatform
)
  .crossType(CrossType.Pure)
  .in(file("openfeature/provider-flipt"))
  .settings(commonDependencies)
  .settings(
    name := "openfeature-provider-flipt"
  )
  .dependsOn(
    // `openfeature-sdk`,
    `openfeature-sdk-circe`,
    `flipt-sdk-server`
  )

lazy val `openfeature-provider-flipt-it` = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("openfeature/provider-flipt-it"))
  .enablePlugins(NoPublishPlugin)
  .settings(commonDependencies)
  .settings(
    name := "openfeature-provider-flipt-it",
    libraryDependencies ++= Seq(
      "org.http4s"  %%% "http4s-ember-client"        % V.http4s         % Test,
      "com.dimafeng" %% "testcontainers-scala-munit" % V.testcontainers % Test,
      "io.circe"    %%% "circe-generic"              % V.circe          % Test
    )
  )
  .dependsOn(
    `openfeature-provider-flipt`
  )

lazy val examples = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("examples"))
  .enablePlugins(NoPublishPlugin)
  .dependsOn(`openfeature-provider-flipt`)

lazy val docs = project
  .in(file("site"))
  .enablePlugins(NoPublishPlugin, TypelevelSitePlugin)
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
      "org.http4s"                      %%% "http4s-ember-client" % V.http4s,
      "dev.openfeature.contrib.providers" % "flagd"               % "0.8.9"
    )
  )
  .dependsOn(
    `openfeature-sdk`.jvm,
    `openfeature-sdk-circe`.jvm,
    `openfeature-sdk-otel4s`.jvm,
    `openfeature-provider-java`.jvm,
    `openfeature-provider-flipt`.jvm
  )

addCommandAlias("fix", "headerCreateAll;scalafixAll;scalafmtAll;scalafmtSbt")
