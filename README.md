

[一、Flink的安装部署-standalone](src/main/markDwon/firstFlinkSteup.md)

## 一、Flink的安装部署-standalone
1.下载flink，可以进入官网进行下载，flink是apache的顶级项目，越来越受到大家的熟悉。
本文使用wget进行下载并且安装。<br>
&ensp;&ensp;&ensp;&ensp;如下是命令操作【备注：java1.7以上，最好1.8+,免密ssh】:
> cd /app/opt/ <br>
> wget http://mirrors.hust.edu.cn/apache/flink/flink-1.6.0/flink-1.6.0-bin-hadoop27-scala_2.11.tgz <br>
> tar -zxvf ./flink-1.6.0-bin-hadoop27-scala_2.11.tgz

如上命令是下载安装flink，可以在/etc/profile添加环境变量，记得  source /etc/profile

<table>
    <tr>
        <td>node1</td>
        <td>master</td>
    </tr>
    <tr>
        <td>node2</td>
        <td>slave1</td>
    </tr>
    <tr>
        <td>node3</td>
        <td>slave2</td>
     </tr>
</table>

2.如下配置进行更改：<br>
> vim /app/opt/flink-1.6.0/conf/flink.yml 

修改如下的key velues <br>
<table>
  <tr>
   <td>jobmanager.rpc.address</td>
   <td>node1</td>
  </tr>
</table>

> vim /app/opt/flink-1.6.0/conf/slaves

&ensp;&ensp;<B>node1</B><br>
&ensp;&ensp;<B>node2</B><br>
&ensp;&ensp;<B>node3</B><br>

3.分发到其他节点
>scp  /app/opt/flink-1.6.0 node2:/app/opt/ <br>
>scp /app/opt/flink-1.6.0 node3:/app/opt/ 

4.启动flink集群和关闭flink集群
> /app/opt/flink-1.6.0/bin/start-cluster.sh <br>
>/app/opt/flink-1.6.0/bin/stop-***.sh

## 二、运行wordcount实例

1.Maven的依赖添加pom.xml

```
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

## 三、waterMark

[下面引述：Flink流计算编程--watermark（水位线）简介，如需了解更多相关，请点击](https://blog.csdn.net/lmalds/article/details/52704170)

![avatar](https://github.com/backbook/flinkDemo/blob/master/src/main/images/waterMark.png) 

- 图中蓝色虚线和实线代表着watermark的时间。


