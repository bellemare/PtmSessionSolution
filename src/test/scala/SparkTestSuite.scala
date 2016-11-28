import org.apache.spark.SparkContext
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.{Outcome, fixture}

/**
 * Creates a Spark context that can be passed to any test in a subclass of this suite.
 */
class SparkTestSuite extends fixture.FunSuite {
  case class FixtureParam(sc: SparkContext)

  override protected def withFixture(test: OneArgTest): Outcome = {
    //The sparkcontext to use in the test.
    val sc = new SparkContext("local[4]", "Test")
    try {
      println("Created test spark context.. make sure you have 'parallelExecution in Test := false' in your build.sbt")

      // Wrap the sparkcontext in a Fixture param that will be passed into the tests
      val scFixture = FixtureParam(sc)

      // Inject the fixture param into the test
      withFixture(test.toNoArgTest(scFixture))

    } finally {
      sc.stop()
      println("Stopped test spark context.")
    }
  }
}
