package sparkSql.projectTraining

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * 进行二次清洗，是在第一个sparkStatForMatJob中进行的操作
  */

object SparkStatCleanJob {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkStatCleanJob").master("local[2]").getOrCreate()
    val accessRDD = spark.sparkContext.textFile("/Users/backbook/data/log/out/access.log")


    val accessDF =  spark.createDataFrame(accessRDD.map(line=> AccessConvertUtils.parseLog(line)),AccessConvertUtils.struct)

//    accessDF.printSchema()
//    accessDF.show(false)


    //使用如下的一些参数可以进行一些优化，在使用文件的时候，我们是可以根据一定数据进行的操作，比如比较少打数据可以进行的1
    //
    accessDF.coalesce(1).write.format("parquet").mode(SaveMode.Overwrite)
      .partitionBy("day").save("/Users/backbook/data/log/clean")
    spark.stop()

  }

}
