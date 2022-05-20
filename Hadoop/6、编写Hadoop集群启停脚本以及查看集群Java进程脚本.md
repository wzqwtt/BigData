
==**在配置两个脚本之前，需要配置好ssh免密登陆**==

# 一、Hadoop集群启停脚本<font color='red'> myhadoop.sh</font>

## 1、编写脚本

在用户家目录的`bin`下，创建脚本`myhadoop.sh`

```bash
[wzq@hadoop102 ~]$ cd /home/wzq/bin/
[wzq@hadoop102 bin]$ vim myhadoop.sh
```

将以下代码插入到该文件中：
```shell
#!/bin/bash
if [ $# -lt 1 ]
then
	echo "No Args Input..."
	exit ;
fi

case $1 in
"start")
	echo " =================== 启动 hadoop 集群 ==================="
	echo " --------------- 启动 hdfs ---------------"
	ssh hadoop102 "/opt/module/hadoop-3.1.3/sbin/start-dfs.sh"
	echo " --------------- 启动 yarn ---------------"
	ssh hadoop103 "/opt/module/hadoop-3.1.3/sbin/start-yarn.sh"
	echo " --------------- 启动 historyserver ---------------"
	ssh hadoop102 "/opt/module/hadoop-3.1.3/bin/mapred --daemon start historyserver"
;;
"stop")
	echo " =================== 关闭 hadoop 集群 ==================="
	echo " --------------- 关闭 historyserver ---------------"
	ssh hadoop102 "/opt/module/hadoop-3.1.3/bin/mapred --daemon stop historyserver"
	echo " --------------- 关闭 yarn ---------------"
	ssh hadoop103 "/opt/module/hadoop-3.1.3/sbin/stop-yarn.sh"
	echo " --------------- 关闭 hdfs ---------------"
	ssh hadoop102 "/opt/module/hadoop-3.1.3/sbin/stop-dfs.sh"
;;
*)
	echo "Input Args Error..."
;;
esac
```

然后给该文件满权限：
```bash
chmod 777 myhadoop.sh
```

## 2、脚本解读

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404110113388.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


## 3、测试
关闭集群：

```bash
myhadoop.sh stop
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404110334672.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

启动集群：
```bash
myhadoop.sh start
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040411050489.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

然后使用`jps`命令查看是否启动成功：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404110529158.png)

这只是在`hadoop102`上查看了是否成功，同时也需要去`hadoop103和104`上使用`jps`命令查看，如果我们有非常多的服务器，逐个查看实在太麻烦了，所以编写一下，查看所有服务进程的脚本

# 二、查看三台服务器Java进程脚本 <font color='red'> jpsall</font>

## 1、编写脚本
在用户家目录的`bin`下，创建脚本`myhadoop.sh`

```bash
[wzq@hadoop102 ~]$ cd /home/wzq/bin/
[wzq@hadoop102 bin]$ vim jpsall
```

将以下代码插入到该文件中：
```shell
#!/bin/bash
for host in hadoop102 hadoop103 hadoop104
do
	echo =============== $host ===============
	ssh $host jps 
done
```

然后给该文件满权限：
```bash
chmod 777 myhadoop.sh
```

## 2、脚本解读

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210404111051227.png)

## 3、测试

直接在控制台输入：`jpsall`

![在这里插入图片描述](https://img-blog.csdnimg.cn/202104041112302.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

这样就直接查看到了所有主机的进程！


# 三、脚本分发

现在只在`hadoop102`上写了脚本，也就是说只能在`hadoop102`上面运行该脚本，现在做一下分发，让其余两台也都可以连接：

```bash
[wzq@hadoop102 ~]$ xsync /home/wzq/bin/
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040411141743.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

