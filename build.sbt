lazy val commonSettings = Seq(
  organization := "com.jasperdenkers",
  scalaVersion := "2.11.8"
)

lazy val core = (project in file("core")).settings(commonSettings: _*).enablePlugins(PlayScala)

lazy val integration = (project in file("integration")).settings(commonSettings: _*).dependsOn(core).enablePlugins(PlayScala)