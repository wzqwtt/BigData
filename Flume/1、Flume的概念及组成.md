## 一、Flume定义



`Flume`是`Cloudera`提供的一个高可用，高可靠的，分布式的**海量日志采集、聚合和传输的系统**。Flume基于流式架构，灵活简单

![](./img/微信截图_20220325124004.png)

上图展示了`Flume`的一个作用，它介于数据和数据存储地方中间，很明显它做了一个传输的功能，于是Flume最主要的作用是：`实时`读取服务器本地磁盘的数据，然后写入HDFS或者其他存储框架。



## 二、Flume基础架构（组成）

下图是摘自`Apache Flume`官网上的图片，它描述了Flume的架构

![](./img/DevGuide_image00.png)

它主要包含：`Agent`（方框）、`Source`、`Channel`、`Sink`还有一个`Event`（这个没有在图片上显示），接下来逐个击破。

### 1、Agent

它相当于一个Java进程（JVM），他以事件的形式将数据从源头输送至目的地

### 2、Source

Source是负责接收数据到Flume Agent的组件，Source组件可以处理各种类型、各种格式的日志文件

比如：avro、thrift、exec、jms、spooling directory、netcat、sequence、generator、syslog、http、legacy等。

在一个Agent中可以配置多个Source负责接收不同的数据



### 3、Channel

Channel是位于Source和Sink之间的缓冲区。因此，Channel允许Source和Sink运作在不同的速率上。Channel是线程安全的，可以同时处理几个Source的写入操作和几个Sink的读取操作



Flume自带两种Channel：

- `Memory Channel`：基于内存的，内存中的队列，但是使用内存有一定的风险，如果服务器宕机、断电就会数据丢失
- `File Channel`：基于磁盘的，将所有的时间都写入磁盘，因此不用担心服务器在宕机或者断电的时候数据会丢失
- `Kafka Channel`：基于Kafka的



### 4、Sink

Sink不断轮询Channel中的时间并且批量的移除他们，并将这些事件批量写入到存储或索引系统、或者被发送到另一个Flume Agent

Sink组件目的地包括：hdfs、logger、avro、thrift、ipc、file、HBase、solr、自定义



### 5、Event

传输单元，是Flume数据传输的基本单元，以Event的形式将数据从源头送至目的地。

它由`Header`和`Body`两部分组成，Header存储event的属性，是K-V结构，Body用来存放该条数据，形式为字节数组：

![](./img/微信截图_20220325131615.png)



## 参考资料

- 尚硅谷Flume教学视频
- [Apache Flume官网](https://flume.apache.org/)

