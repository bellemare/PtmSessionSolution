package session

import event.WebLogEvent
import org.joda.time.DateTime

import scala.collection.mutable

/**
 * Created by adambellemare on 2016-11-27.
 */
case class WebSession (userIP: String, events: List[WebLogEvent], startTime: DateTime, endTime: DateTime) {
  override def toString: String = {
    s"userIP = $userIP, deltaTime in ms = $getSessionTimeInMillis"
  }

  def getSessionTimeInMillis: Long = {
    endTime.getMillis - startTime.getMillis
  }

  //Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session.
  def getUniqueURLVisits(): Seq[String] = {
    val hashURLs = new mutable.HashSet[String]()

    events.foreach(x => {
      hashURLs.add(x.request)
    })
    hashURLs.toSeq
  }
}
