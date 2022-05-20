[toc]

----


# 一、Partition是个什么东西？
`Map`之后、`Reduce`之前的数据处理过程统称为`Shuffle`机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210418172419410.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
`Partition`分区是`Shuffle`的一部分功能，它的作用是==按照条件把结果输出到不同的文件（分区）中。==

如果通过`job.setNumReduceTasks(x)`设置多个`分区`，`Partition`的默认实现是根据`key`的`hashCode`对`ReduceTask`个数取模得到的，用户没法控制哪个`key`存储到哪个分区，`Partition`的默认实现代码是：
```java
public class HashPartitioner<K, V> extends Partitioner<K, V> {
	public int getPartition(K key, V value, int numReduceTasks) {
		return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
	}
}
```

如果不设置`ReduceTask`个数，它默认分区个数为`1个`

# 二、实现自定义Partition的步骤 <font color='red'>套路</font>

## 1、自定义类继承<font color='red'>Partition</font>，重写<font color='red'>getPartition()</font>方法

如果要自定义`Partition`，那么就需要写一个类，继承`Partition`抽象类，实现它的`getPartition`方法
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419152908866.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

通常在重写的方法写分区逻辑，根据传进来的`K-V`键值对，指定哪个值，最终存到哪个文件中去
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419141535173.png)

## 2、指定自定义的<font color='red'>Partition</font>
自定义的类，要捆绑到`job`中，否则`job`还会走默认的分区代码块，指定自定义的方法是：

```java
job.setPartitionerClass(实现Partition类的名字.class);
```

## 3、设置相应的数量的<font color='red'>ReduceTask</font>
要根据自定义`Partition`的逻辑设置相应数量的`ReduckTask`，设置任务个数的方法是：
```java
job.setNumReduceTasks(个数Int值);
```
需要注意的是：

- 如果`ReduceTask`的数量 ==>== `getPartition`的数量，则会多产生几个空的输出文件`part-r-000xx`
- 如果`ReduceTask`的数量 ==<== `getPartition`的数量，则有一部分数据会丢失，会抛出异常
- 如果`ReduceTask`的数量 ==1== ，则不管`MapTask`输出多少个分区文件，最终都交给一个`ReduceTask`，最终也就只会产生一个结果文件
- **分区号必须从零开始，逐一累加**

# 三、分区案例实操
## 1、需求分析
需要求[前几篇博客](https://blog.csdn.net/lesileqin/article/details/115771389)中的序列化的需求一样：统计手机号的上行流量、下行流量与总流量，但是最终要按照手机的归属地不同输出到不同的文件中去。

输入数据：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419142752654.png)
```java
1	13736230513		192.196.100.1	www.atguigu.com		2481	24681	200
2	13846544121		192.196.100.2						264		0		200
3 	13956435636		192.196.100.3						132		1512	200
4 	13966251146		192.168.100.1						240		0		404
5 	18271575951		192.168.100.2	www.atguigu.com		1527	2106	200
6 	84188413		192.168.100.3	www.atguigu.com		4116	1432	200
7 	13590439668		192.168.100.4						1116	954		200
8 	15910133277		192.168.100.5	www.hao123.com		3156	2936	200
9 	13729199489		192.168.100.6						240		0		200
10 	13630577991		192.168.100.7	www.shouhu.com		6960	690		200
11 	15043685818		192.168.100.8	www.baidu.com		3659	3538	200
12 	15959002129		192.168.100.9	www.atguigu.com		1938	180		500
13 	13560439638		192.168.100.10						918		4938	200
14 	13470253144		192.168.100.11						180		180		200
15 	13682846555		192.168.100.12	www.qq.com			1938	2910	200
16 	13992314666		192.168.100.13	www.gaga.com		3008	3720	200
17 	13509468723		192.168.100.14	www.qinghua.com		7335	110349	404
18 	18390173782		192.168.100.15	www.sogou.com		9531	2412	200
19 	13975057813		192.168.100.16	www.baidu.com		11058	48243	200
20 	13768778790		192.168.100.17						120		120		200
21 	13568436656		192.168.100.18	www.alibaba.com		2481	24681	200
22 	13568436656		192.168.100.19						1116	954		200
```

期望输出数据（手机号 136、137、138、139 开头都分别放到一个独立的 4 个文件中，其他开头的放到一个文件中）：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419142816196.png)

那么只需要自定义一个`分区`，实现它的方法就好了呀

## 2、撸代码
`Mapper`、`Reducer`、`Driver（稍微更改）`三个类的代码与[之前博客](https://blog.csdn.net/lesileqin/article/details/115771389)相同

创建一个`FlowPartition`类，写代码：
```java
package com.wzq.mapreduce.partitioner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FlowPartitioner extends Partitioner<Text, FlowBean> {

    @Override
    public int getPartition(Text text, FlowBean flowBean, int numPartitions) {
        //1、截取手机号前三位
        String prePhone = text.toString().substring(0, 3);

        //2、设置标记变量，标记最后输出到哪个分区
        int partition;

        //3、对比手机号，给标记变量赋值
        if ("136".equals(prePhone)) {
            partition = 0;
        } else if ("137".equals(prePhone)) {
            partition = 1;
        } else if ("138".equals(prePhone)) {
            partition = 2;
        } else if ("139".equals(prePhone)) {
            partition = 3;
        } else {
            partition = 4;
        }

        return partition;
    }
}
```

在`Driver`类中绑定`Partition`类，设置`ReduceTask`个数：
```java
job.setPartitionerClass(FlowPartitioner.class);
job.setNumReduceTasks(5);
```

执行后，五个文件：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419143557898.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

