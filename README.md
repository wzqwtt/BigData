# BigData
小白大数据学习笔记 :star:



# 一、Hadoop

![](./Hadoop/img/hadoop_logo.png)

| 模块               | Blog                                                         |
| ------------------ | ------------------------------------------------------------ |
| Hadoop概述         | [关于Hadoop你应该了解这些！](./Hadoop/1、大数据概论+Hadoop概述.md) |
| 搭建Hadoop运行环境 | [准备三台CentOS虚拟机](./Hadoop/2、准备三台虚拟机.md)        |
|                    | [安装Java、Hadoop以及编写集群分发脚本](./Hadoop/3、安装Java、Hadoop以及集群分发脚本.md) |
|                    | [重头戏：配置Hadoop集群](./Hadoop/4、配置Hadoop集群.md)      |
|                    | [群起集群并进行测试](./Hadoop/5、群起集群进行测试.md)        |
|                    | [编写Hadoop集群启停脚本以及查看集群Java进程脚本](./Hadoop/6、编写Hadoop集群启停脚本以及查看集群Java进程脚本.md) |
| HDFS               | [HDFS是个什么东西？](./Hadoop/7、HDFS是个什么东西？.md)      |
|                    | [HDFS常用Shell命令图文详解](./Hadoop/8、HDFS常用Shell命令图文详解.md) |
|                    | [搭建HDFS客户端API环境](./Hadoop/9、搭建HDFS客户端API环境.md) |
|                    | [HDFS API操作详解.md](./Hadoop/10、HDFS的API操作.md)         |
|                    | [HDFS读写流程图文详解](./Hadoop/11、HDFS读写流程图文详解.md) |
|                    | [HDFS NameNode和SecondaryNameNode工作机制](./Hadoop/12、HDFS中NameNode和SecondaryNameNode工作机制.md) |
|                    | [HDFS DataNode工作机制](./Hadoop/13、DataNode工作机制.md)    |
| MapReduce          | [MapReduce概述及核心编程思想](./Hadoop/14、MapReduce的概述及核心编程思想.md) |
|                    | [图解MapReduce编程规范](./Hadoop/15、图解MapReduce编程规范.md) |
|                    | [MapReduce WordCount案例实操](./Hadoop/16、WordCount案例实操.md) |
|                    | [MapReduce 序列化](./Hadoop/17、MapReduce序列化.md)          |
|                    | [MapReduce InputFormat 数据输入 框架原理](./Hadoop/18、InputFormat数据输入及框架原理.md) |
|                    | [MapReduce Shuffle机制之Partition分区](./Hadoop/19、Shuffle机制之Partition分区.md) |
|                    | [MapReduce Shuffle机制之WritableComparable排序](./Hadoop/20、Shuffle机制之WritableComparable排序.md) |
|                    | [MapReduce Shuffle机制之Combiner合并](./Hadoop/21、Shuffle机制之Combiner合并.md) |
|                    | [MapReduce OutputFormat数据输出 框架原理](./Hadoop/22、OutputFormat数据输出及框架原理.md) |
|                    | [MapReduce MapTask与ReduceTask工作机制](./Hadoop/23、MapTask与ReduceTask工作机制.md) |
|                    | [MapReduce Join应用](./Hadoop/24、MapReduce中的Join应用.md)  |
|                    | [MapReduce ETL数据清洗 案例实操](./Hadoop/25、使用MapReduce进行ETL数据清洗.md) |
|                    | [MapReduce 数据压缩](./Hadoop/26、MapReduce的数据压缩.md)    |
| Yarn               | [Yarn 基础架构、工作机制及作业提交全过程](./Hadoop/27、Yarn基础架构、工作机制及作业提交全过程.md) |



# 二、Zookeeper

<img src="./Zookeeper/img/Apache_ZooKeeper_logo.png" height="150px">

| 模块          | Blog                                                         |
| ------------- | ------------------------------------------------------------ |
| Zookeeper概述 | [Zookeeper是个什么东西？](./Zookeeper/1、Zookeeper是个什么东西？.md)     |
| 安装部署      | [Zookeeper 分布式安装部署](./Zookeeper/2、Zookeeper本地与分布式安装.md)  |
| 如何操作？    | [Zookeeper 客户端命令行与API操作](./Zookeeper/3、Zookeeper客户端命令行与API操作.md) |
| 案例实操      | [Zookeeper 案例：服务器动态上下线监听](./Zookeeper/4、Zookeeper案例—服务器动态上下线监听.md) |



# 三、Hive

<img src="./Hive/img/Hive_logo.png">

| 模块         | Blog                                                         |
| ------------ | ------------------------------------------------------------ |
| Hive概述     | [Hive的基本概念](./Hive/1、Hive的基本概念.md)                |
| 安装部署     | [Hive安装部署并替换derby为MySQL](./Hive/2、Hive安装部署并替换derby为MySQL.md) |
| Hive操作数据 | [Hive数据类型](./Hive/3、Hive数据类型)                       |
|              | [HiveSQL DDL数据定义](./Hive/4、Hive中的DDL数据定义.md)      |
|              | [Hive 导入与导出数据](./Hive/5、Hive导入与导出数据.md)       |
|              | [HiveSQL DML数据查询](./Hive/6、Hive中的DML数据查询.md)      |
|              | [Hive 分区表与分桶表](./Hive/7、Hive中的分区表与分桶表.md)   |
|              | [Hive 常用函数汇总以及练习](./Hive/8、Hive常用函数.md)       |
|              | [Hive 如何自定义函数](./Hive/9、Hive如何自定义函数.md)       |
|              | [Hive 压缩和存储](./Hive/10、Hive中的压缩和存储.md)          |
| 调优         | [Hive企业级调优](./Hive/11、Hive企业级调优.md)               |
| 实战         | [Hive 实战！分析视频网站TopN数据](./Hive/12、Hive实战演练——分析视频网站TopN数据.md) |



# 四、Flume

<img src="./Flume/img/flume-logo.png" >

| 模块                 | Blog                                                         |
| -------------------- | ------------------------------------------------------------ |
| Flume概述            | [Flume 是什么？都由什么组成？](./Flume/1、Flume的概念及组成.md) |
| 安装部署以及入门案例 | [Flume 分布式安装部署以及入门案例](./Flume/2、Flume分布式安装部署以及入门案例.md) |
| 内部原理             | [Flume中的事务、Agent内部原理、拓扑结构及对应案例](./Flume/3、Flume中的事务、Agent内部原理、拓扑结构及对应案例.md) |
| 自定义组件           | [Flume自定义Interceptor、Source与Sink](./Flume/4、Flume自定义Interceptor、Source与Sink.md) |



# 五、Kafka



<img src="./Kafka/img/kafka-logo.jpg" height="150">



| 模块                 | Blog                                                         |
| -------------------- | ------------------------------------------------------------ |
| Kafka极速入门        | [Kafka 是个什么东西？](./Kafka/1、Kafka-概述.md)             |
|                      | [Kafka 分布式安装部署](./Kafka/2、Kafka-分布式安装部署.md)   |
|                      | [Kafka 常用命令行操作](./Kafka/3、Kafka-常用命令行操作.md)   |
| Kafka Producer       | [Producer 生产者消息发送原理](./Kafka/4、Kafka-Producer-生产者消息发送原理.md) |
|                      | [Producer 异步与同步以及分区API操作](./Kafka/5、Kafka-Producer-异步与同步以及分区API操作.md) |
|                      | [Producer 生产者的生产经验](./Kafka/6、Kafka-Producer-生产者的生产经验.md) |
| Kafka Broker         | [Broker 工作流程以及节点服役和退役](./Kafka/8、Kafka-Broker-工作流程以及节点服役和退役.md) |
|                      | [Broker 副本机制详解](./Kafka/9、Kafka-Broker-副本机制详解.md) |
|                      | [Broker 文件存储、清理机制以及高效读写数据](./Kafka/10、Kafka-Broker-文件存储、清理机制以及高效读写数据.md) |
| Kafka Consumer       | [Consumer 消费者工作原理](./Kafka/11、Kafka-Consumer消费者工作原理.md) |
|                      | [Consumer 消费者API操作](./Kafka/12、Kafka-Consumer消费者API操作.md) |
|                      | [Consumer 消费者组分区的分配以及再平衡](./Kafka/13、Kafka-Consumer消费者组分区的分配以及再平衡.md) |
|                      | [Offset位移详解](./Kafka/14、Kafka-Offset位移详解.md)        |
| 大屏监控Kafka        | [Kafka Eagle(EAFS)监控安装部署](./Kafka/15、Kafka-Eagle监控安装部署.md) |
| Kafka-Kraft          | [Kafka-Kraft 模式的安装与部署](./Kafka/16、Kafka-Kraft模式的安装与部署.md) |
| Kafka 与外部系统集成 | [Kafka集成Flume](./Kafka/17、Kafka集成Flume.md)              |
|                      | Kafka集成Spark，留坑                                         |
|                      | Kafka集成Flink，留坑                                         |
|                      | Kafka集成Spring Boot，留坑                                   |





# 参考资料