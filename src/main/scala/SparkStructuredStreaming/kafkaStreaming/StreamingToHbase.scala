package SparkStructuredStreaming.kafkaStreaming

import SparkStructuredStreaming.customSink.HbaseSink
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
    val value=sql("select value from Originalkafka ")



    val query =value.writeStream
      .outputMode("append")
      .foreach(new HbaseSink("localhost:2181","hdfs://localhost:8020/hbase","structured_streaming_hbase","info"))
      .queryName("test")
      .format("foreach")
      .start()
    query.awaitTermination()


  }
}
