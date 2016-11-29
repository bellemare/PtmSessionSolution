package session

import grizzled.slf4j.Logging
import event.WebLogEvent
import org.joda.time.DateTime
import scala.collection.mutable.ListBuffer

/**
 * A stateful session factory that generates WebSessions from consecutive inputs of WebLogEvents.
 * This factory can be used both by streaming and batch applications.
 *
 * @param userId - The userId for which the sessions are being generated. You must ensure that only events for this user
 *               are input into a particular instance.
 * @param timeout - The number of milliseconds of which to wait before timing out a session. The default value is 30 minutes.
 */
class StatefulSessionFactory(userId: String, timeout: Long = 30*1000*60) extends Logging with Serializable {
  private final val CONST_MOST_RECENT_SENTINEL: Long = Long.MinValue
  private final val CONST_LEAST_RECENT_SENTINEL: Long = Long.MaxValue
  private val completedSessions = new ListBuffer[WebSession]
  private val activeSessionEvents = new ListBuffer[WebLogEvent]
  private var mostRecentEventTimeInMillis: Long = CONST_MOST_RECENT_SENTINEL
  private var leastRecentEventTimeInMillis: Long = CONST_LEAST_RECENT_SENTINEL

  /**
   *
   * Yields any sessions waiting to be produced by the Session Factory.
   *
   * @param includeCurrentActiveSession - If true, complete  the active session and return it along the
   *                                    other completed sessions.
   * @return A sequence of WebSessions.
   */
  def yieldCompletedSessions(includeCurrentActiveSession: Boolean): Seq[WebSession] = completedSessions.synchronized {
    //evaluateClosureOfCurrentSessionState(DateTime.now.getMillis)
    if (includeCurrentActiveSession && activeSessionEvents.size > 0)
      completeAndAddSessionToCompletedList()
    val sessions: Seq[WebSession] = completedSessions.toSeq
    completedSessions.clear()
    sessions
  }

  /**
   * Add a sequence of WebLogEvents to the SessionFactory. The events are sorted by time before being added.
   *
   * @param events - A list of WebLogEvents to add to the factory.
   */
  def addSeqOfEvents(events : Seq[WebLogEvent]) = completedSessions.synchronized {
    val sortedByTime = events.sortWith((x,y) => x.timestamp.isBefore(y.timestamp))
    sortedByTime.foreach(addEvent)
  }

  /**
   * Adds a single WebLogEvent to the activeSessions. Determines the state of the active web log session based on the
   * event timestamp. If it has passed the timeout window, then a new session is started and a completed session is formed.
   * NOTE: The events must be added in sorted timestamp order. Use addSeqOfEvents whenever possible.
   *
   * @param event - The WebLogEvent to add to the factory.
   */
  private def addEvent(event: WebLogEvent) {
    val eventTimeInMillis: Long = event.timestamp.getMillis
    if (mostRecentEventTimeInMillis == CONST_MOST_RECENT_SENTINEL) {
      mostRecentEventTimeInMillis = eventTimeInMillis
      logger.debug(s"Setting mostRecentEventTimeInMillis from $CONST_MOST_RECENT_SENTINEL to $eventTimeInMillis")
    }
    if (leastRecentEventTimeInMillis == CONST_LEAST_RECENT_SENTINEL) {
      leastRecentEventTimeInMillis = eventTimeInMillis
      logger.debug(s"Setting leastRecentEventTimeInMillis from $CONST_LEAST_RECENT_SENTINEL to $eventTimeInMillis")
    }
    evaluateClosureOfCurrentSessionState(eventTimeInMillis)

    if (eventTimeInMillis > mostRecentEventTimeInMillis) {
      mostRecentEventTimeInMillis = eventTimeInMillis
      logger.debug(s"Setting mostRecentEventTimeInMillis from $mostRecentEventTimeInMillis to $eventTimeInMillis")
    }
    if (eventTimeInMillis < leastRecentEventTimeInMillis) {
      leastRecentEventTimeInMillis = eventTimeInMillis
      logger.debug(s"Setting leastRecentEventTimeInMillis from $leastRecentEventTimeInMillis to $eventTimeInMillis")
    }

    activeSessionEvents.append(event)
  }

  /**
   * Evaluate the current session state and close it off if the elapsed time since the last event is greater than
   * the timeout period.
   *
   * @param eventTimeInMillis - Time since the last event in milliseconds.
   */
  private def evaluateClosureOfCurrentSessionState(eventTimeInMillis: Long) {
    if (eventTimeInMillis - mostRecentEventTimeInMillis >= timeout) {
      completeAndAddSessionToCompletedList()
    }
  }

  /**
   * Complete the last session and add it to the completed list.
   * Update the state of the SessionFactory for the next events.
   */
  private def completeAndAddSessionToCompletedList() = completedSessions.synchronized {
    val session: WebSession = new WebSession(userId, activeSessionEvents.toList, new DateTime(leastRecentEventTimeInMillis), new DateTime(mostRecentEventTimeInMillis))
    completedSessions.append(session)
    this.mostRecentEventTimeInMillis = CONST_MOST_RECENT_SENTINEL
    this.leastRecentEventTimeInMillis = CONST_LEAST_RECENT_SENTINEL
    activeSessionEvents.clear()
  }

}
