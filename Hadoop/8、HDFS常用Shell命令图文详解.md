# 一、命令大全
在终端输入`hadoop fs`或者`hdfs dfs`可以查看所有的命令：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405162806930.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

如果想要知道某个命令具体是怎么用的，可以使用`-help`输出这个命令参数

比如：

```shell
hadoop fs -help rm
```
输出的内容有：该命令的解释以及可以追加的参数

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405162958246.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

---

# 二、上传

## 1、-mkdir	创建文件夹

```shell
hadoop fs -mkdir /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405164034296.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 2、<font color='red'>-moveFromLocal</font>	从本地剪切粘贴到HDFS

```shell
vim shuguo.txt
# 在该文件中输入：shuguo

# 剪切粘贴到HDFS
hadoop fs -moveFromLocal ./shuguo.txt /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405164540629.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405164606839.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 3、<font color='red'>-copyFromLocal 或 -put</font>从本地复制到HDFS

**`-copyFromLocal`等同于`-put`，在生产环境中更多使用`-put`**

```shell
vim weiguo.txt
# 在该文件中输入：weiguo

# 复制到HDFS
hadoop fs -copyFromLocal weiguo.txt /sanguo
# 或
hadoop fs -put weiguo.txt /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405165043721.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040516511525.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 4、<font color='red'>-appendToFile</font>	追加一个文件到已存在的文件末尾

```shell
vim liubei.txt
# 在该文件中输入：liubei

# 执行追加
hadoop fs -appentToFile liubei.txt /sanguo/shuguo.txt
```

在这里踩个坑，执行插入命令之后如果报以下错误：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405170737658.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
需要在`hadoop3.1.3/etc/hadoop/hdfs-site.xml`中插入以下配置：
```xml
<!--解决Failed to replace a bad datanode on the existing pipeline due to no more good datanodes being availa 异常 -->
<property>
  <name>dfs.client.block.write.replace-datanode-on-failure.enable</name>
  <value>true</value>
</property>
<property>
  <name>dfs.client.block.write.replace-datanode-on-failure.policy</name>
  <value>NEVER</value>
</property>
```

然后分发配置，重启`hdfs`，再次执行上面的命令，到`hdfs`中查看效果：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405170928461.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 三、下载
## <font color='red'>-copyToLocal 或 -get</font>	从HDFS拷贝到本地

**`-copyToLocal `等同于`-get`，在生产环境中更多使用`-get`**

```shell
# 从hdfs中下载shuguo.txt，在此命令中还可以更改文件名
hadoop fs -copyToLocal /sanguo/shuguo.txt ./shuguo1.txt
# 或者使用get
hadoop fs -get /sanguo/shuguo.txt ./shuguo2.txx
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405171312590.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 四、HDFS直接操作

## 1、<font color='red'>-ls</font>	显示目录信息

```shell
hadoop fs -ls /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405171445537.png)
## 2、<font color='red'>-cat</font>	显示文件内容
```shell
hadoop fs -cat /sanguo/shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405171547698.png)
## 3、<font color='red'>-chgrp、-chmod、-chown</font>	修改文件所属权限

```shell
# 更改文件权限
hadoop fs -chmod 777 /sanguo/shuguo.txt

# 更改文件的拥有者
hadoop fs -chown wzq:wzq /sanguo/shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405171943498.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
## 4、<font color='red'>-cp 或 -mv</font>	从HDFS的一个路径拷贝或移动到HDFS的另一个路径

```shell
# 在hdfs根目录下创建文件夹jinguo
hadoop fs -mkdir jinguo

# 把/sanguo/shuguo.txt复制到/jinguo 
hadoop fs -cp /sanguo/shuguo.txt /jinguo

# 把/sanguo/weiguo.txt移动到/jinguo
hadoop fs -mv /sanguo/weiguo.txt /jinguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040517240077.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405172432427.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 5、<font color='red'>-tail</font>	显示一个文件的末尾1kb的数据
```shell
hadoop fs -tail /jinguo/shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405172626774.png)

## 6、<font color='red'>-rm 或 -rm -r</font>	删除文件[夹] ，或递归删除目录及内容

```shell
# 删除/sanguo/shuguo.txt
hadoop fs -rm /sanguo/shuguo.txt

# 递归删除/sanguo目录下所有内容
hadoop fs -rm -r /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040517284730.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 7、<font color='red'>-du</font>	统计文件夹的大小信息

显示整个文件夹大小信息：
```shell
hadoop fs -du -s -h /jinguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405173109875.png)

显示文件夹里的内容大小信息：

```shell
hadoop fs -du -h /jinguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210405173128464.png)
## 8、<font color='red'>-setrep</font> 设置HDFS中副本数量

```shell
hadoop fs -setrep 10 /jinguo/shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040517325854.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

