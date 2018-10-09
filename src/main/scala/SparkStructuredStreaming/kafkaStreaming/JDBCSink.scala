package SparkStructuredStreaming.kafkaStreaming


import java.sql
import java.sql.DriverManager

import org.apache.spark.sql.ForeachWriter

class JDBCSink(url:String,user:String,pwd:String) extends ForeachWriter[(String,String)]{

  val driver = "com.mysql.Driver"
  var connection:sql.Connection = _
  var  statement:sql.Statement = _

  override def open(partitionId: Long, version: Long): Boolean = {
    Class.forName(driver)
    connection = DriverManager.getConnection(url,user,pwd)
    statement = connection.createStatement()
    true
  }

  override def process(value: (String,String)) :Unit ={
    statement.executeUpdate("insert into test"+"values")
  }

  override def close(errorOrNull: Throwable): Unit = ???
}
