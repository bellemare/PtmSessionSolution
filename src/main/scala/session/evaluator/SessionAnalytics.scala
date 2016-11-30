package session.evaluator

import grizzled.slf4j.Logging
import org.apache.spark.rdd.RDD
import session.WebSession

/**
 * A simple SessionAnalytics object containing functions that perform analytics on RDDs or large sets of WebSessions.
 */
object SessionAnalytics extends Logging {

  /**
   * Find the most engaged users, ie the IPs with the longest session times.
   * Return the top users with the absolutely longest session times.
   *
   * @param sessions - an RDD of all the sessions to investigate.
   * @param topEngagedCount - The number of unique engaged users IPs to return.
   */
  def getMostEngagedUsers(sessions: RDD[WebSession], topEngagedCount: Int = 10): Array[(String, Long)] = {
    val maxTimeToUserIP = sessions.map(x => {
      (x.userIP, x.getSessionTimeInMillis)
    })
      .groupByKey()
      .map(x => (x._2.max, x._1))

    maxTimeToUserIP.takeOrdered(topEngagedCount)(Ordering[Long].reverse.on(_._1))
    .map(_.swap)
  }

  /**
   * Calculate the mean of the sessions.
   *
   * @param sessions - an RDD of WebSessions for calculating the mean.
   * @param ignoreSingleEventSessions - If true, do not use single-event sessions with zero-length periods.
   */
  def calculateMean(sessions: RDD[WebSession], ignoreSingleEventSessions: Boolean = false): Double = {
    sessions.flatMap(x => {
      if (ignoreSingleEventSessions && x.getSessionTimeInMillis == 0) {
        None
      } else {
        Some(x.getSessionTimeInMillis)
      }
    }).mean()
  }
}
