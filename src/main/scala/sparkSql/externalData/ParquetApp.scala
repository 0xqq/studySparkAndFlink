package sparkSql.externalData

import org.apache.spark.sql.SparkSession

object ParquetApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("ParquetApp").master("local[2]").getOrCreate()

    //标准写法
    val  df = spark.read.format("parquet").load("file:///Library/spark-2.3.2-bin-hadoop2.6/examples/src/main/resources/users.parquet")

    df.printSchema()
    df.show()


//    df.select("name","favorite_numbers").write.format("json").save("file:///Users/backbook/data/json/user.json")

    spark.stop()
  }
}
