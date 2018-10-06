package SparkStructuredStreaming.kafkaStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

object isStreaming {
  def main(args: Array[String]): Unit = {

    val spark:SparkSession = SparkSession
      .builder
      .master("local[*]")
      .appName("kafkaSource")
      .getOrCreate()

    // Read text from socket
    val socketDF = spark
      .readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    socketDF.isStreaming    // Returns True for DataFrames that have streaming sources

    socketDF.printSchema





  }
}
