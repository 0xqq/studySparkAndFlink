package TransformationsDemo

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object reduceDemo {
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
    val key: DataStream[(String, Int)] = input.map(f => {
      val data:Array[String] = f.split(" ")
      val key = data(0)
      val time = data(1).toInt
      //      print(key +" " + time + " "+ count)
      (key,time)
    })

    /*
    reduce能够与windows结合使用，当然也可以进行一直叠加，只是这样的叠加，需要在多个线程中进行，
    举例：
    2> (1,3)
    1> (2,4)
    1> (2,8)
    1> (2,17)
    1> (2,116)
    需要观察到具体的样例可以运行程序进行设置，程序为nc
     */
//    val reduceRes= key.keyBy(_._1)
//      .reduce{(v1,v2)=>(v1._1,v1._2+v2._2)}
    val reduceRes= key.keyBy(_._1)
      .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      .reduce{(v1,v2)=>(v1._1,v1._2+v2._2)}
    reduceRes.print()
    env.execute()

  }
}

