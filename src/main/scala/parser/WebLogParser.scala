package parser

import event.WebLogEvent
import grizzled.slf4j.Logger
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat, DateTimeFormatter}

/**
 * Created by adambellemare on 2016-11-27.
 */
object WebLogParser {

  val TIMESTAMP = "timestamp"
  val ELB = "elb"
  val CLIENT_IP_AND_PORT = "client_ip"
  val BACKEND_IP_AND_PORT = "backend_ip"
  val REQUEST_PROCESSING_TIME = "request_processing_time"
  val BACKEND_PROCESSING_TIME = "backend_processing_time"
  val RESPONSE_PROCESSING_TIME = "response_processing_time"
  val ELB_STATUS_CODE = "elb_status_code"
  val BACKEND_STATUS_CODE = "backend_status_code"
  val RECEIVED_BYTES = "received_bytes"
  val SENT_BYTES = "sent_bytes"
  val REQUEST = "request"
  val USER_AGENT = "user_agent"
  val SSL_CIPHER = "ssl_cipher"
  val SSL_PROTOCOL = "ssl_protocol"

  @transient private[this] lazy val logger = Logger[this.type]
  //    timestamp elb client:port backend:port request_processing_time backend_processing_time response_processing_time elb_status_code backend_status_code received_bytes sent_bytes "request" "user_agent" ssl_cipher ssl_protocol

  def parsePayTMAssignmentFormat(logLine: String): Option[WebLogEvent] = {
    try {
      val regex =
        """([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s([^\s]+)\s"(.*)"\s"([^"]*)"\s([^\s]+)\s([^\s]+)"""

          .r(TIMESTAMP, ELB, CLIENT_IP_AND_PORT,
            BACKEND_IP_AND_PORT, REQUEST_PROCESSING_TIME, BACKEND_PROCESSING_TIME,
            RESPONSE_PROCESSING_TIME, ELB_STATUS_CODE, BACKEND_STATUS_CODE,
            RECEIVED_BYTES, SENT_BYTES, REQUEST, USER_AGENT,
            SSL_CIPHER, SSL_PROTOCOL)

        val results = regex.findFirstMatchIn(logLine)

        if (results.isDefined) {
          val data = results.get

          val dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(data.group(TIMESTAMP))
          val clientIP = data.group(CLIENT_IP_AND_PORT).split(":")(0)
          Some(new WebLogEvent(dateTime,
            clientIP,
            data.group(REQUEST)
          ))
      }
      else {
        logger.warn(s"Regex may be out of date. Unable to parse $logLine")
        None
      }
    } catch {
      case e: Exception => {
        logger.error(s"Exception while trying to parse and create a WebLogEvent. Exception = $e, WebLog text = $logLine")
        None
      }
    }
  }
}
