# 一、ReduceJoin 是什么
在现实世界，很多事情都是有关联的，这些关联的事务被抽象成数据的话，如果放在一个文件中是很麻烦的，所以人们一般会用多个文件进行存储，`Join`做的工作就是：把这些相关的文件都关联到一起。==现在可能非常抽象，请耐心往下看案例分析==

`Map端主要工作`：为来自不通表或文件的`k-v`键值对，打标签以区别不同来源的记录，**用连接字段作为`key`，其余部分和新加的标志作为`value`，最后进行输出。**

`Reduce端主要工作`：在`Reduce`端以连接字段作为`key`的分组已经完成，**只需要在每一个分组当中将哪些来源不同文件的记录（这个在`Map`阶段已经打标志）分开**，最后进行合并就odk了


# 二、ReduceJoin案例分析

## 1、需求分析
现在我们有两个表（以文件方式输入）：一个订单表，一个商品表，这两个表用`pid`关联了起来：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420195454688.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

最终期望输出的数据格式为：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420195523218.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

`order`订单表数据：
```java
1001	01	1
1002	02	2
1003	03	3
1004	01	4
1005	02	5
1006	03	6
```
`pd`商品表数据：
```java
01	小米
02	华为
03	格力
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420200850304.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

具体分析部分请看==撸代码==章节
## 2、撸代码

### 1）Bean对象
这个`Bean`对象，应该包含全部字段，再附加一个标记变量，标记该条记录来自哪个文件

所以需要设置五个属性：
```java
private String orderId; // 订单ID
private String pid; //商品ID
private int amount; //商品数量
private String pname;   //商品名称
private String flag;    //标志是哪个表
```
在`idea`中按住`alt+insert`键，生成他们的`getter`和`setter`方法，这里不再贴出

既然是一个`bean`对象，为了集群之间传输方便，需要实现`Writable`序列化接口，并且要加一个空参构造方法，且重写`toString`方法：
```java
//使用Writable必须加一个无参构造
public TableBean() {
}

@Override
public void write(DataOutput out) throws IOException {
	//字符串类型使用UTF写
    out.writeUTF(this.orderId);
    out.writeUTF(this.pid);
    out.writeInt(this.amount);
    out.writeUTF(this.pname);
    out.writeUTF(this.flag);
}

@Override
public void readFields(DataInput in) throws IOException {
    this.orderId = in.readUTF();
    this.pid = in.readUTF();
    this.amount = in.readInt();
    this.pname = in.readUTF();
    this.flag = in.readUTF();
}

//因为最终我们只输出三个属性到文件中，所以这里的toString只写三个字段
@Override
public String toString() {
    return orderId + "\t" + pname + "\t" + amount;
}
```
### 2）Mapper
这个`Mapper`就有点讲究了，首先看`Mapper`需要加的四个参数：前两个保持不变，后两个是`pid(Text)-TableBean`：
```java
public class TableMapper extends Mapper<LongWritable, Text,Text,TableBean>
```
因为我们要区分每条记录都是来自哪个文件的，所以为了节省内存开销，可以重写`Mapper`的初始化`setup`方法，[在以前的博客有解释`Mapper`的三个方法](https://blog.csdn.net/lesileqin/article/details/115766653)。在初始化方法中，通过切片获取文件名称：
```java
private String fileName;
@Override
protected void setup(Context context) throws IOException, InterruptedException {
    FileSplit split = (FileSplit) context.getInputSplit();
    fileName = split.getPath().getName();
}
```
然后是`map()`方法，在这个方法中，首先判断记录来自哪个文件，然后封装`TableBean`（没有的字段设置为空）最后使用`context`输出，`Mapper`完整代码：
```java
package com.wzq.mapreduce.reducejoin;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class TableMapper extends Mapper<LongWritable, Text,Text,TableBean> {

    private String fileName;
    private Text outK = new Text();
    private TableBean outV = new TableBean();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        FileSplit split = (FileSplit) context.getInputSplit();
        fileName = split.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] split = value.toString().split("\t");
        if(fileName.contains("order")){
            //设置key，对应order的pid字段
            outK.set(split[1]);
            //设置value
            outV.setOrderId(split[0]);
            outV.setPid(split[1]);
            outV.setAmount(Integer.parseInt(split[2]));
            outV.setPname("");
            outV.setFlag("order");
        }else {
            //设置key，对应pd的pid字段
            outK.set(split[0]);
            //设置value
            outV.setOrderId("");
            outV.setPid(split[0]);
            outV.setAmount(0);
            outV.setPname(split[1]);
            outV.setFlag("pd");
        }
        context.write(outK,outV);
    }
}
```
### 3）Reducer
`Reducer`也需要四个泛型，前两个泛型对应`Mapper`输出的泛型；由于我们把属性都封装到了`Bean`，所以后面两个参数可以直接设置为`bean`与空：
```java
public class TableReducer extends Reducer<Text,TableBean,TableBean, NullWritable>
```
完整代码：
```java
package com.wzq.mapreduce.reducejoin;

import javafx.scene.control.Tab;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class TableReducer extends Reducer<Text,TableBean,TableBean, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<TableBean> values, Context context) throws IOException, InterruptedException {
        //该集合存储从order文件来的数据
        ArrayList<TableBean> order = new ArrayList<>();
        //因为pd文件只有一条对应的记录，所以这里直接new一个对象
        TableBean pd = new TableBean();

        //遍历键重复的对象
        for (TableBean value : values) {
            //获取文件名
            String flag = value.getFlag();
            //如果进来的是order
            if("order".equals(flag)){
                //新建一个临时变量
                TableBean tmp = new TableBean();
                try {
                    //属性对拷
                    BeanUtils.copyProperties(tmp,value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                //添加到集合中
                order.add(tmp);
            }else{
                try {
                    //属性对拷
                    BeanUtils.copyProperties(pd,value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        //循环遍历集合，设置每个bean的商品名称，最终通过context输出
        for (TableBean tableBean : order) {
            tableBean.setPname(pd.getPname());
            context.write(tableBean,NullWritable.get());
        }

    }
}
```
`Driver`类是固定的套路，这里不贴出，直接看结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420201143240.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)



# 三、ReduceJoin的缺点与MapJoin优点

在上节中，使用`ReduceJoin`完成了对两张表的分析，合并的操作是在`Reduce`阶段完成的，在集群工作中，`ReduceTask`的个数会少于`MapTask`的个数，`Reduce`端的处理压力太大，`Map`节点的运算负载则很低，资源利用率不高，且在`Reduce`阶段极易产生数据倾斜。

它的解决方案就是在`Map`端实现数据合并，`Map Join`适用于一张表十分小，一张表很大的场景，在`Map`端缓存多张表，提前处理业务逻辑，这样增加`Map`端业务，减少`Reduce`端数据的压力，尽可能的减少数据倾斜。

# 四、实现MapJoin的步骤

采用`DistributedCache分布式缓存`

- 在`Mapper`的`setup初始化`阶段，将文件读取到缓存集合中

- 在`Driver`驱动类中加载缓存：

  ```java
  //缓存windows文件到Task运行节点
  job.addCacheFile(new URI("file:///文件真实路径"))
  //如果时集群运行，则需要设置HDFS路径
  job.addCacheFile(new URI("hdfs://hadoop102:8020/文件路径"))
  ```

# 五、案例实操

[本博客的案例依旧采用上一篇的博客的案例](23、MapTask与ReduceTask工作机制.md)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421132558280.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 1、Driver类

```java
package com.wzq.mapreduce.mapjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TableDriver {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException {
        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(TableDriver.class);

        job.setMapperClass(TableMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //设置ReduceTask个数为0
        job.setNumReduceTasks(0);

        //获取cache文件
        job.addCacheFile(new URI("file:///D:/BigData_workspace/input/tablecache/pd.txt"));

        FileInputFormat.setInputPaths(job, new Path("D:\\BigData_workspace\\input\\inputtable2"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\BigData_workspace\\output\\outputtable2"));

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
```

## 2、Mapper类

```java
package com.wzq.mapreduce.mapjoin;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;

public class TableMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

    private HashMap<String, String> pd = new HashMap<>();
    private Text outK = new Text();

    //任务开始之前把缓存区数据读进一个集合
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[] cacheFiles = context.getCacheFiles();
        Path path = new Path(cacheFiles[0]);

        //获取系统文件对象，并开流
        FileSystem fs = FileSystem.get(context.getConfiguration());
        FSDataInputStream fis = fs.open(path);

        //通过包装流转换为reader，方便按行读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        String line;
        while (StringUtils.isNotEmpty(line = reader.readLine())) {
            String[] split = line.split("\t");
            //0代表pid，1代表商品名称
            pd.put(split[0], split[1]);
        }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //读取大表数据
        String[] fileds = value.toString().split("\t");

        //读取大表的每行数据，去pdMap取出name
        String pname = pd.get(fileds[1]);

        //封装
        outK.set(fileds[0] + "\t" + pname + "\t" + fileds[2]);

        //写出
        context.write(outK,NullWritable.get());
    }
}
```

测试结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421133027794.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

