addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.6")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.6")
resolvers += "Artifactory Realm" at "https://artifacts.ws.contitech.cloud/artifactory/ctdna-sbt"
credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
addSbtPlugin("ct.dna" % "dna-build-tools" % "17.3.11")
addDependencyTreePlugin