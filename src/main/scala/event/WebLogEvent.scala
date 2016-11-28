package event

import org.joda.time.DateTime

/**
 * Created by adambellemare on 2016-11-27.
 */
case class WebLogEvent (timestamp: DateTime, clientIP: String, request: String) {

}
