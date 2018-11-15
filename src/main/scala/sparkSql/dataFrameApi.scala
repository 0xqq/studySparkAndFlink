package sparkSql

import org.apache.spark.sql.SparkSession

//

object dataFrameApi {
  def main(args: Array[String]): Unit = {

    val  spark = SparkSession.builder().appName("dataFrameApp").master("local[2]").getOrCreate()

    val  peopleDF =  spark.read.format("json").load("file:///Users/backbook/data/json/test.json")

    peopleDF.printSchema()


    peopleDF.show()

    //输出某列所有数据：select reId from table
    peopleDF.select("reId").show()
    //select reId,workFlowId+10 from table
    peopleDF.select(peopleDF.col("reId"),(peopleDF.col("workFlowId")+10).as("w2")).show()
    //select * from table where workFlowId > 3
    peopleDF.filter(peopleDF.col("workFlowId") > 3).show()
    //select count("reId") from table
    peopleDF.groupBy(peopleDF.col("reId")).count().show()

    spark.stop()

  }
}
