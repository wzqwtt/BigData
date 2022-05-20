# 一、下载Hadoop客户端环境
**`Hadoop`客户端环境与所需要微软运行库**

链接：[https://pan.baidu.com/s/1pjDSImaztgGUIOj7dMUTEQ](https://pan.baidu.com/s/1pjDSImaztgGUIOj7dMUTEQ) 
提取码：7qrk 

# 二、配置Hadoop环境变量
将下载的`Hadoop`依赖包放在一个==非中文且不能有空格存在==的路径中！

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407142552214.png)

右键单击`此电脑`=>`属性`=>`高级系统设置`=>`高级`=>`环境变量`，到如下图所示的界面：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407142911442.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

点击系统变量下的`新建`，输入：

- 变量名：`HADOOP_HOME`
- 变量值：`D:\opt\hadoop-3.1.0`（你的安装目录）
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407143242864.png)


点击确定，在系统变量对话框中找到`Path`，选中它，点击下面的`编辑`，之后会弹出一个界面，然后点击新建，复制以下内容到里面去：

```shell
%HADOOP_HOME%\bin
```
最后点击确定

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407143454671.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

这时候环境变量就算配置好了，验证一下环境：

找到`hadoop`安装目录，打开`bin`，找到`winutils.exe`，双击它：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407145401247.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

双击之后，如果**一闪而过**就表示安装成功了！

> 如果出现以下报错：
> 
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040714545133.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
> 
> 这表示缺少一些微软的运行库，直接安装就好了，安装包都在最上面的连接里面，双击安装重启电脑，再次运行就成功了！

# 三、在IDEA创建Maven工程

打开`IDEA`，点击新建工程，然后选择`Maven`：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407145923726.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

然后点击`Next`：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407150007709.png)

点击下一步，选择工程存放位置：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407150158317.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

点击`Finish`即可。

打开之后一定要把`Maven`改为自己的`Maven`：（如果没有搭建自己的Maven环境，可以参照我的另一篇博客搭建环境，[点击这串文字前往连接！](https://blog.csdn.net/lesileqin/article/details/112792828)）

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210407150512588.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


然后，打开`pom.xml`，将以下依赖粘贴到`<dependencies></dependencies>`中：
```xml
<!-- hadoop依赖 -->
<dependency>
	<groupId>org.apache.hadoop</groupId>
	<artifactId>hadoop-client</artifactId>
	<version>3.1.3</version>
</dependency>
<!-- Junit测试依赖 -->
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.12</version>
</dependency>
<!-- log4j日志记录依赖 -->
<dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>slf4j-log4j12</artifactId>
	<version>1.7.30</version>
</dependency>
```

然后在`src/resources`目录下，创建文件：`log4j.properties`，粘贴以下内容：
```
log4j.rootLogger=INFO, stdout 
log4j.appender.stdout=org.apache.log4j.ConsoleAppender 
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout 
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m%n 
log4j.appender.logfile=org.apache.log4j.FileAppender 
log4j.appender.logfile.File=target/spring.log 
log4j.appender.logfile.layout=org.apache.log4j.PatternLayout 
log4j.appender.logfile.layout.ConversionPattern=%d %p [%c] - %m%n
```

`Hadoop`的`API`相关操作将在下篇博客中详解！
