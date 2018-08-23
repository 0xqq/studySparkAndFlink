import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object KeyByDemo {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("USAGE:\nSocketWatermarkTest <hostname> <port>")
      return
    }
    val hostName = args(0)
    val port = args(1).toInt

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val input:DataStream[String] = env.socketTextStream(hostName, port)

    val key:DataStream[(String, String, String)] = input.map(f => {
     val data:Array[String] = f.split(" ")
      (data(0),data(1),data(2))
    })
    val keyRes= key.keyBy(_._1)

    keyRes.print()

    env.execute()
  }
}
