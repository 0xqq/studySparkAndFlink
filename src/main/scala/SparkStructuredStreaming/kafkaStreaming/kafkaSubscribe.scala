package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.SparkSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object kafkaSubscribe {
  def main(args: Array[String]): Unit = {

    val logger:Logger=LoggerFactory.getLogger(kafkaSubscribe.getClass)


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
     df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]

    val ds = df
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "test1")
      .option("checkpointLocation","/Users/backbook/data/ck")
      .start()

    ds.awaitTermination()

  }


}
