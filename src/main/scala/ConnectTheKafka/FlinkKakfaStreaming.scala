//package ConnectTheKafka
//
//import java.util.Properties
//
//import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09
//import org.apache.flink.streaming.util.serialization.SimpleStringSchema
//object FlinkKakfaStreaming {
//  def main(args: Array[String]): Unit = {
//
//
//    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    env.enableCheckpointing(2000)
//    val properties = new Properties()
//    properties.setProperty("bootstrap.servers", "cdh01:9092")
//    properties.setProperty("zookeeper.connect", "cdh01:2181")
//    properties.setProperty("group.id", "test")
//
//
////    env.setStateBackend(new FsStateBackend("hdfs://cdh01:9000/flink/checkpoints"))
//
//    val stream = env.addSource(new FlinkKafkaConsumer09 [String]("test", new SimpleStringSchema(), properties))
//
//
//    stream.print()
//
//
//    env.execute("kafkaFlink")
//
//  }
//}
