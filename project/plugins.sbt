// Play
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.1")

// Releasing
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.3.0")

// Sonatype publishing
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.10.0")

// PGP signing
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

// Formatting
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// Workaround for SBT1.8 (https://github.com/playframework/playframework/releases/tag/2.8.19)
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
