import parser.WebLogParser

/**
 * Created by adambellemare on 2016-11-27.
 */
class WebLogParserTest extends SparkTestSuite {
  test("Test parsing of specific web logs to ensure corner cases are met.") { fixture =>

    val logList = List( """2015-07-22T09:00:28.019143Z marketpalce-shop 123.242.248.130:54635 10.0.6.158:80 0.000022 0.026109 0.00002 200 200 0 699 "GET https://paytm.com:443/shop/authresponse?code=f2405b05-e2ee-4b0d-8f6a-9fed0fcfe2e0&state=null HTTP/1.1" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36" ECDHE-RSA-AES128-GCM-SHA256 TLSv1.2""",

      //Note the lack of host IP and ports.
      """2015-07-22T16:10:16.672818Z marketpalce-shop 122.167.26.202:19342 - -1 -1 -1 504 0 0 0 "POST https://paytm.com:443/shop/log HTTP/1.1" "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)" ECDHE-RSA-AES128-SHA TLSv1""",

      //Note the " in the user request string.
      """2015-07-22T16:10:54.994028Z marketpalce-shop 106.51.132.54:5048 10.0.6.178:80 0.000023 0.004066 0.000022 200 200 0 13820 "GET https://paytm.com:443/'"()&%251<ScRiPt%20>prompt(940817)</ScRiPt>/termsconditions HTTP/1.1" "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)" DHE-RSA-AES128-SHA TLSv1""",

      //Note the empty User Agent string.
      """2015-07-22T17:45:17.549417Z marketpalce-shop 106.202.23.193:47132 10.0.4.150:80 0.000021 0.001118 0.000032 200 200 0 1150 "GET https://paytm.com:443/favicon.ico HTTP/1.1" "" ECDHE-RSA-AES128-GCM-SHA256 TLSv1.2""",

      //Amazon example 1
      """2015-05-13T23:39:43.945958Z my-loadbalancer 192.168.131.39:2817 10.0.0.1:80 0.000073 0.001048 0.000057 200 200 0 29 "GET http://www.example.com:80/ HTTP/1.1" "curl/7.38.0" - -""",

      //Amazon example 2
      """2015-05-13T23:39:43.945958Z my-loadbalancer 192.168.131.39:2817 10.0.0.1:80 0.000086 0.001048 0.001337 200 200 0 57 "GET https://www.example.com:443/ HTTP/1.1" "curl/7.38.0" DHE-RSA-AES128-SHA TLSv1.2""",

      //Amazon example 3
      """2015-05-13T23:39:43.945958Z my-loadbalancer 192.168.131.39:2817 10.0.0.1:80 0.001069 0.000028 0.000041 - - 82 305 "- - - " "-" - -""",

      //Amazon example 4
      """2015-05-13T23:39:43.945958Z my-loadbalancer 192.168.131.39:2817 10.0.0.1:80 0.001065 0.000015 0.000023 - - 57 502 "- - - " "-" ECDHE-ECDSA-AES128-GCM-SHA256 TLSv1.2"""
    )

    val results = logList.map(WebLogParser.parsePayTMAssignmentFormat(_))

    results.foreach(println(_))

    //There should be 8 fully decoded events.
    assert(results.size == 8)
  }

  test ("Full parsing of sample PayTM log.") { fixture =>
    val sc = fixture.sc
    val data = sc.textFile("src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz")
    val totalLinesRead = data.count()

    //The number of lines the file according to "wc -l <filename>"
    //This is assuming they were all generated by a valid web log server.
    assert(totalLinesRead == 1158500)

  }
}