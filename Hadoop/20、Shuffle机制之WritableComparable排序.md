[toc]

----

# 一、WritableComparable排序是什么东西？
`Map`之后、`Reduce`之前的数据处理过程统称为`Shuffle`机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210418172419410.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
`WritableComparable`排序是`Shuffle`的一部分功能，它的作用是==如果自定义数据类型做为key，则实现该接口，对所需要的字段进行排序。==


`MapReduce`规定必须对`key`进行排序，如果`key`的数据类型是自定义类型，而且没有实现`WritableComparable`接口的`compartTo`方法，那么就会**报错**！

默认排序是按照==字典顺序==排序的，且实现该排序的方法是==快速排序==

看一下`WritableComparable`接口：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041915483251.png)
既然该接口继承了两个接口：

- `Writable`：是实现序列化的接口，[这个在之前的博客中有解释及对应的案例，感兴趣的读者可以看一下](https://blog.csdn.net/lesileqin/article/details/115771389)
- `Comparable`：实现比较的接口，隶属于`java.util.*`包
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419155210621.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 二、排序分类
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419155412474.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 三、案例分析
## 1、全排序与二次排序
根据[前几篇博客（序列化）](https://blog.csdn.net/lesileqin/article/details/115771389)中的案例再进一步：**对总流量进行倒叙排序，如果总流量相等，则按照上行流量倒叙排序**

输入数据为：序列化案例处理后的数据：

```java
手机号			上行		下行		总流量
13590439668		1116	954		2070
13630577991		6960	690		7650
```

输出数据为对总流量从大到小排序后的数据
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041916040252.png)
### 1）需求分析
`MapReduce`默认对`key`进行排序，那么在`Mapper`中输出的泛型就可以调换一下位置：
```java
public class FlowMapper extends Mapper<LongWritable, Text, FlowBean, Text>
```
同样的，`Reducer`中作为输入的泛型要与`Mapper`的输出类型保持一致，最终输出的泛型还希望是`手机号->数据`：
```java
public class FlowReducer extends Reducer<FlowBean, Text, Text, FlowBean>
```
同样也需要重写`map()`和`reduce()`方法逻辑，也需要在`Driver`类中重新指定`Map`的输出类型

`FlowBean`类需要实现`WritableComparable`接口，重写实现序列化（本文不涉及，请参考前文）的操作与比较`compartTo()`方法

### 2）撸代码
`FlowBean`类添加`compartTo()`方法：
```java
@Override
public int compareTo(FlowBean o) {
    //对总流量进行排序
    if (this.sumFlow > o.sumFlow) {
        return -1;
    } else if (this.sumFlow < o.sumFlow) {
        return 1;
    } else {
        //如果相等，对上行流量进行排序
        if (this.upFlow > o.upFlow) {
            return -1;
        } else if (this.upFlow < o.upFlow) {
            return 1;
        } else {
            return 0;
        }
    }
}
```
因为替换了泛型，所以重写`Mapper`：
```java
package com.wzq.mapreduce.writableComparable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable, Text, FlowBean, Text> {

    private FlowBean outK = new FlowBean();
    private Text outV = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //获取一行数据，分割字符串
        String[] split = value.toString().split("\t");

        //封装
        outV.set(split[0]);
        outK.setUpFlow(Long.parseLong(split[1]));
        outK.setDownFlow(Long.parseLong(split[2]));
        outK.setSumFlow();

        //写输出
        context.write(outK, outV);
    }
}
```
`Reducer`：
```java
package com.wzq.mapreduce.writableComparable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<FlowBean, Text, Text, FlowBean> {

    @Override
    protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            context.write(value, key);
        }
    }
}
```
`Driver`类，需要指定`Map`的输出类型，替换输入输出路径，这里只贴出这两部分代码，其他保持不变：
```java
//设置Mapper输出的K-V类型
job.setMapOutputKeyClass(FlowBean.class);
job.setMapOutputValueClass(Text.class);
//设置输入输出路径
FileInputFormat.setInputPaths(job, new Path("D:\\BigData_workspace\\output\\output2"));
FileOutputFormat.setOutputPath(job, new Path("D:\\BigData_workspace\\output\\output5"));
```

执行之后的输出结果：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419161837531.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
## 2、区内排序（又叫部分排序）
现在对上面的案例，再进一步要求：**把排序输出的结果按照不同的手机号前缀输出到不同的文件中**
### 1）需求分析
基于前一个需求分析，增加自定义分区类，因为分区也属于`Shuffle`机制，所以它的泛型对应的也是`Mapper`输出的泛型：（[分区详解请点击我](https://blog.csdn.net/lesileqin/article/details/115836007)）
```java
public class FlowPartition extends Partitioner<FlowBean, Text>
```
然后重写`getPartition()`方法即可
### 2）撸啊撸代码
`FlowPartitioner`类：
```java
package com.wzq.mapreduce.writableComparable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FlowPartition extends Partitioner<FlowBean, Text> {
    @Override
    public int getPartition(FlowBean flowBean, Text text, int numPartitions) {
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
在`Driver`类中也需要指定使用`Partitioner`类，设置`ReduceTask`个数，替换输入输出文件路径：
```java
job.setPartitionerClass(FlowPartition.class);
job.setNumReduceTasks(5);

//设置输入输出路径
FileInputFormat.setInputPaths(job, new Path("D:\\BigData_workspace\\output\\output5"));
FileOutputFormat.setOutputPath(job, new Path("D:\\BigData_workspace\\output\\output6"));
```

最终运行测试：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210419163607807.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

