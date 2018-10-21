package FaultToleranceWithSparkAnFlink

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object reduceByKeyAndWindowDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("BlackListFilter")
      .setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    //使用socketTextStream 监听端口
    var lines:ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)

    val words: DStream[String] = lines.flatMap(_.split(" "))
    val wordAndOne: DStream[(String, Int)] = words.map((_, 1))

   val windowedWordCounts: DStream[(String, Int)] = wordAndOne.reduceByKeyAndWindow((a:Int,b:Int)=>(a + b),Seconds(5),Seconds(1))
    windowedWordCounts.print()


    //将数据写入到MYSQL中
    wordAndOne.reduceByKey(_+_).foreachRDD(rdd=>{
            rdd.foreachPartition(
              partitionOfRecords =>{
                  val  connection = createConnection()
                  partitionOfRecords.foreach(record => {
                    val sql = "insert into wordCount(word,wordCount) values( '"+ record._1 + "'," + record._2 + ")"
                    connection.createStatement().execute(sql)
                  })
                  connection.close()
              }
            )
          })

    ssc.start()
    ssc.awaitTermination()


  }

  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://cdh01:3306/spark_streaming","root","Whcyit123!@#")
  }

}
