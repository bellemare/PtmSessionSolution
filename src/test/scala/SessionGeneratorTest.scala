import common.SparkTestSuite
import parser.WebLogParser
import session.SessionGenerator

/**
 * Created by adambellemare on 2016-11-27.
 */
class SessionGeneratorTest extends SparkTestSuite {
  test("Basic count") { fixture =>
    val sourceRDD = fixture.sc.textFile("src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz")
    val webLogEvents = sourceRDD.flatMap(WebLogParser.parsePayTMAssignmentFormat(_))
    val sessions = SessionGenerator(webLogEvents)

    println(s"Number of sessions: ${sessions.count()}")

    val sessionLengthAverage = sessions.map(x => {
      x.endTime.getMillis - x.startTime.getMillis
    }).mean()

    println(s"Average session length in seconds = ${sessionLengthAverage/(1000)}")
    
  }
}
