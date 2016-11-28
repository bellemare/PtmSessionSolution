name := "PtmSessionSolution"

version := "20161127.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  Resolver.mavenLocal,
  "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
  "Spray Repository" at "http://repo.spray.cc/",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "A second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Twitter4J Repository" at "http://twitter4j.org/maven2/",
  "Twitter Maven Repo" at "http://maven.twttr.com/",
  "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
  Resolver.sonatypeRepo("public")
)

libraryDependencies ++= Seq(
  "org.projectlombok" %  "lombok"          % "1.+",
  "com.beust"         %  "jcommander"      % "1.48", //1.48 is last known good Java 7 implementation.
  "org.apache.spark"  %% "spark-core"      % "1.6.2",
  // Testing and Mocking
  "org.scalatest"     %% "scalatest"       % "3.+",
  // Project specific
  "joda-time"         % "joda-time"        % "2.+",
  "org.clapper"       %% "grizzled-slf4j"  % "1.3.0"
)

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.first
  case PathList("org", "jboss", xs @ _*) => MergeStrategy.first
  case "about.html"  => MergeStrategy.rename
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

parallelExecution in Test := false
