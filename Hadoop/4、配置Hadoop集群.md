[toc]

# 一、Hadoop目录文件结构
查看`hadoop`的目录结构：
```bash
[wzq@hadoop102 hadoop-3.1.3]$ ll
总用量 176
drwxr-xr-x. 2 wzq wzq    183 9月  12 2019 bin
drwxr-xr-x. 3 wzq wzq     20 9月  12 2019 etc
drwxr-xr-x. 2 wzq wzq    106 9月  12 2019 include
drwxr-xr-x. 3 wzq wzq     20 9月  12 2019 lib
drwxr-xr-x. 4 wzq wzq    288 9月  12 2019 libexec
-rw-rw-r--. 1 wzq wzq 147145 9月   4 2019 LICENSE.txt
-rw-rw-r--. 1 wzq wzq  21867 9月   4 2019 NOTICE.txt
-rw-rw-r--. 1 wzq wzq   1366 9月   4 2019 README.txt
drwxr-xr-x. 3 wzq wzq   4096 9月  12 2019 sbin
drwxr-xr-x. 4 wzq wzq     31 9月  12 2019 share
```
重要目录：

- `bin目录`：存放对`Hadoop`相关服务（`hdfs，yarn，mapred`）进行操作的脚本
- `ect目录`：`Hadoop`的配置文件目录，存放`Hadoop`的配置文件
- `lib目录`：存放`Hadoop`的本地库（对数据进行压缩解压缩功能）
- `sbin目录`：存放启动或停止`Hadoop`相关服务的脚本
- `share目录`：存放`Hadoop`的依赖`jar`包、文档、和官方案例

----

# 二、Hadoop的运行模式

`Hadoop`的运行模式包括：本地模式、伪分布式以及完全分布式

- 本地模式：单机运行，只是用来演示一下官方案例。<font color='red'> 生产环境不用 </font>
- 伪分布式模式：也是单机运行，但是具备`Hadoop`集群的所有功能，一台服务器模拟一个分布式的环境，<font color='red'> 个别缺钱的公司用来测试，生产环境不用 </font>
- 完全分布式模式：多台服务器组成分布式环境，<font color='red'> 生产环境不用 </font>

**本人学习阶段全部使用`伪分布式模式`**

----

# 三、集群配置规划
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402170837911.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
需要注意的是：

- `NameNode`和`SecondaryNameNode`不安装在同一个虚拟机
- `ResourceManager`很消耗内存，不和`NameNode`与`SecondaryNameNode`配置在同一台虚拟机


----
# 四、配置文件说明

只要用户想修改某一默认配置文件中的配置，才需要修改自定义配置文件，更改相应属性值

## 1、默认配置文件

|默认文件| 文件存放在Hadoop的jar包中的位置 |
|--|--|
| **core-default.xml** | hadoop-common-3.1.3,jar/core-default.xml  |
| **hdfs-default.xml** | hadoop-hdfs-3.1.3,jar/hdfs-default.xml  |
| **yarn-default.xml** | hadoop-yarn-common-3.1.3,jar/yarn-default.xml  |
| **mapred-default.xml** | hadoop-mapred-client=core-3.1.3,jar/mapred-default.xml  |


## 2、用户自定义配置文件

`core-site.xml、hdfs-site.xml、yarn-site.xml、mapred-site.xml`均存放在`$HADOOP_HOME/etc/hadoop`这个路径上，用户可以根据项目需求重新进行修改配置

# 五、配置集群
进入到`$HADOOP_HOME/etc/hadoop`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402171653864.png)
## 1、核心配置文件 <font color='red'> core-site.xml </font>

```bash
vim core-site.xml;
```
将以下代码插入到`<configuration></configuration>`标签中
```xml
<!-- 指定 NameNode 的地址 -->
<property>
	<name>fs.defaultFS</name>
	<value>hdfs://hadoop102:8020</value>
</property>
<!-- 指定 hadoop 数据的存储目录 -->
<property>
	<name>hadoop.tmp.dir</name>
	<value>/opt/module/hadoop-3.1.3/data</value>
</property>
<!-- 配置 HDFS 网页登录使用的静态用户为 你的用户名-->
<property>
	<name>hadoop.http.staticuser.user</name>
	<value>你的用户名！！！！<alue>
</property>
```

## 2、HDFS配置文件 <font color='red'> hdfs-site.xml </font>
```bash
vim hdfs-site.xml
```
将以下代码插入到`<configuration></configuration>`标签中
```xml
<!-- nn web 端访问地址-->
<property>
	<name>dfs.namenode.http-address</name>
	<value>hadoop102:9870</value>
</property>
<!-- 2nn web 端访问地址-->
<property>
	<name>dfs.namenode.secondary.http-address</name>
	<value>hadoop104:9868</value>
</property>
```

## 3、YARN配置文件 <font color='red'> yarn-site.xml </font>
```bash
vim yarn-site.xml
```
将以下代码插入到`<configuration></configuration>`标签中：
```xml
<property>
    <name>yarn.nodemanager.resource.memory-mb</name>
    <value>22528</value>
    <discription>每个节点可用内存,单位MB</discription>
</property>

<property>
    <name>yarn.scheduler.minimum-allocation-mb</name>
    <value>1500</value>
    <discription>单个任务可申请最少内存，默认1024MB</discription>
</property>

<property>
    <name>yarn.scheduler.maximum-allocation-mb</name>
    <value>16384</value>
    <discription>单个任务可申请最大内存，默认8192MB</discription>
</property>

<!-- Site specific YARN configuration properties -->
<!-- 指定 MR 走 shuffle -->
<property>
	<name>yarn.nodemanager.aux-services</name>
	<value>mapreduce_shuffle</value>
</property>
<!-- 指定 ResourceManager 的地址-->
<property>
	<name>yarn.resourcemanager.hostname</name>
	<value>hadoop103</value>
</property>
<!-- 环境变量的继承 -->
<property>
	<name>yarn.nodemanager.env-whitelist</name>

	<value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CO
	NF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAP
	RED_HOME</value>
</property>
```
## 4、MapReduce配置文件 <font color='red'> mapred-site.xml </font>
```bash
vim mapred-site.xml
```
将以下代码插入到`<configuration></configuration>`标签中：
```xml
<!-- 指定 MapReduce 程序运行在 Yarn 上 -->
<property>
     <name>mapreduce.map.memory.mb</name>
     <value>1500</value>
     <description>每个Map任务的物理内存限制</description>
 </property>

 <property>
     <name>mapreduce.reduce.memory.mb</name>
     <value>3000</value>
     <description>每个Reduce任务的物理内存限制</description>
 </property>

 <property>
     <name>mapreduce.map.java.opts</name>
     <value>-Xmx1200m</value>
 </property>

 <property>
     <name>mapreduce.reduce.java.opts</name>
     <value>-Xmx2600m</value>
 </property>
<!-- 指定 MapReduce 程序运行在 Yarn 上 -->
<property>
	<name>mapreduce.framework.name</name>
	<value>yarn</value>
</property>
<property>
    <name>yarn.app.mapreduce.am.env</name>
    <value>HADOOP_MAPRED_HOME=/opt/module/hadoop-3.1.3</value>
</property>
<property>
    <name>mapreduce.map.env</name>
    <value>HADOOP_MAPRED_HOME=/opt/module/hadoop-3.1.3</value>
</property>
<property>
    <name>mapreduce.reduce.env</name>
    <value>HADOOP_MAPRED_HOME=/opt/module/hadoop-3.1.3</value>
</property>
```

## 5、集群分发配置文件
```bash
xsync /opt/module/hadoop-3.1.3/etc/hadoop/
```
效果如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402180052256.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
然后去另外两台机器验证有没有分发成功

# 六、配置历史服务器<font color='red'> 端口：19888</font>

为了查看程序的历史运行情况，需要配置一下历史服务器。

在配置历史服务之前，需要在`hadoop103`上停掉`yarn`

```bash
[wzq@hadoop103 hadoop-3.1.3]$ sbin/stop-yarn.sh
```

## 1、配置mapred-site.xml

编辑`mapred-site.xml`：

```bash
[wzq@hadoop103 hadoop-3.1.3]$ vim etc/hadoop/mapred-site.xml
```

在`mapred-site.xml`中插入以下代码：

```xml
<!-- 历史服务器端地址 -->
<property>
	 <name>mapreduce.jobhistory.address</name>
	 <value>hadoop102:10020</value>
</property>
<!-- 历史服务器 web 端地址 -->
<property>
	 <name>mapreduce.jobhistory.webapp.address</name>
	 <value>hadoop102:19888</value>
</property>
```

## 2、分发配置

配置完成之后，一定要把配置文件分发到另外两台虚拟机

```bash
[wzq@hadoop102 hadoop-3.1.3]$ xsync etc/hadoop/
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404095404218.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 3、在hadoop102上启动历史服务器

在启动历史服务器之前，需要先在`hadoop103`上启动`yarn`：

```bash
[wzq@hadoop103 hadoop-3.1.3]$ sbin/stop-yarn.sh
```

随后在`hadoop102`的`hadoop/bin`目录上启动历史服务器：

```bash
[wzq@hadoop102 bin]$ mapred --daemon start historyserver
```

## 4、查看历史服务器是否启动成功

使用`jps`命令查看：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404095833518.png)
搞个`wordcount`程序测试历史服务器：

```bash
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.3.jar wordcount /input /woutput
```

回车等待运行成功之后，打开`http://hadoop103:8088`，可以看到有一个任务已经成功运行：
![在这里插入图片描述](https://img-blog.csdnimg.cn/202104041001210.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
点击蓝色的`history`，就可以查看历史服务器的内容了：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404100405348.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

上图中可以看到点击`history`后跳转到了`19888`端口

但是值得我们注意的是，右下角有个`logs`，这里是查看程序运行的日志，现在点击一下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404100449632.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
报错了，这是因为还没有配置日志聚集，下面来配置一下。

----

# 七、配置日志的聚集

如下图所示，每个`hadoop服务器`都有自己的日志，但是如果程序出了bug，在单个服务器上查看日志是很不方便的，所以`hadoop`就做了`日志聚集`的功能，他把所有服务器的日志都聚集到了`hdfs`
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404101248515.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
日志聚集的好处就是可以方便查看程序的运行详情，方便开发调试

## 1、配置yarn-site.xml

编辑`yarn-site.xml`：

```bash
[wzq@hadoop102 hadoop-3.1.3]$ vim etc/hadoop/yarn-site.xml
```

在`yarn-site.xml`最后插入以下代码：

```xml
<!-- 开启日志聚集功能 -->
<property>
	 <name>yarn.log-aggregation-enable</name>
	 <value>true</value>
</property>
<!-- 设置日志聚集服务器地址 -->
<property> 
	 <name>yarn.log.server.url</name> 
	 <value>http://hadoop102:19888/jobhistory/logs</value>
</property>
<!-- 设置日志保留时间为 7 天 -->
<property>
	 <name>yarn.log-aggregation.retain-seconds</name>
	 <value>604800</value>
</property>
```

## 2、分发配置

配置完成之后要做集群分发：

```bash
[wzq@hadoop102 hadoop-3.1.3]$ xsync etc/hadoop/
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404101747651.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 3、重启yarn和HistoryServer

因为修改了`yarn`的配置文件，所以需要在`hadoop103`上重启`yarn`和在`hadoop102`上重启`HistoryServer`历史服务器

停止`yarn`：

```bash
[wzq@hadoop103 hadoop-3.1.3]$ sbin/stop-yarn.sh 
```

停止`HistoryServer`：

```bash
[wzq@hadoop102 hadoop-3.1.3]$ mapred --daemon stop historyserver
```

启动`yarn`：

```bash
[wzq@hadoop103 hadoop-3.1.3]$ sbin/start-yarn.sh 
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404102509981.png)
启动`historyserver`：

```bash
[wzq@hadoop102 hadoop-3.1.3]$ mapred --daemon start historyserver
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404102434892.png)


## 4、测试

删除刚刚测试历史服务器中自动生成的`woutput`文件夹：

```bash
[wzq@hadoop103 hadoop-3.1.3]$ hadoop fs -rm -r /woutput
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404102728369.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
执行`wordcount`程序：

```bash
[wzq@hadoop102 hadoop-3.1.3]$ hadoop jar  share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.3.jar wordcount /input /output
```

在程序运行期间，可以打开`yarn`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404102921801.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
可以看到正在执行，等待执行完成，点击`History`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404102953580.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
再点进去`logs`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404103011434.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
日志聚集配置成功


# 八、常用端口号说明

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404111628740.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

