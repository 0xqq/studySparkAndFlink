package TransformationsDemo

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

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
    val keyedSteam= input.map(x=>mapFuction(x))


    //A fold function that, when applied on the sequence (1,2,3,4,5), emits the sequence "start-1", "start-1-2", "start-1-2-3", ...
    //实例：
    /*
    2> start-2
    2> start-2-3
    2> start-2-3-5
    2> start-2-3-5-5
    2> start-2-3-5-5-4
     */
//    val  Res:DataStream[String]=keyedSteam
//      .keyBy(0)
//      .fold("start")((str, i) => { str + "-" + i._2 })

    val  Res:DataStream[String]=keyedSteam
      .keyBy(0)
      .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      .fold("start")((str, i) => { str + "-" + i._2 })

    Res.print()

    //执行flink程序
    env.execute()
  }


  def mapFuction(f:String) ={
    val data:Array[String] = f.split(" ")
    val key = data(0)
    val time = data(1).toLong
    //      print(key +" " + time + " "+ count)
    (key,time)
  }
}
