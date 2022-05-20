[toc]

------


# 一、序列化概述

## 1、什么是序列化
**序列化**就是<font color='red'>把内存中的对象，转换成字节序列</font>，然后用户存储到磁盘或网络传输

**反序列化**就是<font color='red'>把收到的序列化序列，转换成内存的对象</font>，两者是相反的

一方面，因为`MapReduce`在生产环境中是分布式工作的，那么就避免不了内存中的数据在网络上的传输，使用序列化是为了更加方便的传输

另一方面，`Hadoop`提供的序列化的数据类型太少， 并不能满足生产的需求。比如有时候期望输出的是一组数据，那么就需要一个`bean`对象存储这些基本的数据类型，这时候把`bean`实现序列化，放在`Mapper`和`Reducer`的泛型上就好了

## 2、Java序列化机制与Hadoop序列化的对比
`Java`序列化机制是由`Serializable`实现的，它的序列化会附加各种校验信息、头信息、继承体系等，如果用于网络传输，那么要传的数据太大了

`Hadoop`序列化机制只附加了简单校验信息，数据量小，有利于网络传输；如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041620512092.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
`Hadoop`序列化的特点：

- 紧凑：高效使用存储空间
- 快速：读写数据的额外开销小
- 互操作性强：支持多语言的交互

## 3、自定义bean对象实现序列化 <font color='red'>套路</font>

- `Bean`对象要实现`Writable`接口
- 重写接口中的序列化和反序列化方法，值得注意的是：==序列化的顺序要和反序列化的顺序完全一致==
- 必须要由空参构造方法
- 重写`toString`方法，用`\t`把数据分开
- `Hadoop`的`key`自带排序，如果把自定义的`bean`对象放在`key`中传输，则还需要实现`Comparable`接口


# 二、序列化案例实操

## 1、需求分析

==统计每一个手机号耗费的总上行流量、总下行流量、总流量==

输入数据格式：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416205651681.png)
数据：
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

输出数据格式：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416205713350.png)
 在`Map`阶段：
- 需要给四个泛型，前两个泛型就是固定的偏移量和这一行的数据，即`LongWritable,Text`
- 后两个泛型用`手机号`表示`key`，自定义`bean`实现序列化接口表示`上行流量、下行流量、总流量`
- 在此阶段，首先需要读取一行数据，按照`\t`切分数据，抽取三个数据，再封装到`context`

在`Reduce`阶段：
- 同样需要给四个泛型，前两个与`Map`保持一致
- 后两个泛型与我们期望的输出数据也一致
- 在此阶段，只需要累加上、下行流量，得到总流量，最后封装就好了

如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416210312881.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
## 2、撸代码
### 1）实现序列化
自定义`bean`对象，写入上行、下行、总流量属性，并添加`setter与getter`方法：
```java
package com.wzq.mapreduce.writable;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements Writable {

    private Long upFlow;
    private Long downFlow;
    private Long sumFlow;

    public Long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(Long upFlow) {
        this.upFlow = upFlow;
    }

    public Long getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(Long downFlow) {
        this.downFlow = downFlow;
    }

    public Long getSumFlow() {
        return sumFlow;
    }

    public void setSumFlow(Long sumFlow) {
        this.sumFlow = sumFlow;
    }
	//直接计算
    public void setSumFlow() {
        this.sumFlow = this.upFlow + this.downFlow;
    }
}
```
按照套路，需要重写两个接口：==注意：序列化与反序列化的顺序要保持一致！==
```java
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(this.upFlow);
        out.writeLong(this.downFlow);
        out.writeLong(this.sumFlow);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        //与序列化的顺序应该是一致的
        this.upFlow = in.readLong();
        this.downFlow = in.readLong();
        this.sumFlow = in.readLong();
    }
```
添加空参构造方法：
```java
    public FlowBean() {
    }
```
重写`toString`方法，以`\t`分割：
```java
    @Override
    public String toString() {
        return upFlow + "\t" + downFlow + "\t" + sumFlow;
    }
```
### 2）Mapper、Reducer、Driver编写
[按照`MapReduce`编程规范](https://blog.csdn.net/lesileqin/article/details/115729267?spm=1001.2014.3001.5501)，应该分为三个部分：

`Mapper`类:
```java
package com.wzq.mapreduce.writable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

    private Text outK = new Text();
    private FlowBean outV = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1、获取一行数据
        //1	13736230513	192.196.100.1	www.atguigu.com	2481	24681	200
        String line = value.toString();

        //2、切分数据，为一个字符串数组
        //1,13736230513,192.196.100.1,www.atguigu.com,2481,24681,200
        String[] split = line.split("\t");

        //3、封装数据
        //手机号：13736230513
        //上行流量：2481
        //下行流量：24681
        outK.set(split[1]);
        outV.setUpFlow(Long.parseLong(split[split.length - 3]));
        outV.setDownFlow(Long.parseLong(split[split.length - 2]));
        outV.setSumFlow();

        //4、提交数据
        context.write(outK, outV);
    }
}
```
`Reducer`类：
```java
package com.wzq.mapreduce.writable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

    private FlowBean outV = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        //1、循环遍历values，累加上下行流量
        long totalUpFlow = 0;
        long totalDownFlow = 0;
        for (FlowBean value : values) {
            totalUpFlow += value.getUpFlow();
            totalDownFlow += value.getDownFlow();
        }

        //2、封装数据
        outV.setUpFlow(totalUpFlow);
        outV.setDownFlow(totalDownFlow);
        outV.setSumFlow();

        //3、写数据
        context.write(key, outV);
    }
}
```
`Driver`类：
```java
package com.wzq.mapreduce.writable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //1、获取job
        Job job = Job.getInstance(new Configuration());

        //2、设置jar包路径
        job.setJarByClass(FlowDriver.class);

        //3、关联mapper和reducer
        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReducer.class);

        //4、设置mapper输出kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        //5、设置最终输出kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        //6、设置输入/输出路径
        FileInputFormat.setInputPaths(job, new Path("D:\\BigData_workspace\\input\\inputflow"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\BigData_workspace\\output\\output2"));

        //7、提交job
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
```
### 3）测试

至此代码编写完毕，点击运行，查看输出结果：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416210802758.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
也可以打包上传到`hadoop`集群测试哦~
