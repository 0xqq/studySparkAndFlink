package sparkSql.projectTraining

import org.apache.spark.sql.SparkSession

object SparkStatFormatJob {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("SparkStatFormatJob")
               .master("local[2]").getOrCreate()

    val accessRDD= spark.sparkContext.textFile("/Users/backbook/data/log/10000_access.20161111.log")
//    accessRDD.take(10).foreach(println)

    accessRDD.map( line => {
      val words = line.split(" ")
      val ip = words(0)
      val time = DateUtils.parse(words(3)+" "+words(4))
      val url = words(11).replace("\"","")
      val traffic = words(9)
//      (ip,time,url,traffic)

      time+"\t"+url+"\t"+traffic+"\t"+ip
    }).saveAsTextFile("/Users/backbook/data/log/out/")
    spark.stop()
  }
}
