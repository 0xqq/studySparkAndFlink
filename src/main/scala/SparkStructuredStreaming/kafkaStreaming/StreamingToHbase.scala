package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.SparkSession

object StreamingToHbase {

  def main(args: Array[String]): Unit = {
    val spark=SparkSession
      .builder
      .appName("testWriteResultToHbase")
      .master("local")
      .getOrCreate()


    import spark.implicits._
    val lines = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("subscribe", "test")
      .load()
      .selectExpr("cast(topic as String) ","cast(key as String)","CAST(value AS STRING)")
      .as[(String,String,String)]

    lines.createTempView("Originalkafka")
    import spark.sql
    val count=sql("select value from Originalkafka ")


    val query =count.writeStream
      .outputMode("append")
      .foreach(HbaseSink)
      .queryName("test")
      .format("foreach")
      .start()
    query.awaitTermination()


  }

}
