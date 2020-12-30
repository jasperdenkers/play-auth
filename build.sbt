lazy val commonSettings = Seq(
  organization := "com.jasperdenkers",
  scalaVersion := "2.13.4",
  crossScalaVersions := Seq("2.12.12", "2.13.4")
)

lazy val core = (project in file("core")).settings(commonSettings: _*).enablePlugins(PlayScala)

lazy val integration = (project in file("integration")).settings(commonSettings: _*).dependsOn(core).enablePlugins(PlayScala)

import xerial.sbt.Sonatype._

sonatypeProjectHosting := Some(GitLabHosting("jasperdenkers", "play-auth", "jasperdenkers@gmail.com"))

developers := List(
  Developer(
    id="jasperdenkers",
    name="Jasper Denkers",
    email="jasperdenkers@gmailcom",
    url=url("http://jasperdenkers.com")
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges
