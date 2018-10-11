package SparkStructuredStreaming.customSink

import java.sql

import ThreadPool.MysqlConnectionPool
import org.apache.spark.sql.{ForeachWriter, Row}

class JDBCSink() extends ForeachWriter[Row]{

  var connection:sql.Connection = _
  var  statement:sql.Statement = _



  override def open(partitionId: Long, version: Long): Boolean = {
    connection =  MysqlConnectionPool.getConnection()
    statement = connection.createStatement()
    true
  }

  override def process(value: Row) :Unit ={
    statement.executeUpdate("insert into student(name,age) values(\"li\",17)")
  }

  override def close(errorOrNull: Throwable): Unit =
  {

  }
}
