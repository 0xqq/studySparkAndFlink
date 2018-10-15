package sparkSql

import org.apache.spark.sql.SparkSession

class getSpark extends Serializable{
  @volatile var spark:SparkSession = _
  def getSparkSession(name:String,master:String): SparkSession ={
    if (spark == null){
      synchronized{
        if (spark == null){
          spark = SparkSession.builder().appName(name).master(master).getOrCreate()
        }
      }
    }
    spark
  }
}