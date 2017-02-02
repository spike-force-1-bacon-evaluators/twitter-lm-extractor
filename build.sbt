name := "twitter-lm-extractor"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += Resolver.sonatypeRepo("releases")

// Twitter4Scala API
libraryDependencies += "com.danielasfregola" %% "twitter4s" % "4.2"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

// https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
// Added to bypass the "Failed to load class org.slf4j.impl.StaticLoggerBinder" error.
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.22"

// https://mvnrepository.com/artifact/org.scalatest/scalatest_2.11
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1"

