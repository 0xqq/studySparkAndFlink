package SparkStructuredStreaming.other

import org.apache.spark.sql.{Dataset, Encoders, SparkSession}


object sqlDemo {
  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession
      .builder
      .master("local[*]")
      .appName("sqlDemo")
      .getOrCreate()


    val schema = Encoders.product[DeviceData].schema
    import spark.implicits._
    val df = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    df.createTempView("original")
    import spark.sql
    val lines = sql("select value from original")

    val  linedf= lines.as[String].map(_.split(",")).map(attributes => DeviceData(attributes(0), attributes(1).trim,attributes(2).trim.toDouble))

    val ds: Dataset[DeviceData] = linedf.as[DeviceData]

    ds.filter(_.signal > 10).map(_.device)

    import org.apache.spark.sql.expressions.scalalang.typed
    val wordCount  = ds.groupByKey(_.deviceType).agg(typed.avg(_.signal))

//    val wordCount = df.groupBy("value").count()
    val query = wordCount.writeStream.outputMode("complete").format("console").start()

    query.awaitTermination()







  }
}
case class DeviceData(device: String, deviceType: String, signal: Double)