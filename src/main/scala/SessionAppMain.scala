import grizzled.slf4j.Logging
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by adambellemare on 2016-11-27.
 */

object SessionAppMain extends App with Logging {

  implicit val sparkConf = new SparkConf()
    .setIfMissing("spark.master", "local[6]")
    .setIfMissing("spark.executor.memory", "2g")
    .set("spark.logConf", "true")

  val sc = new SparkContext(sparkConf)

  val data = sc.textFile("src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz")

  println("Total lines read = ", data.count())


}
