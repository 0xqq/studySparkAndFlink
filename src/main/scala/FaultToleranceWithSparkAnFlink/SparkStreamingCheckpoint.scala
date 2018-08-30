package FaultToleranceWithSparkAnFlink


import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamingCheckpoint {
  def ssc: StreamingContext={
    val sparkConf = new SparkConf().
      setAppName("SparkStreamingCheckpoint")
      .setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.driver.allowMultipleContexts","true")
    val ssc: StreamingContext = new StreamingContext(sparkConf,Seconds(2))   // new context
    ssc.checkpoint("E:\\log")   // set checkpoint directory
    ssc
  }
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("USAGE:\nSocketWatermarkTest <hostname> <port>")
      return
    }
    val hostName = args(0)
    val port = args(1).toInt
    val lines: ReceiverInputDStream[String] = ssc.socketTextStream(hostName,port)
    val words: DStream[String] = lines.flatMap(_.split(" "))
    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))
    val wordCount: DStream[(String, Int)] = wordAndOne.reduceByKey(_ + _)
    wordCount.print()


    ssc.start()       // Start the computation
    ssc.awaitTermination()     // Wait for the computation to terminate
  }



}
