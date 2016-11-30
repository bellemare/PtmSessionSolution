package session

import event.WebLogEvent
import org.joda.time.DateTime
import scala.collection.mutable

/**
 * Contains the details of a WebSession.
 * @param userIP - The User's IP address.
 * @param events - A Seq of WebLogEvents for analyzing session behaviour.
 * @param startTime - The time of the first event in the session.
 * @param endTime - The time of the last event in the session.
 */
case class WebSession (userIP: String, events: Seq[WebLogEvent], startTime: DateTime, endTime: DateTime) {
  override def toString: String = {
    s"userIP = $userIP, deltaTime in ms = $getSessionTimeInMillis"
  }

  def getSessionTimeInMillis: Long = {
    endTime.getMillis - startTime.getMillis
  }

  //Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session.
  def getUniqueURLVisits: Seq[String] = {
    val hashURLs = new mutable.HashSet[String]()

    events.foreach(x => {
      hashURLs.add(x.request)
    })
    hashURLs.toSeq
  }
}
