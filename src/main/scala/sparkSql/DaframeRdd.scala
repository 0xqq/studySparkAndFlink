package sparkSql


import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}



object DaframeRdd {
  def main(args: Array[String]): Unit = {

    val spark = new getSpark().getSparkSession("DataFrameRdd","local[2]")

//    program(spark)
    interReflection(spark)
    spark.stop()
  }

//这种方式是在不知道的情况下进行的输出schema类型匹配
  def  program(spark:SparkSession): Unit ={
    val rdd = spark.sparkContext.textFile("file:///Users/backbook/data/txt/info.txt")
    import spark.implicits._
    val  infoRDD = rdd.map(_.split(",")).map(line => Row(line(0).toInt,line(1),line(2).toInt))

    val structType = StructType(Array(StructField("id",IntegerType,true),StructField("name",StringType,true),StructField("age",IntegerType,true)))

    val  infoDF =   spark.createDataFrame(infoRDD,structType)

    infoDF.printSchema()
    infoDF.show()

  }

//反射定义case class 事先知道了类型，优先选择这一种
  def interReflection(spark:SparkSession): Unit ={
    val infoRDD = spark.sparkContext.textFile("file:///Users/backbook/data/txt/info.txt")
    import spark.implicits._
    val  infoDF = infoRDD.map(_.split(",")).map(line => Info(line(0).toInt,line(1),line(2).toInt)).toDF()
//    infoDF.show()
//    infoDF.filter(infoDF.col("age") > 10).show()
//    infoDF.createOrReplaceTempView("student")
//    spark.sql("select * from student").show()
//    spark.sql("select * from student where age > 12").show()
    val infoDs = infoDF.as[Info]
    val filterRDD =infoDs.filter(_.age>13).show()

  }

  case class Info(id:Int,name:String,age:Int)



}
