name := """play-auth"""

homepage := Some(url("https://gitlab.com/jasperdenkers/play-auth"))

licenses := Seq("MIT" -> url("https://opensource.org/licenses/mit-license"))

libraryDependencies ++= Seq(
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)

import xerial.sbt.Sonatype._

sonatypeProjectHosting := Some(
  GitLabHosting(
    "jasperdenkers",
    "play-auth",
    "jasperdenkers@gmail.com"
  )
)

developers := List(
  Developer(
    id="jasperdenkers",
    name="Jasper Denkers",
    email="jasperdenkers@gmailcom",
    url=url("http://jasperdenkers.com")
  )
)

publishTo := sonatypePublishToBundle.value

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

import ReleaseTransformations._

releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)