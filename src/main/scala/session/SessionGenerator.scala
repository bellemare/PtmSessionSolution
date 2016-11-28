package session

import grizzled.slf4j.{Logger, Logging}
import org.apache.spark.rdd.RDD
import parser.WebLogParser

/**
 * Created by adambellemare on 2016-11-27.
 */
object SessionGenerator extends Logging {

  //@transient private[this] lazy val logger = Logger[this.type]

  def apply(sourceEvents : RDD[String]) = {

    val webLogEvents = sourceEvents.flatMap(WebLogParser.parsePayTMAssignmentFormat(_))

    val eventsPerUser = webLogEvents.map(x => {
      (x.clientIP,1)
    }).reduceByKey(_+_)

//    eventsPerUser.foreach(println(_))
  }

}
