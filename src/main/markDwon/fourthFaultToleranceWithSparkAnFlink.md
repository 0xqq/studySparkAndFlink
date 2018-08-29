## 谈谈spark和flink的容错机制

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