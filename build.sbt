lazy val commonSettings = Seq(
  organization := "com.jasperdenkers",
  scalaVersion := "2.13.4",
  crossScalaVersions := Seq("2.12.12", "2.13.4")
)

lazy val core = (project in file("core")).settings(commonSettings: _*).enablePlugins(PlayScala)

lazy val integration = (project in file("integration")).settings(commonSettings: _*).dependsOn(core).enablePlugins(PlayScala)

Global / onChangedBuildSource := ReloadOnSourceChanges
