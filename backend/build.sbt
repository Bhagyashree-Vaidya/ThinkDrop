val scala3Version  = "3.3.3"
val http4sVersion  = "0.23.27"
val circeVersion   = "0.14.9"
val catsEffVersion = "3.5.4"

lazy val root = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name         := "thinkdrop-backend",
    version      := "0.1.0",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      // HTTP server
      "org.http4s"    %% "http4s-ember-server" % http4sVersion,
      "org.http4s"    %% "http4s-ember-client" % http4sVersion,
      "org.http4s"    %% "http4s-circe"        % http4sVersion,
      "org.http4s"    %% "http4s-dsl"          % http4sVersion,
      // JSON
      "io.circe"      %% "circe-generic"       % circeVersion,
      "io.circe"      %% "circe-parser"        % circeVersion,
      // Effect runtime
      "org.typelevel" %% "cats-effect"         % catsEffVersion,
      // Logging
      "ch.qos.logback" % "logback-classic"     % "1.5.6",
    ),

    // Assembly / run settings
    Compile / mainClass := Some("thinkdrop.Main"),
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),
  )
