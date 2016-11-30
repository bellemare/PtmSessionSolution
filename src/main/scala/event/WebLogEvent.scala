package event

import org.joda.time.DateTime

/**
 * Stores a minimal set of web log event contents required for completing the Paytm assignment.
 * @param timestamp - Log event timestamp
 * @param clientIP - Log event user clientIP
 * @param request - The request string recorded in the log event
 */
case class WebLogEvent (timestamp: DateTime, clientIP: String, request: String) {

}
