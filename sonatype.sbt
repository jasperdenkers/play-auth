credentials ++= (for {
  username <- sys.env.get("SONATYPE_USERNAME")
  password <- sys.env.get("SONATYPE_PASSWORD")
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq

pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray)

pgpSecretRing := file(sys.env.getOrElse("PGP_PATH", sys.props.apply("user.home") + "/.sbt/gpg") + "/secring.asc")

pgpPublicRing := file(sys.env.getOrElse("PGP_PATH", sys.props.apply("user.home") +  "/.sbt/gpg") + "/pubring.asc")