[TOC]
## 一、Flink的安装部署-standalone

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
