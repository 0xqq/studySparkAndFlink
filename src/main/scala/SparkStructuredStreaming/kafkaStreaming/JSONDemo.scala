package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{OutputMode, ProcessingTime}
import org.apache.spark.sql.types.StructType

object JSONDemo {
  def main(args: Array[String]): Unit = {
    //    创建sparksession
    val spark = SparkSession
      .builder
      .master("local")
      .appName("StructuredStreamingDemo")
      .getOrCreate()

    
    import spark.implicits._

    //  定义schema
    val userSchema = new StructType().add("name", "string").add("age", "integer")

    // 读取数据
    val dataFrameStream = spark
      .readStream
      .option("sep",",")
      .schema(userSchema)
      //只能是监控目录，当新的目录进来时，再进行计算
      .csv("file:///Users/backbook/data/csv/")


    //在控制台监控
    val query = dataFrameStream.writeStream
      //complete,append,update。目前只支持前面两种
      .outputMode(OutputMode.Append)
      //console,parquet,memory,foreach 四种
      .format("console")
      .trigger(ProcessingTime(500))
      .start()

    query.awaitTermination()
  }
}
