package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.{Dataset, SparkSession}


object sqlDemo {
  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession
      .builder
      .master("local[*]")
      .appName("sqlDemo")
      .getOrCreate()

    import spark.implicits._
    val df = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

//    val ds: Dataset[DeviceData] = df.as[DeviceData]

    val wordCount = df.groupBy("value").count()


    val query = wordCount.writeStream.outputMode("complete").format("console").start()

    query.awaitTermination()







  }
}
case class DeviceData(device: String, deviceType: String, signal: Double)