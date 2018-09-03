package TransformationsDemo

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}

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

    //指定hostName用nc，进行实验，读取的是socket流的数据
    val input:DataStream[String] = env.socketTextStream(hostName, port)
    //map 释义：
    /*
    Map 是将元素中的值拉取并且可以对其进行基本的操作，
    DataStream → DataStream
     */
    val key: DataStream[(String, String, String)] = input.map(f => {
     val data:Array[String] = f.split(" ")
      val key = data(0)
      val time = data(1)
      val count = data(2)
//      print(key +" " + time + " "+ count)
      (key,time,count)
      
    })
    /*keyBy释义  DataStream → KeyedStream
      第一种
      可以指定指定好的case值也就是case class (id:Int, click:Int) 样例：keyBy(x=>person(x._1.toInt))
      case class person(id:Int)
      第二种方式
      可以指定tuple中的元素值进行   key.keyBy(_._1)
      说明：在1> 3>这些中是主要的关系，表示了相关性，默认是四个线程，主要跟waterMark有关。
      1> (1,2,3)
      3> 1
    */
    /* filter() 返回的是Ture or False 该方法和spark是一样的用法
    如果返回是True的话，则成立，将其过滤，如果是False的话，保留不被过滤
     */
    val keyRes= key.keyBy(_._1)
    val Res = keyRes.filter(_._1.equals(0))
    key.print()
    Res.map(_._1).print()

    env.execute()
  }
}
