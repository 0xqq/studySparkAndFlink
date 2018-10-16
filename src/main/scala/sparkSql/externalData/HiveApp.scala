package sparkSql.externalData

import java.io.File

import org.apache.spark.sql.SparkSession

object HiveApp {
  def main(args: Array[String]): Unit = {
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath
    val spark = SparkSession.builder()
      .appName("ParquetApp")
      .master("local[2]")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .enableHiveSupport()
      .getOrCreate()
    import  spark.implicits
    val databaseDf = spark.sql("show databases")
    databaseDf.show()
    spark.sql("use spark_sql")
    val  hiveDF  =  spark.sql("select * from sql_test")
    hiveDF.write.saveAsTable("hive_spark_1")
    hiveDF.show()


  }
}
