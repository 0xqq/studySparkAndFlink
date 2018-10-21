package sparkSql.projectTraining

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object TopNstat {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("TopNstat").
      config("spark.sql.sources.partitionColumnTypeInference.enabled","false").
      master("local[2]").getOrCreate()
    val accessDF = spark.read.format("parquet").load("file:///Users/backbook/data/log/clean/")
    accessDF.printSchema()
    accessDF.show(false)

    videAccessTopNStat(spark,accessDF)

    spark.stop()

  }

  def videAccessTopNStat(spark: SparkSession,aceessDF:DataFrame)={
    import spark.implicits._

    //在使用$符号的时候，要进行隐式转换，不然无法进行交换
   val topNStatDF =  aceessDF.filter($"day" === "20170511" && $"cmsType" === "video")
      .groupBy("day","cmsId").agg(count("cmsId").as("times"))
      .orderBy($"times".desc)
    topNStatDF.show(false)



  }




}
