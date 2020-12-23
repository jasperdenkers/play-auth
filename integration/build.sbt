name := """play-auth-example"""

libraryDependencies ++= Seq(
  // Web assets
  "org.webjars" %% "webjars-play" % "2.8.0",
  "org.webjars" % "bootstrap"     % "3.3.7" exclude ("org.webjars", "jquery"),

  // Crypto
  "org.mindrot" % "jbcrypt" % "0.4",
  
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)
