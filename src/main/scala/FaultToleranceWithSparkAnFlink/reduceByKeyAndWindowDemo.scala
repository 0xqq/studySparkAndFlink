package FaultToleranceWithSparkAnFlink

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object reduceByKeyAndWindowDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("BlackListFilter")
      .setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    //使用socketTextStream 监听端口
    var lines:ReceiverInputDStream[String] = ssc.socketTextStream("39.108.170.235", 9999)

    val words: DStream[String] = lines.flatMap(_.split(" "))
    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))

   val windowedWordCounts: DStream[(String, Int)] = wordAndOne.reduceByKeyAndWindow((a:Int,b:Int)=>(a + b),Seconds(5),Seconds(1))

    windowedWordCounts.print()
    ssc.start()
    ssc.awaitTermination()


  }
}
