import grizzled.slf4j.Logging
import org.apache.spark.storage.StorageLevel.MEMORY_AND_DISK
import org.apache.spark.{SparkContext, SparkConf}
import parser.WebLogParser
import session.SessionGenerator
import session.evaluator.SessionAnalytics


/**
 * Created by adambellemare on 2016-11-27.
 */

object SessionAppCLI extends App with Logging {

  implicit val sparkConf = new SparkConf()
    .setIfMissing("spark.master", "local[6]")
    .setIfMissing("spark.executor.memory", "2g")
    .set("spark.logConf", "true")

  val sc = new SparkContext(sparkConf)

  val cliArgs = SessionAppCLIArgs.parse(args)

  val sourceRDD = sc.textFile(cliArgs.getInputFile)
  val webLogEvents = sourceRDD.flatMap(WebLogParser.parseWebLogEvents)
  val sessions = SessionGenerator(webLogEvents, cliArgs.getTimeoutInSeconds*1000)

  //Persist the sessions so we only have to calculate it once.
  sessions.persist(MEMORY_AND_DISK)

  val stringBuilder = new StringBuilder

  //Number of Sessions:
  stringBuilder.append("===========================================================================================================\n")
  stringBuilder.append(s"Number of sessions: ${sessions.count()}\n")
  stringBuilder.append("===========================================================================================================\n")
  //Session length average, including sessions with single-events
  val sessionLengthAverage = SessionAnalytics.calculateMean(sessions)
  stringBuilder.append(s"Average session length in seconds, including 0-duration sessions = ${sessionLengthAverage/1000}\n")
  stringBuilder.append("===========================================================================================================\n")
  //Session length average, excluding sessions with single-events
  val sessionLengthAverageNonZeroSessions = SessionAnalytics.calculateMean(sessions, ignoreSingleEventSessions = true)
  stringBuilder.append(s"Average session length in seconds without 0-duration sessions = ${sessionLengthAverageNonZeroSessions/1000}\n")
  stringBuilder.append("===========================================================================================================\n")
  //Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session

  stringBuilder.append("Printing all the unique URL visits for a sample of 3 sessions\n")
  val sampleOfSessions = sessions.take(3)
  sampleOfSessions.map(x => {
    s"UserIP = ${x.userIP}, unique URL Visits:\n  ${x.getUniqueURLVisits.mkString("\n  ")}\n"
  }).foreach(stringBuilder.append)

  stringBuilder.append("===========================================================================================================\n")
  //Find the most engaged users, ie the IPs with the longest session times
  stringBuilder.append("Get the top-10 most engaged users, non-duplicated, with their longest session times.\n")
  val mostEngaged = SessionAnalytics.getMostEngagedUsers(sessions)
  mostEngaged.foreach(x => {stringBuilder.append(s"userIP = ${x._1}, Session Length in Seconds = ${x._2/1000}\n")})
  stringBuilder.append("===========================================================================================================\n")

  val output = stringBuilder.toString()

  println(output)

  //Release the sessions.
  sessions.unpersist()

}