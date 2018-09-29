package ConnectTheKafka

import java.util.Properties

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.createTypeInformation

object FlinkKakfaStreaming {
  def main(args: Array[String]): Unit = {




    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(2000)
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "cdh01:9092")
    properties.setProperty("zookeeper.connect", "cdh01:2181")
    properties.setProperty("group.id", "test")


    val stream = env.addSource(new FlinkKafkaConsumer08[String]("test",
      new SimpleStringSchema(), properties))
    stream.setParallelism(4).writeAsText("hdfs:///tmp/data")


    env.execute("kafkaFlink")

  }
}
