import java.text.SimpleDateFormat

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector



object WatermarkTest {
  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      System.err.println("USAGE:\nSocketWatermarkTest <hostname> <port>")
      return
    }
    val hostName = args(0)
    val port = args(1).toInt

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val input = env.socketTextStream(hostName, port)

    val inputMap:DataStream[(String, Long)] = input.map(f => {
      val arr = f.split("\\W+")
      val code = arr(0)
      val time = arr(1).toLong
      //在这里对数据进行抽象，讲数据进行tup处理成返回的元组
      (code, time)
    })
    //new AssignerWithPeriodicWatermarks[(String,Long)中有抽取timestamp和生成watermark2个方法，在执行时，
    // 它是先抽取timestamp，后生成watermark，因此我们在这里print的watermark时间，其实是上一条的watermark时间，我们到数据输出时再解释
    val watermark = inputMap.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[(String, Long)] {

      var currentMaxTimestamp = 0L
      val maxOutOfOrderness = 10000L //最大允许的乱序时间是10s

      var a: Watermark = null

      val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      //然后才执行这个方法
      override def getCurrentWatermark: Watermark = {
        a = new Watermark(currentMaxTimestamp - maxOutOfOrderness)
        a
      }

      //先执行这个方法
      //提取timestamp生成watermark，并且打印
      override def extractTimestamp(t: (String,Long), l: Long): Long = {
        val timestamp = t._2
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp)
        //并打印（code，time，格式化的time，currentMaxTimestamp，currentMaxTimestamp的格式化时间，watermark时间（）
        println("timestamp:" + t._1 +","+ t._2 + "|" +format.format(t._2) +","+  currentMaxTimestamp + "|"+ format.format(currentMaxTimestamp) + ","+ a.toString)
        timestamp
      }
    })

    val window = watermark
      .keyBy(_._1)
      .window(TumblingEventTimeWindows.of(Time.seconds(3)))
      .apply(new WindowFunctionTest)

    window.print()

    env.execute()

  }

}
class WindowFunctionTest extends WindowFunction[(String,Long),(String, Int,String,String,String,String),String,TimeWindow] {

  override def apply(key: String, window: TimeWindow, input: Iterable[(String, Long)], out: Collector[(String, Int, String, String, String, String)]): Unit = {
    val list = input.toList.sortBy(_._2)
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    out.collect(key, input.size, format.format(list.head._2), format.format(list.last._2), format.format(window.getStart), format.format(window.getEnd))
  }
}