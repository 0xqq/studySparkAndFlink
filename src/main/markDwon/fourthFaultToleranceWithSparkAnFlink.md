## 四、谈谈spark和flink的容错机制

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

在开始整理这个文章的时候，我最新去的官网，可是官网都是官方性的讲解，所以对我来说确实可能不是那么友好。于是我找到了这样的一篇文章。
[关于SparkStreaming checkpoint那些事儿](https://blog.csdn.net/rlnLo2pNEfx9c/article/details/81417061)

本文开始构建项目解析其原因从spark Streaming入手，因为咱们看的是flink差异，但是spark中确实很大一部分主要是关于流式计算，在我更新的这段时间里面，流式flink算是比较火的。

- spark streaming使用checkpoint的优势在哪里：可以帮助driver端`非代码逻辑`错误导致的dirver应用失败重启，比如网络，jvm等。
但是并不是一层不变的，仅限于支持自动重启的集群管理器，比如yarn。
- 注意点：由于checkpoint信息包含序列化的Scala / Java / Python对象，尝试使用新的修改类反序列化这些对象可能导致错误。
