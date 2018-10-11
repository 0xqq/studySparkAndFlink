package SparkStraming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import com.redislabs.provider.redis._
object quickStart {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().
      setAppName("SparkStreamingCheckpoint")
      .setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.driver.allowMultipleContexts","true")
      .set("redis.host","127.0.0.1")
      .set("redis.port","6379")
//    val ssc: StreamingContext = new StreamingContext(sparkConf,Seconds(1))   // new context
//    //我们可以进入到目录下查看checkpoint的变化，然后对其进行查看
//    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost",9999)
//    val words: DStream[String] = lines.flatMap(_.split(" "))
//    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))
//    val totalWordCounts = wordAndOne.reduceByKey(_+_)
//    totalWordCounts.print()
//    ssc.start()       // Start the computation
//    ssc.awaitTermination()     // Wait for the computation to terminate

    val sc = new SparkContext(sparkConf)
    val redisRdd  = sc.fromRedisKV("h*")
    redisRdd.collect().foreach(println)


  }
}
