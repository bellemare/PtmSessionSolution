import common.SparkTestSuite
import parser.WebLogParser
import session.SessionGenerator
import session.evaluator.SessionAnalytics

/**
 * Created by adambellemare on 2016-11-27.
 */
class SessionGeneratorTest extends SparkTestSuite {
  test("Basic count") { fixture =>
    val sourceRDD = fixture.sc.textFile("src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz")
    val webLogEvents = sourceRDD.flatMap(WebLogParser.parseWebLogEvents)
    val sessions = SessionGenerator(webLogEvents)

    println(s"Number of sessions: ${sessions.count()}")

    val sessionLengthAverage = SessionAnalytics.calculateMean(sessions)
    println(s"Average session length in seconds = ${sessionLengthAverage/1000}")

    val sessionLengthAverage2 = SessionAnalytics.calculateMean(sessions, ignoreSingleEventSessions = true)
    println(s"Average session length in seconds = ${sessionLengthAverage2/1000}")
    
  }
}
