import session.SessionGenerator

/**
 * Created by adambellemare on 2016-11-27.
 */
class SessionGeneratorTest extends SparkTestSuite {
  test("Basic count") { fixture =>
    val sourceRDD = fixture.sc.textFile("src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz")
    SessionGenerator(sourceRDD)
  }
}
