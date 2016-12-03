name := """play-auth"""

homepage := Some(url("https://gitlab.com/jasperdenkers/play-auth"))

licenses := Seq("MIT" -> url("https://opensource.org/licenses/mit-license"))

libraryDependencies ++= Seq(
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@gitlab.com:jasperdenkers/play-auth.git</url>
    <connection>scm:git:git@gitlab.com:jasperdenkers/play-auth.git</connection>
    <developerConnection>scm:git:git@gitlab.com:jasperdenkers/play-auth.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>jasperdenkers</id>
      <name>Jasper Denkers</name>
      <url>http://jasperdenkers.com</url>
    </developer>
  </developers>
)