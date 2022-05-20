# 一、OutputFormat是做什么的？

`OutputFormat`可以说是`MapReduce`处理过程的最后一步，由它负责把输出的信息输送到哪个文件或者哪个数据库，等等

`OutputFormat`是`MapReduce`输出的基类，所有实现`MapReduce`输出都实现了`OutputFormat`接口，下图为`OutputFormat`的几个常见实现类（请忽略画横线的，那是我自定义的）：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021042014165838.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

它的默认输出格式为`TextOutputFormat`

# 二、实现自定义OutputFormat的步骤 <font color='red'>套路</font>

- 自定义一个类继承`FileOutputFormat`，实现`RecordWriter`方法，实际上只需要调用`RecordWriter`最终返回就可以了
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420142314169.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- 改写`RecordWriter`方法，需要实现`write`和`close`方法
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420142833836.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
# 三、案例实操

## 1、需求分析
需求：过滤输入的`log`日志，==包含atguigu的网站输出到atguigu.log文件，其他的输出到other.log文件==

因为该案例只输出字符串，所以`Mapper`的参数应该这样写，`map`方法只需要把`Text`写到`context`就好了：
```java
public class LogMapper extends Mapper<LongWritable, Text,Text, NullWritable>
```

同理，`Reducer`应该这样定义：
```java
public class LogReducer extends Reducer<Text, NullWritable,Text,NullWritable>
```

因为`FileOutputFormat`需要加两个参数，它对应最终输出的类型，所以：
```java
public class LogOutputFormat extends FileOutputFormat<Text, NullWritable>
```
同理，`RecordWriter`的参数：
```java
public class LogRecordWriter extends RecordWriter<Text, NullWritable>
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420143036347.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


## 2、撸代码
`Mapper`：
```java
package com.wzq.mapreduce.outputformat;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LogMapper extends Mapper<LongWritable, Text,Text, NullWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	//只需要写到context里就好了，什么都不需要做
        context.write(value,NullWritable.get());
    }
}
```
`Reducer`：
```java
package com.wzq.mapreduce.outputformat;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class LogReducer extends Reducer<Text, NullWritable,Text,NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
    	//为了防止重复的数据，需要使用for遍历写
        for (NullWritable value : values) {
            context.write(key,NullWritable.get());
        }
    }
}
```
`OutputFormat`：
```java
package com.wzq.mapreduce.outputformat;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class LogOutputFormat extends FileOutputFormat<Text, NullWritable> {

    @Override
    public RecordWriter<Text, NullWritable> getRecordWriter(TaskAttemptContext job) throws IOException, InterruptedException {
    	//创建一个LogRecordWriter，把job传递给它，然后返回就好了
        LogRecordWriter lrw = new LogRecordWriter(job);
        return lrw;
    }

}
```
`RecordWriter`：
```java
package com.wzq.mapreduce.outputformat;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class LogRecordWriter extends RecordWriter<Text, NullWritable> {
    
    //两个流
    private FSDataOutputStream atguiguOut;
    private FSDataOutputStream otherOut;

    public LogRecordWriter(TaskAttemptContext job) {
        try {
            //在这里创建FileSystem，这里的Configuration应该是job的Configuration
            FileSystem fs = FileSystem.get(job.getConfiguration());
            //设置atguigu的输出路径
            atguiguOut = fs.create(new Path("D:\\BigData_workspace\\output\\logOutput\\atguigu.log"));
            //设置其他的输出路径
            otherOut = fs.create(new Path("D:\\BigData_workspace\\output\\logOutput\\other.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Text key, NullWritable value) throws IOException, InterruptedException {
        String log = key.toString();
        //判断是否包含atguigu
        if (log.contains("atguigu")) {
            //如果包含则使用atguigu流
            atguiguOut.writeBytes(log + "\n");
        } else {
            otherOut.writeBytes(log + "\n");
        }
    }

    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        //关闭两个流
        IOUtils.closeStream(atguiguOut);
        IOUtils.closeStream(otherOut);
    }
}
```
`Driver`：
```java
package com.wzq.mapreduce.outputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class LogDriver {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        //1、获取Job
        Job job = Job.getInstance(new Configuration());

        //2、设置jar包路径
        job.setJarByClass(LogDriver.class);

        //3、关联Mapper和Reducer
        job.setMapperClass(LogMapper.class);
        job.setReducerClass(LogReducer.class);

        //4、设置Map输出类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        //5、设置最终输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //关联OutPutFormat
        job.setOutputFormatClass(LogOutputFormat.class);

        //6、设置输入输出路径
        FileInputFormat.setInputPaths(job, new Path("D:\\BigData_workspace\\input\\inputoutputformat"));
        //因为mapreduce要输出计算成功信息，所以需要设置一个路径
        FileOutputFormat.setOutputPath(job, new Path("D:\\BigData_workspace\\output\\logOutput\\info"));

        //7、提交Job
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
```

测试：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420143851628.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
