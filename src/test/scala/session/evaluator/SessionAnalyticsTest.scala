package session.evaluator

import common.SparkTestSuite
import event.WebLogEvent
import org.joda.time.DateTime
import parser.WebLogParser
import session.SessionGenerator

/**
 * Created by adambellemare on 2016-11-28.
 */
class SessionAnalyticsTest extends SparkTestSuite {
  test("Most Engaged Users Test") { fixture =>

    val sourceRDD = fixture.sc.textFile("src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz")
    val webLogEvents = sourceRDD.flatMap(WebLogParser.parseWebLogEvents(_))
    val sessions = SessionGenerator(webLogEvents)

    val mostEngagedUsers = SessionAnalytics.getMostEngagedUsers(sessions)
    mostEngagedUsers.foreach(println)
  }

  test("Test the unique visits in a session.") { fixture =>
    val sc = fixture.sc
    val sessionEvents = Seq(new WebLogEvent(DateTime.now(), "a", "GET www.abc.com"),
      new WebLogEvent(DateTime.now().plusSeconds(100), "a", "GET www.bbc.com"),
      new WebLogEvent(DateTime.now().plusSeconds(200), "a", "GET www.abc.com/rootdir"),
      new WebLogEvent(DateTime.now().plusSeconds(300), "a", "GET www.abc.com"))

    val session = SessionGenerator(sc.parallelize(sessionEvents))

    val singleSession = session.collect().head

    println(s"Unique URLs visited = ${singleSession.getUniqueURLVisits}")

    assert(singleSession.getUniqueURLVisits.length == 3)

  }
}
