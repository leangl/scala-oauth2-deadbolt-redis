name := """oauth-deadbolt"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  //cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "be.objectify" %% "deadbolt-java" % "2.5.0",
  "be.objectify" %% "deadbolt-scala" % "2.5.0",
  "com.typesafe.play.modules" %% "play-modules-redis" % "2.5.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.nulab-inc" %% "play2-oauth2-provider" % "0.17.2"
)

resolvers += Resolver.mavenLocal
resolvers += Resolver.jcenterRepo
resolvers += "sedis-fix" at "https://dl.bintray.com/graingert/maven/"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")
