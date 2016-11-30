package session

import event.WebLogEvent
import org.apache.spark.rdd.RDD

/**
 * Generates sessions for an RDD of WebLogEvents.
 */
object SessionGenerator {
  /** A default established by Google Analytics.
  * See: https://support.google.com/analytics/answer/2731565?hl=en
  * TL-DR: Uses a 30 minute timeout
  */
  private final val CONST_TIMEOUT_IN_MS = 1000*60*30

  /**
   * Generates sessions for each user from a body of RDD events. A session is defined as a consecutive series of events,
   * each of which is separated by a period less than <newSSFTimeoutsInMillis>. When the period between two consecutive
   * events is larger than <newSSFTimeoutsInMillis>, a new session will be created.
   *
   * @param sourceEvents - RDD of the WebLogEvents that will be input into sessions.
   * @param newSSFTimeoutsInMillis - Default: 30 minute timeout.
   * @return - RDD of WebSessions for all the users within the sourceEvents.
   */
  def apply(sourceEvents: RDD[WebLogEvent], newSSFTimeoutsInMillis: Long = CONST_TIMEOUT_IN_MS): RDD[WebSession] = {

    val sessions = sourceEvents
      .map(x => (x.clientIP, x))
      .groupByKey()
      .flatMap(x => {
      val ssf = new StatefulSessionFactory(x._1, newSSFTimeoutsInMillis)

      ssf.addSeqOfEvents(x._2.toSeq)

      //Get all the completed sessions from the state machine.
      ssf.yieldCompletedSessions(includeCurrentActiveSession = true)

    })
    sessions
  }
}