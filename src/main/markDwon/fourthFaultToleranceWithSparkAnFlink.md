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