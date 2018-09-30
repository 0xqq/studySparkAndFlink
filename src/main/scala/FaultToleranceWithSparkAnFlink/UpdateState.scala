package FaultToleranceWithSparkAnFlink

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}

object UpdateState {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().
      setAppName("SparkStreamingCheckpoint")
      .setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.driver.allowMultipleContexts","true")
    val ssc: StreamingContext = new StreamingContext(sparkConf,Seconds(1))   // new context
    //我们可以进入到目录下查看checkpoint的变化，然后对其进行查看

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("39.108.170.235",9999)
    val words: DStream[String] = lines.flatMap(_.split(" "))
    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))
    val totalWordCounts = wordAndOne.reduceByKey(_+_)


//    totalWordCounts.foreachRDD(rdd=>{
    ////      val  connection = createConnection()
    ////      rdd.foreach{
    ////        record =>
    ////          val sql = "insert into wordCount(word,wordCount) values( '"+ record._1 + "'" + record._2 + ")"
    ////          connection.createStatement().execute(sql)
    ////      }
    ////    })

    totalWordCounts.print()



    //    val wordCount: DStream[(String, Int)] = wordAndOne.reduceByKey(_ + _)
    //    wordCount.print()


    ssc.start()       // Start the computation
    ssc.awaitTermination()     // Wait for the computation to terminate
  }


}
