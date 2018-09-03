package FaultToleranceWithSparkAnFlink

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup

object FlinkCheckpoint {

  def main(args: Array[String]): Unit = {
    // the port to connect to
    // the port to connect to
    val port: Int = try {
      ParameterTool.fromArgs(args).getInt("port")
    } catch {
      case e: Exception => {
        System.err.println("No port specified. Please run 'SocketWindowWordCount --port <port>'")
        return
      }
    }

    // get the execution environment
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // get input data by connecting to the socket

    //在这里进行设置checkpoint,1000的意思是每1000ms进行开启检查点
    env.enableCheckpointing(1000)
    // set mode to exactly-once (this is the default)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    // make sure 500 ms of progress happen between checkpoints
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
    // checkpoints have to complete within one minute, or are discarded
    env.getCheckpointConfig.setCheckpointTimeout(60000)
    // allow only one checkpoint to be in progress at the same time
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    // enable externalized checkpoints which are retained after job cancellation
    env.getCheckpointConfig.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    

    val text: DataStream[String] = env.socketTextStream("39.108.170.235", port, '\n')
    // parse the data, group it,ignore window it, and aggregate the counts
    //.flatMap(_.split(" "))是和spark的一样的理解
    val windowCounts = text
      .flatMap(_.split(" "))
      .map((_, 1))
      .keyBy(_._1)
      .timeWindow(Time.seconds(1), Time.seconds(1))
      .sum(1)
    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }
}
