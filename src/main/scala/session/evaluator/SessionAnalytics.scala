package session.evaluator

import grizzled.slf4j.Logging
import org.apache.spark.rdd.RDD
import session.WebSession

/**
 * Created by adambellemare on 2016-11-28.
 */
object SessionAnalytics extends Logging {

  /**
   * Find the most engaged users, ie the IPs with the longest session times.
   * Return the top users with the absolutely longest session times.
   *
   * @param sessions - an RDD of all the sessions to investigate.
   * @param topEngagedCount - The number of unique engaged users IPs to return.
   */
  def getMostEngagedUsers(sessions: RDD[WebSession], topEngagedCount: Int = 10) = {
    val maxTimeToUserIP = sessions.map(x => {
      (x.userIP, x.getSessionTimeInMillis)
    })
      .groupByKey()
      .map(x => (x._2.max, x._1))

    maxTimeToUserIP.takeOrdered(topEngagedCount)(Ordering[Long].reverse.on(_._1))
    .map(_.swap)
  }
}
