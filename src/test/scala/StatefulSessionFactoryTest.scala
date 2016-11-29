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

    val events = Seq(//new WebLogEvent(now.minusSeconds(599), USER_ID, "GET Request"),
                     new WebLogEvent(now, USER_ID, "GET Request"),
      new WebLogEvent(now.plusSeconds(601), USER_ID, "GET Request"),
      new WebLogEvent(now.plusSeconds(602), USER_ID, "GET Request"),
      new WebLogEvent(now.plusSeconds(603), USER_ID, "GET Request"),
      new WebLogEvent(now.plusSeconds(1603), USER_ID, "GET Request"))

    val ssf = new StatefulSessionFactory(USER_ID)
    //Iterate over the items and put them in the ssf. Get the items out at the end.
    ssf.addSeqOfEvents(events)

    //Get all the completed sessions from the state machine.
    val sessions = ssf.yieldCompletedSessions(includeCurrentActiveSession = true)

    sessions.foreach(println(_))
  }

}
