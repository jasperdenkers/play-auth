name := """play-auth-example"""

libraryDependencies ++= Seq(
  // Web assets
  "org.webjars"       %% "webjars-play" % "2.5.0-4",
  "org.webjars"       % "bootstrap"     % "3.3.5" exclude ("org.webjars", "jquery"),

  // Crypto
  "org.mindrot" % "jbcrypt" % "0.3m",
  
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
