addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.4")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.13.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")
resolvers += "Artifactory Realm" at "https://artifacts.ws.contitech.cloud/artifactory/ctdna-sbt"
credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
addSbtPlugin("ct.dna" % "dna-build-tools" % "17.3.1")
addDependencyTreePlugin