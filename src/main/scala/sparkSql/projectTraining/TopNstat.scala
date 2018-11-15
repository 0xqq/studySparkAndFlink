package sparkSql.projectTraining

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

import scala.collection.mutable.ListBuffer



object TopNstat {


  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("TopNstat").
      config("spark.sql.sources.partitionColumnTypeInference.enabled","false").
      master("local[2]").getOrCreate()
    val accessDF = spark.read.format("parquet").load("file:///Users/backbook/data/log/clean/")
    accessDF.printSchema()
    accessDF.show(false)
    //最受欢迎的TopN课程
    //videAccessTopNStat(spark,accessDF)
    //按照地市进行统计TopN课程
//    cityAccessTopNStat(spark,accessDF)

    //按照流量进行统计
    videoTrafficsTopNstat(spark,accessDF)

    spark.stop()

  }

  def videAccessTopNStat(spark: SparkSession, accessDF:DataFrame)={
    import spark.implicits._

    //在使用$符号的时候，要进行隐式转换，不然无法进行交换
   val topNStatDF =  accessDF.filter($"day" === "20170511" && $"cmsType" === "video")
      .groupBy("day","cmsId").agg(count("cmsId").as("times"))
      .orderBy($"times".desc)
    topNStatDF.show(false)
  }

  def cityAccessTopNStat(spark: SparkSession,accessDF:DataFrame)={
    //引入隐式转换的意义在于能够在一定作用域在进行相应的方法的引用
    import spark.implicits._


    //在使用$符号的时候，要进行隐式转换，不然无法进行交换
    val cityTopNStatDF =  accessDF.filter($"day" === "20170511" && $"cmsType" === "video")
      .groupBy("day","city","cmsId").agg(count("cmsId").as("times"))

   val  top3 =  cityTopNStatDF.select(cityTopNStatDF("day"),
      cityTopNStatDF("city"),
      cityTopNStatDF("cmsId"),
      cityTopNStatDF("tim6es"),
      row_number().over(Window.partitionBy(cityTopNStatDF("city"))
        .orderBy(cityTopNStatDF("times").desc)
        ).as("times_rank"))
        .filter("times_rank <= 3") //Top3

    try{
      top3.foreachPartition(partitionOfRecords => {
        val list  = new ListBuffer[dayCityVideoAcessStat]

        partitionOfRecords.foreach(info => {
          val day = info.getAs[String]("day")
          val city = info.getAs[String]("city")
          val cmsId = info.getAs[Long]("cmsId")
          val times = info.getAs[Long]("times")
          val timesRank = info.getAs[Int]("times_rank")
          list.append(dayCityVideoAcessStat(day,cmsId,city,times,timesRank))
        })
        StatDAO.insertDayCityVideoAccessStatTopN(list)
      })
    }catch {
      case e:Exception => e.printStackTrace()
    }
  }

//按照流量进行统计
  def videoTrafficsTopNstat(spark: SparkSession, accessDF: DataFrame) = {

    import spark.implicits._
    //在使用$符号的时候，要进行隐式转换，不然无法进行交换
    val videoTrafficTopNStat =  accessDF.filter($"day" === "20170511" && $"cmsType" === "video")
      .groupBy("day","cmsId").agg(sum("traffic").as("traffics"))
      .orderBy($"traffics".desc)


}





}
