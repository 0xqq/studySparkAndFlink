package FaultToleranceWithSparkAnFlink


import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamingCheckpoint {

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("USAGE:\nSocketWatermarkTest <hostname> <port>")
      return
    }
    val hostName = args(0)
    val port = args(1).toInt
    val sparkConf = new SparkConf().
      setAppName("SparkStreamingCheckpoint")
      .setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.driver.allowMultipleContexts","true")
    val ssc: StreamingContext = new StreamingContext(sparkConf,Seconds(2))   // new context
    //我们可以进入到目录下查看checkpoint的变化，然后对其进行查看
    ssc.checkpoint("E:\\log")   // set checkpoint directory

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream(hostName,port)
    val words: DStream[String] = lines.flatMap(_.split(" "))
    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))

    //在使用updateByKey则是需要有
    val addFunc = (currValues: Seq[Int], prevValueState: Option[Int]) => {
      //通过Spark内部的reduceByKey按key规约。然后这里传入某key当前批次的Seq/List,再计算当前批次的总和
      val currentCount = currValues.sum
      // 已累加的值
      val previousCount = prevValueState.getOrElse(0)
      // 返回累加后的结果。是一个Option[Int]类型
      Some(currentCount + previousCount)
    }

    val totalWordCounts = wordAndOne.updateStateByKey[Int](addFunc)
    totalWordCounts.print()


//    val wordCount: DStream[(String, Int)] = wordAndOne.reduceByKey(_ + _)
//    wordCount.print()


    ssc.start()       // Start the computation
    ssc.awaitTermination()     // Wait for the computation to terminate
  }



}
