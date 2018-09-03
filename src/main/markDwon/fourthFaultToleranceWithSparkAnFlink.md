## 四、谈谈spark和flink流式计算的容错机制

- 明确学习点，可以更清楚看到一些本质的东西
- 借鉴网友的一些观点可以弥补自己的短视
- 擅于书写更好的找准位置

### 项目构建spark

在之前我们已经对项目进行flink的构建，为了能够更好的看到一些相关，我们从理论和代码中进行开始学习之路，接下去我们进行构建spark的开发环境

注意点：在spark和flink本地的运行需要的依赖hadoop的需要下载相应的部署安装。

- 如下是maven相关的依赖包，这是截取了片段，详细查看[maven](https://github.com/backbook/flinkDemo/blob/master/pom.xml)<br>
```scala
     <properties>
        <spark.version>2.3.1</spark.version>
     </properties>
     <dependencies>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.11</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming_2.11</artifactId>
            <version>${spark.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_2.11</artifactId>
            <version>${spark.version}</version>
        </dependency>
      </dependencies>
        
```
### spark streamin的checkpoint机制

在开始整理这个文章的时候，我最新去的[官网](http://spark.apache.org/docs/latest/streaming-programming-guide.html)。又或者可你可以查看这样的一片文章也许又收获
[关于SparkStreaming checkpoint那些事儿](https://blog.csdn.net/rlnLo2pNEfx9c/article/details/81417061)

本文开始构建项目解析其原因从spark Streaming入手，因为咱们看的是flink差异，但是spark中确实很大一部分主要是关于流式计算，在我更新的这段时间里面，流式flink算是比较火的。

- spark streaming使用checkpoint的优势在哪里：可以帮助driver端`非代码逻辑`错误导致的dirver应用失败重启，比如网络，jvm等。
但是并不是一层不变的，仅限于支持自动重启的集群管理器，比如yarn。而我们为了容错保证任务实时在线，让即使出现故障的程序能够给从故障中自动回复。
- 注意点：由于checkpoint信息包含序列化的Scala / Java / Python对象，尝试使用新的修改类反序列化这些对象可能导致错误。

1.元数据检查点-将定义流式计算的信息保存到容错存储（如HDFS）。这用于从运行流应用程序的驱动程序的节点的故障中恢复（稍后详细讨论）。元数据包括</br>

- 配置 - 用于创建流应用程序的配置。
- DStream操作 - 定义流应用程序的DStream操作集。
- 不完整的批次 - 其工作排队但尚未完成的批次。

2.数据检查点-将生成的RDD保存到可靠的存储。在一些跨多个批次组合数据的有状态转换中，这是必需的。在这种转换中，生成的RDD依赖于先前批次的RDD，这导致依赖链的长度随时间增加。
为了避免恢复时间的这种无限增加（与依赖链成比例），有状态变换的中间RDD被周期性地 检查点到可靠存储（例如HDFS）以切断依赖链。

### 何时启用检查点

必须为具有以下任何要求的应用程序启用检查点：

- 有状态转换的用法 - 如果在应用程序中使用了（updateStateByKey或reduceByKeyAndWindow使用反函数），则必须提供检查点目录以允许定期RDD检查点。
- 从运行应用程序的驱动程序的故障中恢复 - 元数据检查点用于使用进度信息进行恢复。


### flink的checkpoint机制

本文采用的是网络的一篇比较有价值的[请移步到这里](https://segmentfault.com/a/1190000008129552),阐述了很多很不错的观点，当然也参考了官网的例子，也是很不错的一些实例，值得我们去花时间。<br>

#### 前言

Apache Flink提供了可以恢复数据流的应用一致状态容错机制，确保在发生故障时，程序的每条记录只会作用于状态一次，当然也可以降级到至少一次。

1.怎么实现<br>
容错机制通过持续创建分布式数据流的快照来实现。对于状态占用空间小的流应用，这些快照非常轻量，可以高频率创建而对性能影响很小。流计算应用的状态保存在一个可配置的环境，如：master 节点或者 HDFS上。<br>
2.什么情况下触发机制<br>
在遇到程序故障时（如机器、网络、软件等故障），Flink 停止分布式数据流。系统重启所有 operator ，重置其到最近成功的 checkpoint。输入重置到相应的状态快照位置。保证被重启的并行数据流中处理的任何一个 record 都不是 checkpoint 状态之前的一部分。<br>
3.两个注意点<br>
为了容错机制生效，数据源（例如queue或者broker）需要能重放数据流。Apache Kafka有这个特性，Flink中kafka的connector利用了这个功能。<br>
由于Flink的checkpoint是通过分布式快照实现的，接下来我们将snapshot和checkpoint这两个词汇交替使用。

本文中实验的一些尝试得到的，比较与spark比对得到，spark中是利用将数据进行序列化，而flink是一个个的快照，所以在处理的时候，两个的核心理解层面上的不一样的。<br>
flink中的使用在场景中，可以打开checkpoint然后设置一些的特色参数，但是在windows下并没有见到相应的文本输出，但是在linux系统中，加入了设置位置的一些配置文件之后，能够将快照实时更新。<br>

对于spark来说，无疑这种序列化模式更快一点，但是当计算量大的时候，checkpoint需要付出很大的io成本，本身也有造成空间的冗余。
但是在小数据的情况下，spark的序列化模式显然更高效一点。
