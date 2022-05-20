[toc]

-----

`NameNode`简称为`NN`，`SecondaryNameNode`简称为`2NN`

# 一、NN和2NN工作机制

## 1、元数据在什么地方？

`NameNode`中有元数据，那么元数据是存在什么地方呢？

如果元数据存储在`NN`的内存中，内存有两个特点就是：不可靠（断电即失）、计算速度快；这样保证不了数据的可靠性，一旦断电、元数据丢失，整个集群就无法工作了

如果元数据存储在`NN`的硬盘中，硬盘的特点是：可靠、计算速度慢；这样虽然保证了数据的可靠性，但是它的计算速度就慢了。

因此`HDFS`采用了`内存 + 磁盘`的方式，并且引入了两个概念`FsImage与Edits`：`FsImage`负责存储数据；`Edits`负责追加内容（不计算）。**每当元数据有更新或者或者添加元数据时，修改内存中的元数据并追加到`Edits`中。** 这样，一旦`NN`节点断电，就可以通过`FsImage`和`Edits`的合并，合成元数据。

但是随着`Edits`文件数据过大，它的效率降低，一旦断电、恢复元数据的时间也过长，因此需要定期进行`FsImage`和`Edits`的合并，这个工作如果只有`NN`做，又会效率过低。**所以引入一个新的节点`2NN`专门用于`FsImage`和`Edits`的合并。**

## 2、NN和2NN的工作机制

下面一张图解释了`NN`和`2NN`的工作机制：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414172753450.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
它分为两个阶段：

**第一阶段：`NameNode`启动：**

- 第一次启动`NN`格式化后，创建`FsImage`和`Edits`文件，如果不是第一次启动，直接加载`FsImage`和`Edits`到内存
- 客户端对元数据进行增删改的请求
- `NN`的`Edits`记录操作日志，更新滚动日志
- `NN`在内存中对元数据进行增删改

**第二阶段：`Secondary  NameNode`工作：**

- `2NN`会**定期或当`Edits`数据满的时候**询问`NN`是否需要`CheckPoint`（检查是否需要合并）
- `2NN`请求执行`CheckPoint`
- `NN`滚动正在写的`Edits`日志
- 然后将滚动前的编辑日志和镜像文件拷贝到`2NN`
- `2NN`加载编辑日志和镜像文件到内存，并合并
- 生成新的镜像文件`fsimage.chkpoint`
- 拷贝`fsimage.chkpoint`到`NN`
- `NN`将`fsimage.chkpoint`重新命名为`fsimage`

# 二、FsImage和Edits解析

## 1、FsImage和Edits文件所处位置

我们可以查看`NN`所在的`hadoop102`和`2NN`所在的`hadoop104`他们的`fsimage`和`edits`文件：

`hadoop102`路径：`/opt/module/hadoop-3.1.3/data/dfs/name/current`
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041417412092.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
`hadoop104`路径：`/opt/module/hadoop-3.1.3/data/dfs/namesecondary/current`
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414174352683.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
可以发现`NN`比`2NN`多一个文件`edits_inprogress...`，如果`NN`宕机，那么`2NN`可以帮助恢复`NN`元数据，但是只能恢复一部分，缺少的部分正是该文件。该文件存储的是正在执行的操作

**文件解读：**

- `Fsimage文件`：`HDFS`文件系统元数据的一个永久性的检查点，其中包含`HDFS`文件系统的所有目录和文件`inode`的序列化信息
- `Edits`文件：存放`HDFS`文件系统的所有更新操作的路径，文件系统客户端执行的所有写操作首先会被记录到`Edits`文件中
- `seen_txid`文件保存的是一个数字，就是最后一个`edits_`的数字
- 每次`NN`启动的时候都会将`Fsimage`文件读入内存，加载`Edits`里面的更新操作，保证内存中的元数据信息是最新的、同步的，可以看成`NN`启动的时候就将`Fsimage`和`Edits`文件进行了合并

## 2、oiv查看Fsimage文件
语法：
```shell
hdfs oiv -p 文件类型 -i 镜像文件 -o 转换后文件输出路径
```

现在使用这个命令查看一下`fsimage_0000000000000000394`：
```shell
hdfs oiv -p XML -i fsimage_0000000000000000394 -o /opt/software/fsimage.xml
```
执行完成之后，会输出一个文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414175523208.png)
下载到桌面：
```shell
sz fsimages.xml
```
打开之后：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414175624281.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 3、oev查看Edits文件
语法：
```shell
hdfs oev -p 文件类型 -i 编辑日志 -o 转换后文件输出路径
```
现在使用这个命令查看一下`edits_inprogress...`

```shell
hdfs oev -p XML -i edits_inprogress_0000000000000000398 -o /opt/software/edits.xml
```
已经有了该文件：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414175838221.png)
下载到本地查看：
```shell
sz edits.xml
```
因为现在没有执行任何操作，这个文件还是挺干净的：
![在这里插入图片描述](https://img-blog.csdnimg.cn/202104141800014.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 三、CheckPoint时间设置
通常情况下`2NN`每隔一个小时执行一次检查，可以查看`hdfs-default.xml`：

```xml
<property>
	 <name>dfs.namenode.checkpoint.period</name>
	 <value>3600s</value>
</property>
```



当操作数达到一百万，`2NN`执行一次：

```xml
<property>
	<name>dfs.namenode.checkpoint.txns</name>
	<value>1000000</value>
	<description>操作动作次数</description>
</property>
<property>
 	<name>dfs.namenode.checkpoint.check.period</name>
 	<value>60s</value>
	<description> 1 分钟检查一次操作次数</description>
</property>
```

如果想要更换时间或者更改操作数，可以配置到`hdfs-site.xml`
