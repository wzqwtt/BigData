[toc]


------

# 一、HDFS写数据流程
## 1、文件写入流程
下图为文件写入流程剖析图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414152834733.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
想要上传一个`200M`的文件`ss.avi`到`HDFS`，它的写入流程是这样的：

- 客户端创建`Distributed FileSystem`，该模块向`HDFS`的老大哥`NameNode`请求上传文件`ss.avi`
- 老大哥`NameNode`是个做事很守规矩的人，所以他要检查权限和目录结构是否存在，如果存在就向`Distributed FileSystem`响应可以上传文件
- 但是`Distributed FileSystem`并不知道我上传的文件应该传给哪个小老弟（`DataNode1、2、3`），就问老大哥：”大哥，我应该上传到哪个`DataNodee`小老弟服务器啊？“
- 老大哥`NameNode`需要想一下文件应该存到哪个机器上（**这个过程叫做`副本存储节点选择`，下文有写**），想好之后就返回`dn1、2、3`说”这三个可以存文件“
- 这时候客户端就要创建`文件输出流FSDataOutPutStream`让他去上传数据
- 数据是需要通过`管道`进行传输的，所以`文件输出流`就需要先铺设管道，它首先请求`dn1`、`dn1`再调用`dn2`，最后`dn2`调用`dn3`，将这个通信管道建立完成，`dn1、2、3`逐级应答客户端建立完毕
- 这时候，客户端就开始往`dn1`上传第一个`Block`，以`Packet`为单位，`dn1`收到一个`Packet`就会传给`dn2`，`dn2`再传给`dn3`。**为了保证数据不丢失，三个机器在传数据的时候，都有自己的一个应答对列放`Packet`，直到收到下一台机器传回的`ack`应答信息，才把`Packet`删掉。**
- 当一个`Block`传输完成之后，客户端会再次请求`NameNode`上传第二个`Block`的服务器。重复执行上面的操作，直到所有的数据都上传完成

## 2、网络拓扑 - 节点距离计算

在`HDFS`写数据的过程中，`NameNode`会选择距离待上传数据最近距离的`DataNode`接收数据，需要计算出这个距离。

<font color='red'>节点距离：两个节点到达最近的功能祖先的距离总和</font>

现实生活中，服务器都会在`机架`上放着，然后形成一个`图`，看下图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414160630405.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

我们要想计算节点距离，可以把他抽象成一个图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414161903349.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
如果我们要算机器`d1->r2->n1`到`d2->r6->n0`的距离，则需要找到他们的共同祖先，然后把把路径相加就可以了，如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414162154288.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
则：`Distance(d1/r2/n1,d2/r6/no) = 6`


## 3、机架感知（副本存储节点选择）

`HDFS`写文件会把第一个副本存储在客户端所处节点上，第二个副本在另一个机架上的随机一个节点，第三个副本在第二个副本所在机架的另外一个随机节点上。如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414162511320.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


# 二、HDFS读数据流程
`HDFS`读数据流程如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210414162551729.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- 客户端通过`DistributedFileSystem`向`NameNode`请求下载文件，`NameNode`通过查询元数据，找到文件块所在的`DataNode`地址
- 挑选一台`DataNode`（就近原则，然后随机）服务器，请求读取数据
- `DataNode`开始传输数据给客户端（从磁盘里面读取数据输入流，以`Packet`为单位来做校验）
- 客户端以`Packet`为单位接收，现在本地缓存，然后写入目标文件
