package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.{Encoders, SparkSession}
import org.apache.spark.sql.streaming.{OutputMode, ProcessingTime}

object JSONDemo {
  def main(args: Array[String]): Unit = {
    //    创建sparksession
    val spark = SparkSession
      .builder
      .master("local")
      .appName("StructuredStreamingDemo")
      .getOrCreate()

    import spark.implicits._
    val schema = Encoders.product[CdrData].schema
    // 读取数据
    val dataFrameStream = spark
      .readStream
      .format("json")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .schema(schema)
      //只能是监控目录，当新的目录进来时，再进行计算
      .load("/Users/backbook/data/json/")

    //在控制台监控
    val query = dataFrameStream.writeStream
      //complete,append,update。目前只支持前面两种
      .outputMode(OutputMode.Update())
      //console,parquet,memory,foreach 四种
      .format("console")
      .trigger(ProcessingTime(500))
      .start()

    query.awaitTermination()
  }
}

case class CdrData(reId: String, ratingFlowId: String, workFlowId: String)