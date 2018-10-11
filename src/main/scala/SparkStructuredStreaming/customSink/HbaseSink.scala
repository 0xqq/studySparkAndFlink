package SparkStructuredStreaming.customSink

import org.apache.hadoop.hbase.client.{ConnectionFactory, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.spark.sql.{ForeachWriter, Row}



class HbaseSink(zookeeper:String,hbaseDataDir:String,hbaseTableName:String,columnName:String) extends ForeachWriter[Row] {

  override def open(partitionId: Long, version: Long): Boolean =
    {

      true
    }

  override def process(record: Row): Unit = {
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", zookeeper)
    conf.set("hbase.rootdir", hbaseDataDir)

    val connection = ConnectionFactory.createConnection(conf)
    val tableNameObj = TableName.valueOf(hbaseTableName)
    val table = connection.getTable(tableNameObj)

    val str= record.mkString.split("_")



    val theput= new Put(Bytes.toBytes(str(0)))
    theput.add(Bytes.toBytes(columnName),Bytes.toBytes("age"),Bytes.toBytes(str(1)))
    table.put(theput)
  }

  override def close(errorOrNull: Throwable): Unit = {


  }
}
