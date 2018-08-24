package TransformationsDemo

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment,createTypeInformation}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object ProcessWindowFuction {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("USAGE:\nSocketWatermarkTest <hostname> <port>")
      return
    }
    val hostName = args(0)
    val port = args(1).toInt

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //指定hostName用nc，进行实验，读取的是socket流的数据
    val input:DataStream[String] = env.socketTextStream(hostName, port)
    val keyStream = input.map(f => mapFuction(f))

    keyStream
      .keyBy(_._1)
      .timeWindow(Time.minutes(5))
      .process(new MyProcessWindowFunction())

  }


  def mapFuction(f:Any) ={
    val data:Array[String] = f.toString.split(" ")
    val key = data(0)
    val time = data(1).toLong
    //      print(key +" " + time + " "+ count)
    (key,time)
  }

}
class MyProcessWindowFunction extends ProcessWindowFunction[(String, Long), String, String, TimeWindow] {
  override def process(key: String, context: Context, input: Iterable[(String, Long)], out: Collector[String])= {
    var count = 0L
    for (in <- input) {
      count = count + 1
    }
    out.collect(s"Window ${context.window} count: $count")
  }
}