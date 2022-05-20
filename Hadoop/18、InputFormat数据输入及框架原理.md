[toc]

----


# 一、切片与MapTask并行度决定机制
`MapTask`的并行度决定`Map`阶段的任务处理并发读，进而影响到整个`Job`的处理速度，引入两个概念：

- **数据块：**`Block`是`HDFS`物理上把数据分成一块一块，数据块是`HDFS`存储数据单位
- **数据切片：** 只是在逻辑上对输入进行分片，并不会在磁盘上将其切分成片进行存储。==数据切片是MapReduce程序计算输入数据的单位==，一个切片会对应启动一个`MapTask`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210417194324194.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 二、Job提交流程

提交一个`Job`要经过：

- 建立连接`connect()`
	- 在这里会判断该`Job`是本地运行环境还是`YARN`集群运行环境
- 提交`Job`，`submitJobInternal()`
	- 创建给集群提交数据的`Stag`路径—`getStagingDir()`
	- 获取`JobId`，并创建`Job`路径—`getNewJobID()`
	- 拷贝`jar`包到集群—`copyAndConfigureFiles()`与`uploadFiles`
	- 计算切片，生成切片的规划文件—`writeSplits`
	- 向`Stag`路径写`XML`配置文件—`writreXml()`
	- 最后提交`Job`，返回提交状态

如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041719511357.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 三、切片执行流程解析
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210417195131576.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 四、FileInputFormat切片机制
切片机制：
- 简单地按照文件的内容长度进行切片
- 切片的大小默认等于`Block`大小
- 切片时不考虑数据集整体，而是逐个针对每一个文件单独切片

比如输入数据有两个文件：
```
file1.txt			320M
file2.txt			10M
```
经过`FileInputFormat`的切片机制运算后，形成的切片信息如下：
```
file1.txt.split1			0~128
file1.txt.split2			128~256
file1.txt.split3			256~320
file2.txt.split1			0~19
```

源码中计算切片大小的公式：

```java
Math.max(minSize,Math.max(maxSize,blockSize));
mapreduce.input.fileinputformat.split.minsize=1		默认值为1
mapreduce.input.fileinputformat.split.maxsize=Long.MAXValue		默认值Long.MAXValue
```
默认情况下，切片大小=blocksize

切片大小设置：
- `maxsize`（切片最大值）：参数如果调的比`blockSize`小，则会让切片变小，而且就等于配置的这个参数值
- `minsize`（切片最小值）：参数调的比`blockSize`大，则可以让切片变得比`blockSize`还大

获取切片信息API：
```java
//获取切片的文件名称
String name = inputSplit.getPath().getName();
//根据文件类型获取切片信息
FileSplit inputSplit = (FileSplit) context.getInputSplit();
```
# 五、TextInputFormat

在运行`MapReuce`程序时，输入的文件格式包括：基于行的日志文件、二进制格式文件、数据库表等。针对不同的数据类型，`MapReduce`给用户提供了很多的接口

`FileInputFormat`常见的实现类包括：`TextInputFormat`、`KeyValueTextInputFormat`、`NLineInputFormat`、`CombineTextInputFormat`和自定义`InputFormat`等


`TextInputFormat` 是默认的 `FileInputFormat` 实现类。**按行读取每条记录**。键是存储该行在整个文件中的起始字节偏移量， `LongWritable` 类型。值是这行的内容，不包括任何行终止 符（换行符和回车符），`Text` 类型。

以下是一个示例，比如，一个分片包含了如下 4 条文本记录。 
```
Rich learning form
Intelligent learning engine 
Learning more convenient
From the real demand for more close to the enterprise 
```
每条记录表示为以下键/值对：
```
(0,Rich learning form)
(20,Intelligent learning engine)
(49,Learning more convenient)
(74,From the real demand for more close to the enterprise)
```
# 六、CombineTextInputFormat切片机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041720132094.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210417201328607.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210417201335860.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210417201345765.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

