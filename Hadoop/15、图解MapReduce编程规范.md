
# 一、下载MapReduce的WordCount
要想了解`MapReduce`编程规范，直接看一下官方代码是怎么写的就知道了

打开`shell`工具，下载`hadoop-mapreduce-examples-3.1.3.jar`包，路径是：

```shell
/opt/module/hadoop-3.1.3/share/hadoop/mapreduce
```

然后下载：
```shell
sz hadoop-mapreduce-examples-3.1.3.jar
```
使用反编译工具查看`jar`包内容，[点我下载`反编译工具`](https://download.csdn.net/download/lesileqin/16681366)

打开反编译工具，把`jar`包拖进去，打开后是这样的（这里博主直接点到了`wordcount`代码块）：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415161056692.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 二、常用数据序列化类型
看一下`WordCount`代码：
```java
package org.apache.hadoop.examples;

//import部分省略

public class WordCount
{
  public static void main(String[] args) 
  	throws Exception
  {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length < 2) {
      System.err.println("Usage: wordcount <in> [<in>...] <out>");
      System.exit(2);
    }
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    for (int i = 0; i < otherArgs.length - 1; i++) {
      FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
    }
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[(otherArgs.length - 1)]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

  public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>
  {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, IntWritable>.Context context)
      throws IOException, InterruptedException
    {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      this.result.set(sum);
      context.write(key, this.result);
    }
  }

  public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>
  {
    private static final IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException
    {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        this.word.set(itr.nextToken());
        context.write(this.word, one);
      }
    }
  }
}
```
从上面的代码中，我们可以看到有很多之前没有见过的数据类型，这些类型都是`Hadoop`自己的类型，下表总结了`Java`类型与`Hadoop`数据类型的对比：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415161801226.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

可以发现除了`String`对应的是`Text`，其他的类型只不过是在最后加了关键字`Writable`，所以`Hadoop`的数据类型还是很好记忆与掌握的

# 三、MapReduce编程规范

从上面的案例代码中可以看到整个`WordCount`程序分为了三个部分，下面把他们的方法签名都抽取出来：

```java
public static void main(String[] args)
public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>
public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>
```
其中`main`对应的是`Driver`阶段；`IntSumReducer`对应的是`Reduce`阶段，继承了`Reducer`类；`TokenizerMapper`对应的是`Map`阶段，继承了`Mapper`类

可以看到继承的类后面跟了很多的泛型，接下来逐个击破！

## 1、Mapper阶段
- 用户自定义的`Mapper`要继承自己的父类，即继承了`Mapper`类
- `Mapper`后面跟的泛型，前两个是一个`k-v`键值对（用户可自定义），对应的是输入数据
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415163429466.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- `Mapper`的输出数据也是一个`K-V`键值对，对应的是后面两个泛型
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415164056164.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- `Mapper`中的业务逻辑写在`map()`方法中，`map()即MapTask进程`方法对每一个`k-v`调用一次，看下图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415170454214.png)
## 2、Reducer阶段
- 用户自定义的`Reducer`要继承自己的父类`Reducer`
- `Reducer`的输入数据类型对应`Mapper`的输出数据类型，也是`K-V`键值对，如下图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415171111865.png)
- `Reducer`的业务逻辑写在`reduce()`方法中，`ReduceTask`进程对每一组相同的`k`的`k-v`组调用一次`reduce()`方法
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021041517265266.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


## 3、Driver阶段
相当于`YARN`集群的客户端，用于提交整个程序到`YARN`集群，提交的是封装了`MapReduce`程序相关运行参数的`job`对象。后期详细解释



--------------
下一小节将以此编程规范编写`WordCount`程序！
