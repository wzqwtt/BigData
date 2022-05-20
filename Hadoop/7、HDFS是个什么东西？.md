# 一、HDFS概述
## 1、HDFS产生背景
随着数据量越来越大，一个操作系统放不下所有的数据，那么就分配到更多的操作系统管理的磁盘中，但是这样不方便管理和维护，这时候就迫切需要一种**系统**来**管理多台机器上的文件**，这就是分布式文件管理系统。`HDFS`只是分布式文件管理系统中的一种。

我们平常用的文件管理系统为：`NTFS`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405150757304.png)

## 2、HDFS定义

`HDFS`全称为`Hadoop Distributed File System`，它是一个文件系统，用于存储文件，通过目录树（比如`Linux`的目录就是目录树）来定位文件；其次它是分布式的，由很多服务器联合起来实现它的功能，集群中的服务器有各自的角色。

`HDFS`适合于 ==一次写入，多次多出==的场景，一个文件经过创建、写入和关闭之后就不需要改变。

# 二、HDFS优缺点

## 1、优点
- `高容错性`：数据可以自动保存多个副本，它通过增加副本的形式，提高容错性。当一个副本丢失以后，它可以自动恢复
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405151232390.png)
- `适合处理大数据`，从两个维度来讲
	* `数据规模`：能够处理数据规模可以达到`GB`、`TB`甚至`PB`级别的数据
	* `文件规模`：能够处理**百万规模**以上的文件数量，数量相当之大
- 可以构建在廉价机器上，通过多副本机制，提高可靠性


## 2、缺点
- 不适合低延时数据访问，比如毫秒级的存储数据，是做不到的
- 无法高效的对大量小文件进行存储
	* 存储大量小文件，会占用`NameNode`大量的内存来存储文件目录和块信息，因为`NameNode`的内存是有限的，所以这样是不可取的
	* 小文件存储的寻址时间会超过读取时间，违反了`HDFS`的设计目标
- 不支持并发写入、文件随机修改
	* **一个文件只能有一个写，不允许多线程同时写**
	* **仅支持数据的追加**（append），不支持文件的随时修改


# 三、HDFS组成架构
[官网](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)上有个图片，这个图片为我们展示了`HDFS`的组成架构，如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405151937631.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


## 1、NameNode（nn）<font color='red'>Master主管、管理者</font>

- 管理`HDFS`的名称空间
- 配置副本策略
- 管理数据块（`Block`）的映射信息
- 处理客户端（`client`）读写请求

## 2、DataNode <font color='red'>NameNode下命令，DataNode实际执行</font>

- 存储实际的数据块
- 执行数据块的`读/写`操作


## 3、Client <font color='red'>客户端</font>
- 文件切分。文件上传`HDFS`的时候，`Client`将文件切分成一个一个的`Block`，然后进行上传
- 与`NameNode`交互，获取文件的位置信息
- 与`DataNode`交互，读取或者写入数据
- `Client`提供一些命令来管理`HDFS`，比如`hdfs namenode -format`格式化
- `Client`可以通过一些命令来访问`HDFS`，比如对`HDFS`进行增删改查操作


## 4、Secondary NameNode <font color='red'>NameNode的秘书。NameNode挂掉，能替换NameNode并提供服务</font>

- 并不是必须的
- 辅助`NameNode`，分但其工作量
- 在紧急情况下，可辅助回复`NameNode`

# 四、HDFS文件块大小 <font color='#FF00FF'>重点</font>
`HDFS`中的文件在物理上是分块存储的，块的大小可以通过`dfs.blocksize`来规定，在`hadoop2.x/3.x`版本中默认是`128M`，在`1.x`版本中是`64M`

块的大小不能设置的太大也不能太小，因为：

- `HDFS`的块设置太小，会增加寻址时间，程序一直在找块的开始位置；
- 如果块设置的太大，从磁盘传输数据的时间会明显大于定位这个块开始位置所需的时间。导致程序在处理这块数据时，会非常慢。

==总结：HDFS块的大小设置主要取决于磁盘传输速率。==
