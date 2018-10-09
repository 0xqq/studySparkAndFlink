package SparkStructuredStreaming.kafkaStreaming

import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.spark.sql.{ForeachWriter, Row}
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.TableName

object HbaseSink extends ForeachWriter[Row] {

  override def open(partitionId: Long, version: Long): Boolean = true

  override def process(record: Row): Unit = {
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "localhost:2181")
    conf.set("hbase.rootdir", "hdfs://localhost:8020/hbase")

    val connection = ConnectionFactory.createConnection(conf)
    val tableNameObj = TableName.valueOf("structured_streaming_hbase")
    val table = connection.getTable(tableNameObj)

    val theput= new Put(Bytes.toBytes(record.mkString))
    theput.add(Bytes.toBytes("info"),Bytes.toBytes("age"),Bytes.toBytes("30"))
    table.put(theput)

  }

  override def close(errorOrNull: Throwable): Unit = {


  }
}
