name := """play-auth-example"""

libraryDependencies ++= Seq(
  // Web assets
  "org.webjars"       %% "webjars-play" % "2.7.0",
  "org.webjars"       % "bootstrap"     % "3.3.7" exclude ("org.webjars", "jquery"),

  // Crypto
  "org.mindrot" % "jbcrypt" % "0.3m",
  
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
)
