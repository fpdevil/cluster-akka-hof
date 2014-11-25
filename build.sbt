name:= "ClusterApp1"

version := "1.0"

organization := "com.cluster"

scalaVersion := "2.11.4"

EclipseKeys.withSource := true

resolvers ++= Seq("Typesafe Snapshots" at "http://repo.akka.io/snapshots/")

libraryDependencies ++= {
	val akkaVersion = "2.3.7"
	Seq(
		"com.typesafe.akka" %% "akka-actor"  % akkaVersion,
		"com.typesafe.akka" %% "akka-cluster" % akkaVersion,
		"com.typesafe.akka" %% "akka-contrib" % akkaVersion,
		// "com.typesafe.akka" %% "akka-multinode-testkit" % akkaVersion,
		"com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
		"com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
		"ch.qos.logback" % "logback-classic" % "1.1.2",
		"com.typesafe.akka" %% "akka-testkit" % akkaVersion,
		"org.scalatest" %% "scalatest" % "2.2.0"
		)
}