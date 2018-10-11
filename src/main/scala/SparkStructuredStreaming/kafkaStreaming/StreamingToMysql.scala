package SparkStructuredStreaming.kafkaStreaming

import java.sql.Connection

import SparkStructuredStreaming.customSink.JDBCSink
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object StreamingToMysql {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.registerKryoClasses(Array(classOf[org.apache.commons.pool2.impl.GenericObjectPool[Connection]]))
     val spark=SparkSession
      .builder
      .appName("testWriteResultToHbase")
      .master("local")
       .config(conf)
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
    val value=sql("select value from Originalkafka")


    val query =value.writeStream
      .outputMode("append")
      .foreach(new JDBCSink())
      .queryName("test")
      .format("foreach")
      .start()
    query.awaitTermination()


  }

}
