

# 一、MapReduce学习思维导图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415144857474.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 二、MapReduce定义
`MapReduce`是`Hadoop`的一个<font color='red'>分布式（多台机器）运算程序</font>的编程框架，也是`Hadoop`的核心框架

如果让我们用程序实现一个多台机器并发计算的程序，我们可能需要关心到数据传输、如何并发等很多底层的问题，`MapReduce`为我们提供了一系列的框架，我们可以直接调用

`MapReduce`核心功能是将**用户编写的业务逻辑代码**和**自带默认组件**整合成一个完整的分布式运算程序，并发运行在一个`Hadoop`集群上

综上所述：==**MapReduce = 自己写的业务逻辑代码 + 它自身默认的底层代码**==

# 三、MapReduce的优缺点

## 1、优点
- **易于编程**：因为`MapReduce`已经封装好了底层代码，我们只需要关心业务逻辑代码，实现框架的接口就好了
- **有良好的扩展性**：如果服务器资源枯竭，可以动态增加服务器，解决计算资源不够的问题
- **高容错性**：任何一台机器挂掉，都可以将任务转移到其他节点
- **适合海量数据计算**：TB/PB级别，几千台服务器共同计算


## 2、缺点
当然因为`MapReduce`把中间结果存放在了硬盘，所以它不适合用于：

- 不适用于实时计算
- 不擅长流式计算（Spark Streaming、Flink适合）
- 不擅长DAG有向无环图计算



# 四、MapReduce核心编程思想

拿一个需求来举例说明`MapReduce`的思想：有个300M的文件，统计每一个单词出现的总次数，要求查询结果`a-p`的为一个文件，`q-z`的为一个文件

如果拿到现实中，要统计这么多单词并且按照`a-p，p-z`分区，会拿出两张纸，一张纸记录`a-p`的、另一张纸记录`p-z`的，但是`300M`的文件太大了，自己一个人肯定完不成，所以找来两个人来帮忙，他们都拿出两张纸按照同样的方法进行统计，到最后他们各自会拿出满满的两页纸，上面记录着各个单词，每个单词出现一次就记录为（`hadoop->1`）这样的键值对。这个就是`Map`阶段，他们三个人每个人做的都是`MapTask`任务。

上面的任务做完了，就需要专门派两个人来统计`a-p`与`p-z`开头的单词，把相同的单词后面数字相加，这样就得到了最后的结果。这个阶段是`Reduce`聚合阶段，这两个人做的是`ReduceTask`任务。

如下图所示，就是上述的过程：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041515313320.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

换成专业的说法，看下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415153227666.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

综上所述：

- 分布式的运算程序王王需要分成至少两个阶段
- 第一个阶段的`MapTask`并发实例，完全并行运行，互不相干
- 第二个阶段的`ReduceTask`并发实例互不相干，但是他们依赖于`MapTask`的结果
- `MapReduce`编程模型只能包含一个`Map`阶段和一个`Reduce`阶段，如果用户的业务逻辑非常复杂，那么只好编写多个`MapReduce`程序，但是这些都是串行执行的



