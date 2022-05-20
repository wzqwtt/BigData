# 一、使用Xshell连接Linux
**连接之前一定要关闭防火墙！！！！！！！！！**

打开`Xshell`，点击`新建`：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402145142532.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

输入主机名称和ip地址，然后点击用户身份验证：

![在这里插入图片描述](https://img-blog.csdnimg.cn/202104021452383.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

输入用户名和密码，这里建议不要用`root`用户直接登陆：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402145338176.png)

最后点击确定，连接成功是这个样子的：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040215161830.png)

hadoop102、hadoop103同理

# 二、安装Java
使用`Xshell`文件传输工具把`jdk`和`hadoop`包上传到`linux`的`/opt/software`下

首先改变文件夹`/opt/software`的权限：

```shell
sudo chmod 777 /opt/software
```
更改完权限之后：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402152904391.png)

然后打开`Xshell`的文件传输工具，开始上传两个压缩包：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402153024891.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

上传完成之后：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402153049176.png)

然后使用把`JDK`解压到`/opt/module`下：
```bash
sudo tar -zxvf jdk-8u212-linux-x64.tar.gz -C /opt/module/
```

进入`/opt/module`下查看：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402153407267.png)

然后配置`JAVA`的环境变量，进入到文件`/etc/profile.d`，新建一个文件`my_env.sh`

```bash
cd /etc/profile.d	# 进入到该文件夹
sudo vim my_env.sh		# 自己创建一个文件
```
在`my_env.sh`文件中写入：
```shell
#JAVA_HOME
export JAVA_HOME=/opt/module/jdk1.8.0_212
export PATH=$PATH:$JAVA_HOME/bin
```

最后重新加载`profile`
```shell
sudo source /etc/profile
```

最后输入`java -version`校验是否安装成功：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402153732422.png)

# 三、安装Hadoop
进入`Hadoop`的压缩包目录下，将压缩包解压到`/opt/module`中
```bash
sudo tar -zxvf hadoop-3.1.3.tar.gz -C /opt/module/
```
进入`/opt/module`下查看：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402154057900.png)

安装成功，然后配置`hadoop`的环境变量

同样在`/etc/profile.d/my_enc.sh`后追加：

```bash
#HADOOP_HOME
export HADOOP_HOME=/opt/module/hadoop-3.1.3
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
```

最后重新加载`profile`
```shell
sudo source /etc/profile
```
运行`hadoop`：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402154327971.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 四、配置集群分发
集群分发的作用就是，比如在第一台服务器上修改了某个文件，服务器又很多，一台一台的修改太麻烦了，所以有个分发的脚本直接分发就好了。

在`/home/wzq/bin`目录下创建`xsync`文件：
```bash
[wzq@hadoop102 ~]$ cd /home/wzq/
[wzq@hadoop102 ~]$ mkdir bin
[wzq@hadoop102 ~]$ cd bin/
[wzq@hadoop102 bin]$ vim xsync
```

在该文件中写下下面的代码：
```bash
#!/bin/bash
#1. 判断参数个数
if [ $# -lt 1 ]
then
	echo Not Enough Arguement!
	exit;
fi

#2. 遍历集群所有机器
for host in hadoop102 hadoop103 hadoop104
do
	echo ==================== $host ====================
	#3. 遍历所有目录，挨个发送
	for file in $@
	do
		#4. 判断文件是否存在
		if [ -e $file ]
			then
				#5. 获取父目录
				pdir=$(cd -P $(dirname $file); pwd)
				#6. 获取当前文件的名称
				fname=$(basename $file)
				ssh $host "mkdir -p $pdir"
				rsync -av $pdir/$fname $host:$pdir
			else
				echo $file does not exists!
		fi
	done
done
```
使该文件具有执行权限
```bash
chmod +x xsync
```

测试脚本：
```bash
xsync /home/wzq/bin
```
到`hadoop103`上查看，成功拷贝：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402160840823.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

然后将脚本复制到`/bin`中，以便全局调用
```bash
cd /home/wzq/bin
sudo cp xsync /bin/
```

然后把`/opt/moudle/`的软件分发给`hadoop103和104`：
```bash
sudo ./bin/xsync /opt/module
```

==如果使用sudo进行xsync，一定要把它的路径补全==

然后分发`java和hadoop`的环境配置
```bash
sudo ./bin/xsync /etc/profile.d/my_env.sh
```

最后在`hadoop103和104`重新加载`/etc/profile`，使`java和hadoop`生效
```bash
source /etc/profile
```
最后分别测试

# 五、配置SSH无密登陆
即使使用了`xsync`分发脚本，但是每次分发都要输入两次密码，很是麻烦，所以配置一下`ssh免密登陆`

进入`/home/wzq/.ssh`：

```bash
cd /home/wzq/.ssh/
# 生成密钥
ssh-keygen -t rsa
# 分别配置免密登陆
ssh-copy-id hadoop102
ssh-copy-id hadoop103
ssh-copy-id hadoop104
```

最后在`hadoop103和104`以`wzq（非root）`的身份都执行一下以上命令

最后在`hadoop104`上免密登陆一下`hadoop102`：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402163521929.png)