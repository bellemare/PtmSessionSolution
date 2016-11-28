import sbt._

object Version {

  val Spark        = "1.6.2"
  val log4j = "1.6.1"

  val slf4j        = "1.+"
  val ScalaTest    = "3.+"
  val Mockito      = "1.+"
  val Lombok       = "1.+"
  val Jcommander   = "1.48" //Breaking change at 1.52 (Java 8 only). 1.48 version is the last known Java 7 version.
  val JodaTime = "2.+"
  val JodaConvert = "1.+" //Resolves java.lang.AssertionError: assertion failed: org.joda.convert.FromString
  val GrizzledSlf4j = "1.3.0"
}