package session

import event.WebLogEvent
import org.apache.spark.rdd.RDD

/**
 * Generates sessions for an RDD of WebLogEvents.
 */
object SessionGenerator {
  //A default established by Google Analytics.
  private final val CONST_MAGIC_NUMBER_TIMEOUT_IN_MS = 1000*60*10

  /**
   *
   * @param sourceEvents
   * @param newSSFTimeoutsInMillis
   * @return
   */
  def apply(sourceEvents: RDD[WebLogEvent], newSSFTimeoutsInMillis: Long = CONST_MAGIC_NUMBER_TIMEOUT_IN_MS): RDD[WebSession] = {

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