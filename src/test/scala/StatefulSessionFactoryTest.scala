/**
 * Created by adambellemare on 2016-11-28.
 */

import common.SparkTestSuite
import event.WebLogEvent
import org.joda.time.DateTime
import session.StatefulSessionFactory


class StatefulSessionFactoryTest extends SparkTestSuite {
  test("Session Factory Boundary Test") { fixture =>
    val USER_ID = "userId"

    val now = DateTime.now()

    val events = Seq( new WebLogEvent(now.minusSeconds(1801), USER_ID, "GET Request"),
                      new WebLogEvent(now.minusSeconds(1800), USER_ID, "GET Request"),
                      new WebLogEvent(now.minusSeconds(1799), USER_ID, "GET Request"),
                      new WebLogEvent(now, USER_ID, "GET Request"),
                      new WebLogEvent(now.plusSeconds(1800), USER_ID, "GET Request"),
                      new WebLogEvent(now.plusSeconds(1801), USER_ID, "GET Request"))

    val ssf = new StatefulSessionFactory(USER_ID, timeout = 1800000) //30 min timeout.

    //Iterate over the items and put them in the ssf. Get the items out at the end.
    ssf.addSeqOfEvents(events)

    //Get all the completed sessions from the state machine.
    val sessions = ssf.yieldCompletedSessions(includeCurrentActiveSession = true)

    val expectedSessionsValues = Set((USER_ID, 1801000L), (USER_ID, 1000L))

    sessions.map(x => {
      val detail = (x.userIP, x.getSessionTimeInMillis)
      assert(expectedSessionsValues.contains(detail))
    })
  }

}
