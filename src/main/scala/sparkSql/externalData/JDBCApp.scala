package sparkSql.externalData

import org.apache.spark.sql.SparkSession

object JDBCApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("JDBCDemp")
      .master("local[2]")
      .getOrCreate()
    val mysqlDF = spark.read.format("jdbc")
      .option("url","jdbc:mysql://localhost:3306/hive")
      .option("dbtable","hive.SDS")
      .option("user","root")
      .option("password","Server2008!")
      .option("driver","com.mysql.jdbc.Driver")
      .load()
    mysqlDF.show(false)


  }
}
