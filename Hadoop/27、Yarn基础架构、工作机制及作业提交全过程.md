[toc]


------
# 一、Yarn是什么？

在现实生产环境中，大数据处理服务器众多（如下图所示），那么如果管理集群资源、如何给任务合理的分配资源就是个问题，`Yarn`的作用就是解决这些问题

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210427092350931.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

`Yarn`是一个资源调度平台，负责为运算程序提供服务器运算资源，相当于一个分布式的操作系统平台，而`MapReduce`等运算程序则相当于运行于操作系统之上的应用程序
# 二、Yarn基础架构

`Yarn`主要由`ResourceManager`、`NodeManager`、`ApplicationMaster`、`Container`等组件组成：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210427092624724.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

- `ResourceManager(RM)`：相当于集群的老大，负责处理客户端的请求，如果有任务进来，它负责分配`NodeManager`、并监控`NodeManager`，启动和监控`ApplicationMaster`，负责资源的分配与调度
- `NodeManager(NM)`：它管理单个节点上的资源，处理来自`RM`的命令，处理`ApplicationMaster`的命令
- `ApplicationMaster(AM)`：为`MapReduce`计算程序申请资源并分配给内部的任务，同时负责任务的监控与容错
- `Container`：是`Yarn`种资源的抽象，它在某个节点种启动，就像一个服务器中的一个虚拟机，封装了某个节点上的多维度资源，比如内存、CPU、磁盘、网络等

# 三、Yarn工作机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210427093416293.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

如上图所示，下面是`Yarn`工作机制刨析：
- 当`MR`程序提交到客户端所在节点，它会启动`YarnRunner`，向`RM`申请一个`Application`
- `RM`接收请求并返回一个`资源提交路径`，该程序将运行所需资源提交到`HDFS`上
- 程序资源提交完毕之后，申请运行`mrAppMaster`
- `RM`会首先把用户的请求初始化为一个`Task`，这个`Task`会进入一个调度队列
- 其中一个`NodeManager`领取`Task`任务
- 该`NodeManager`创建容器`Container`，并产生`MRAppMaster`
- `Container`从`HDFS`上拷贝资源到本地
- `MRAppmaster`向`RM`申请运行`MapTask`资源
- `RM`将运行`MapTask`任务分配给另外两个`NodeManager`，这两个`NodeManager`分别领取任务并创建容器
- `MR`向两个接收到任务的`NodeManager`发送程序启动脚本，这两个`NodeManager`分别启动`MapTask`，`MaspTask`对数据分区排序
- `MrAppMaster`等待所有`MapTask`运行完毕后，向`RM`申请容器，运行`ReduceTask`
- `ReducerTask`向`MapTask`获取相应分区的数据
- 程序运行完毕后，`MR`向`RM`申请注销自己

# 四、Yarn作业提交全过程

## 1、HDFS、YARN、MapReduce三者关系
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210427094034143.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

- `HDFS`负责文件的上传，读取等工作
- `MapReduce`负责运算，对大数据进行处理
- `Yarn`负责调度一切，分配资源等

## 2、Yarn作业提交过程
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210427094551952.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


## 3、HDFS 与 MapReduce 作业提交过程
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210427094601235.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

