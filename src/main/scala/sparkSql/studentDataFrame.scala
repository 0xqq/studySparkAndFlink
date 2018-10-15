package sparkSql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession

object studentDataFrame {
  def main(args: Array[String]): Unit = {
    val spark  = SparkSession.builder()
      .appName("studentDataFrame")
      .master("local[2]")
      .getOrCreate()

    val stuRDD:RDD[String]= spark.sparkContext.textFile("/Users/backbook/data/txt/sudent.data")
    import spark.implicits._
    val  stuDF: sql.DataFrame  =  stuRDD.map(_.split("\\|")).map(line => Student(line(0).toInt,line(1),line(2),line(3),line(4))).toDF()

    stuDF.show(50,false)
    val count = stuDF.filter("name !=''").count()
    println(count)

    stuDF.filter("name = ''").head(2).foreach(println)



  }

  case class Student(id:Int,name:String,phone:String,company:String,address:String)
}
