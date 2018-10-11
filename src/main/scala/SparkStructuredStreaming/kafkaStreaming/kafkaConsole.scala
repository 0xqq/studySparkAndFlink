package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{OutputMode, ProcessingTime}
import org.slf4j.{Logger, LoggerFactory}


object kafkaConsole {
  def main(args: Array[String]): Unit = {
    val logger:Logger=LoggerFactory.getLogger(kafkaConsole.getClass)


    val spark:SparkSession = SparkSession
      .builder
      .master("local[*]")
      .appName("kafkaSource")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "test")
      .option("failOnDataLoss","false")
      .load()
      .selectExpr("cast(topic as String)","CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String,String, String)]

    df.createTempView("original")
    import spark.sql
    val lines = sql("select value from original")

    // Split the lines into words
    val words = lines.as[String].flatMap(_.split(" "))



    // Generate running word count
    val wordCounts = words.groupBy("value").count()

    val query = wordCounts
      .writeStream
      .queryName("a").
      trigger(ProcessingTime(10))
      .format("console")
      .outputMode(OutputMode.Append())
      .start()

    query.awaitTermination()


  }


}
