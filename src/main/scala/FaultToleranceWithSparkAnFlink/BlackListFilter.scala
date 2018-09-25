package FaultToleranceWithSparkAnFlink

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object BlackListFilter {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("BlackListFilter")
      .setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    //黑名单
    val blackList = Array(("jack", true), ("rose", true))
    //设置并行度
    val blackListRDD = ssc.sparkContext.parallelize(blackList, 3)

    //使用socketTextStream 监听端口
    var st:ReceiverInputDStream[String] = ssc.socketTextStream("39.108.170.235", 9999)

    //user, boolean==>
    val users:DStream[(String, String)] = st.map {
      line =>  (line.split(" ")(1), line)
    }


    val validRddDS: DStream[String] = users.transform(ld => {
      //通过leftOuterJoin 将(k, v) join (k,w) ==> (k, (v, some(W)))
      val ljoinRdd = ld.leftOuterJoin(blackListRDD)

      //过滤掉黑名单
      val fRdd = ljoinRdd.filter(tuple => {
        println(tuple)
        if(tuple._2._2.getOrElse(false)) {
          false
        } else {
          true
        }
      })

      //获取白名单
      val validRdd = fRdd.map(tuple => tuple._2._1)
      validRdd
    })

    validRddDS.print()
    ssc.start()
    ssc.awaitTermination()


  }
}
