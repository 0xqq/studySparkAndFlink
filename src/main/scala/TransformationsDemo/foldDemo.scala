package TransformationsDemo

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment, createTypeInformation}

object foldDemo {
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
    //map 释义：
    /*
    Map 是将元素中的值拉取并且可以对其进行基本的操作，
    DataStream → DataStream
     */
    val keyedSteam: KeyedStream[(String, String), Tuple] = input.map(x=>{
      val data =x.split(" ")
      (data(0),data(1))
    }).keyBy(0)


    //A fold function that, when applied on the sequence (1,2,3,4,5), emits the sequence "start-1", "start-1-2", "start-1-2-3", ...
    val  Res:DataStream[String]=keyedSteam.fold("start")((str, i) => { str + "-" + i })

    Res.print()

    //执行flink程序
    env.execute()
  }
}
