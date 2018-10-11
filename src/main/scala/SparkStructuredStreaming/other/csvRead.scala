package SparkStructuredStreaming.other

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.ProcessingTime
import org.apache.spark.sql.types.StructType

object csvRead {
  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession
      .builder
      .master("local[*]")
      .appName("kafkaSource")
      .getOrCreate()

    // Read all the csv files written atomically in a directory
    val userSchema = new StructType().add("name", "string").add("age", "integer")
    val csvDF = spark
      .readStream
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .schema(userSchema)      // Specify schema of the csv files
      .csv("/Users/backbook/data/csv")    // Equivalent to format("csv").load("/path/to/directory")


    val query = csvDF.writeStream
      .outputMode("append")
      .format("console")
      .trigger(ProcessingTime(1))
      .start()


    query.awaitTermination()


  }
}
