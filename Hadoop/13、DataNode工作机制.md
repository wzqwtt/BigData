

# 一、DataNode工作机制
直接看一张图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415095425696.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

它的工作流程是：
- 当`DataNode`启动后，必须向`NameNode`汇报自己的块信息，然后定期（6个小时）扫描、上报自己所有块的信息。块信息包括：数据、数据长度、校验和（即数据完整性）、时间戳
- 每个`DataNode`必须定期向`NameNode`汇报说：我还活着。这个过程叫做心跳，心跳每三秒一次；如果超过==10分钟+30秒== `NameNode`没有收到`DataNode`的心跳，就会认为`DataNode`挂掉了

DN向NN汇报当前块信息的时间间隔，默认6个小时，在`hdfs-default.xml`文件中有配置：
```xml
<property>
	<name>dfs.blockreport.intervalMsec</name>
	<value>21600000</value>
	<description>Determines block reporting interval in milliseconds.</description>
</property>
```
DN扫描自己自己节点块的信息的时间，默认6个小时，同样在`hdfs-default.xml`文件中有配置：
```xml
<property>
	<name>dfs.datanode.directoryscan.interval</name>
	<value>21600s</value>
	<description>Interval in seconds for Datanode to scan data
	directories and reconcile the difference between blocks in memory and on 
	the disk. Support multiple time unit suffix(case insensitive), as described
	in dfs.heartbeat.interval.
	</description>
</property>
```

如果想改变时间间隔，可以将上述两个配置信息配置到`hdfs-site.xml`中，然后分发配置重启`hadoop`集群，配置就会生效
# 二、数据完整性

数据完整性就是要保证数据在网络传输中不发生错误，所以要采取一些校验数据的手段，比如**奇偶校验、CRC循环冗余校验**等

比如我们要在网络中传输
```
第一个数据：
1000100
第二个数据：
1000101
```

奇偶校验就是：数所有的位中有多少个`1`，如果`1`的个数是偶数，那么就在末尾添加`0`；反之，如果`1`的个数是奇数，那么就在末尾添加`1`。对于上面的两个数据，他们应该各自加：
```
第一个数据：
1000100			|	0
第二个数据：
1000101			|	1
```
这样只要在传输过去之后，再次计算校验位，然后与携带过来的校验位进行对比，就可以知道数据有没有传输失误了。

**但是，使用奇偶校验有一个很明显的问题，那就是如果两个位发生了改变，最后得出的校验位还是原来的数字。比如第一个数据：1000100在传输过程中，变成了1100110，数据发生了改变，但是校验位依旧是0，显然是有很大的问题的**

所有`Hadoop`就采用了另外一种更安全的校验方法`CRC循环冗余校验`。这种校验法会随机生成一个`多项式`，然后把原始数据与多项式进行除法操作，最后把得出的余数一起发送过去。接收端再对原始数据除一下多项式，如果得出的余数和发送端一样，就没有任何问题。

# 三、掉线时限参数设置

在前文提到的`心跳`中，如果`DataNode`在`10分钟+30秒`内不向`NameNode`心跳一下，就会认为`DataNode`挂掉了

那么这个`10分钟+30秒`是怎么得来的呢？

它的计算公式是：

```
TimeOut = 2 * dfs.namenode.heartbeat.recheck-interval + 10 * dfs.heartbeat.interval
```

看一下`hdfs-default.xml`的默认设置：其中`dfs.namenode.heartbeat.recheck-interval`的默认时间是`300000毫秒`即五分钟，`dfs.heartbeat.interval`的默认时间是`3秒`，于是超时时间就是`10分钟+30秒`
```xml
<property>
	 <name>dfs.namenode.heartbeat.recheck-interval</name>
	 <value>300000</value>
</property>
<property>
	 <name>dfs.heartbeat.interval</name>
	 <value>3s</value>
</property>
```

如果想要修改这些配置，可以把这些配置信息都配置到`hdfs-site.xml`中
