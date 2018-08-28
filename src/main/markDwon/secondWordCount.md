## 二、运行wordcount实例

1.Maven的依赖添加pom.xml

```scala
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-java</artifactId>
  <version>1.6.0</version>
</dependency>
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-streaming-java_2.11</artifactId>
  <version>1.6.0</version>
</dependency>
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-clients_2.11</artifactId>
  <version>1.6.0</version>
</dependency>

```

下面是代码展示(scala)：

```scala
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala.createTypeInformation  //隐式转换

object wordCount {
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
    val text: DataStream[String] = env.socketTextStream("192.168.123.123", port, '\n')
    // parse the data, group it,ignore window it, and aggregate the counts
    val windowCounts = text
      .flatMap(_.split(" "))
      .map((_, 1))
      .keyBy(_._1)
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .sum(1)
    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }
  // Data type for words with count
}

```