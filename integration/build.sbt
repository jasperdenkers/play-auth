name := """play-auth-example"""

libraryDependencies ++= Seq(
  // Web assets
  "org.webjars" %% "webjars-play" % "2.9.1",
  "org.webjars"  % "bootstrap"    % "3.3.7" exclude ("org.webjars", "jquery"),

  // Crypto
  "org.mindrot" % "jbcrypt" % "0.4",
  
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0" % Test
)
