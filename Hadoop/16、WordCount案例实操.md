[toc]


-----

# 一、需求分析
**统计给定文本中每一个单词出现的个数**

准备的文本数据：
```
atguigu atguigu
ss ss
cls cls
jiao
banzhang
xue
hadoop
wtt wtt wzq
wzq love wangtiantian
```
期望的输出数据：
```
atguigu	2
banzhang	1
cls	2
hadoop	1
jiao	1
love	1
ss	2
wangtiantian	1
wtt	2
wzq	2
xue	1
```
根据[上一篇博客](https://blog.csdn.net/lesileqin/article/details/115729267)写到的`MapReduce`编程规范，应该将该程序分为三个部分：
- `Mapper`类：负责数据的拆分
	* 将内容先转换为`String`
	* 使用`String.split()`将这一行的数据切分成单词
	* 将单词封装为`<wzq,1>`
- `Reducer`类：汇总数据，即聚合，统计出出现次数
	- 汇总各个`key`的个数
	- 输出该`key`的总次数
- `Driver`类：固定的<font color='red'>七大步套路</font>
	- 获取`Job`
	- 设置本程序`jar`包所在的本地路径
	- 关联`Mapper`和`Reducer`类
	- 指定`Mapper`输出数据的`k-v`类型
	- 指定最终输出的数据的`k-v`类型
	- 指定`job`输入与输出文件路径
	- 提交作业

如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416164646868.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)


# 二、环境准备
打开`idea`，新建一个`Maven`项目，打开`pom.xml`添加以下依赖：
```xml
<dependencies>
    <!-- hadoop依赖 -->
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-client</artifactId>
        <version>3.1.3</version>
    </dependency>
    <!-- Junit单元测试依赖 -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
    <!-- slf4j日志依赖 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.30</version>
    </dependency>
</dependencies>
```
新建一个包：`com.wzq.mapreduce.wordcount`

在此包下，新建三个类：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416164837513.png)

# 三、编写程序
==在下面的编写代码中，切记不要导错包，一定要导**mapreduce**包下的包，不要导入mapred的==
## 1、Mapper类

### 1）Mapper源码
该类需要继承`Mapper`类，我们先写上：

```java
public class WordCountMapper extends Mapper
```

按住`ctrl+b`，点击`Mapper`查看源码：

首先看`Contxt`这个抽象内部类：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416165718940.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

然后找到`run`方法，这个方法是用于执行`Mapper`操作的：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416165518218.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
在源码中`setup`和`cleanup`都是空方法，什么都不做：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416165853930.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
### 2）编写Mapper类代码
该类需要继承`Mapper`类，而在[上一篇博客](https://blog.csdn.net/lesileqin/article/details/115729267)中提到需要加四个泛型，分别是两个`k-v`键值对

前两个泛型是第一个`k-v`对应的是：每一行数据的偏移量与该行的内容，所以，可以写成`LongWritable,Text`

第二个`k-v`对应的是输出的数据：文本与`1`，所以可以写成：`Text,IntWritable`，即:

```java
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>
```
然后重写`map`方法，直接输入`map`按下回车，直接生成

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416170339296.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
因为传进来的是一行数据，并且`Text`数据类型并没有很丰富的操作，所以首先把它转换为`String`，然后利用`split(" ")`切割这一行数据，最终得到一个字符串数组：

```java
//1、获取 行
String line = value.toString();

//2、将“行”的数据按照空格分隔为字符串数组
String[] words = line.split(" ");
```
然后就是循环遍历该数组，往外写出数据了，我们可以这样写：
```java
//3、循环遍历，往外写出
for (String word : words) {
   	//设置K
   	Text outK = new Text();
    outK.set(word);
    //写出，因为传递的是Hadoop的数据类型，需要先对String转义
    context.write(outK,new IntWritable(1));
}
```
整体的代码是这样的：（==**包千万不要导错了！**==）
```java
package com.wzq.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1、获取 行
        String line = value.toString();

        //2、将“行”的数据按照空格分隔为字符串数组
        String[] words = line.split(" ");

		//3、循环遍历，往外写出
		for (String word : words) {
		   	//设置K
		   	Text outK = new Text();
		    outK.set(word);
		    //写出，因为传递的是Hadoop的数据类型，需要先对String转义
		    context.write(outK,new IntWritable(1));
		}
    }
}
```
但是我们注意到，如果数据量很大，每一行都要调用`map`方法，每次都要循环该行所有单词；所以不能在循环里面每次都`new`两次对象，这是非常耗费资源的，所以可以做一次变量提升：

最终代码：
```java
package com.wzq.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private Text outK = new Text();
    private IntWritable outV = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1、获取 行
        String line = value.toString();

        //2、将“行”的数据按照空格分隔为字符串数组
        String[] words = line.split(" ");

        //3、循环遍历，往外写出
        for (String word : words) {
            //设置K
            outK.set(word);
            //写出
            context.write(outK,outV);
        }
    }
}
```

## 2、Reducer类
### 1）Reducer源码
该类需要继承``Reducer``类，先写上：
```java
public class WordCountReducer extends Reducer
```
按住`ctrl+B`，点击`Reducer`，查看源码：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416171724505.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
看`run`方法：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416172021971.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
### 2）编写Reducer类
该类需要继承`Reducer`，同样的他也需要加四个泛型，两个`k-v`键值对

第一个键值对，对应的是`Mapper`的输出类型，应该填：`Text,IntWritable`

第二个键值对，是最终输出的类型，这里填写我们期望输出数据的类型：`Text,IntWritable`

```java
public class WordCountReducer extends Reducer<Text, IntWritable,Text,IntWritable>
```

这个类需要重写`reduce`方法，直接输入`reduce`按下回车：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416172557751.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
在这个方法里面，我们只需要做两件事：循环遍历`Iterable<IntWritable>`集合，把值相加；最终通过`Context`把值输出，这里同样做一次变量提升：

```java
package com.wzq.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordCountReducer extends Reducer<Text, IntWritable,Text,IntWritable> {

    private IntWritable outK = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable value : values) {
            sum += value.get();
        }
        outK.set(sum);

        context.write(key,outK);
    }
}
```
## 3、Driver类
前文的需求分析，已经提到了七大步，这里直接给出代码：

```java
package com.wzq.mapreduce.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //1、获取Job
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //2、设置jar包路径
        job.setJarByClass(WordCountDriver.class);

        //3、关联Mapper和Reducer
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        //4、设置map输出的K-V类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //5、设置最终输出K-V类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //6、设置输入/输出路径
        //一定要导入正确的包，用
        FileInputFormat.setInputPaths(job,new Path("D:\\BigData_workspace\\test"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\BigData_workspace\\output\\output1"));

        //7、提交Job
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
```


# 四、测试

## 1、本地测试
这时候代码都已经写好了，直接点击右键运行：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416173010789.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
打开指定的输出文件夹：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416173141501.png)
直接打开：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416173155904.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
`WordCount`案例成功！

## 2、提交到集群并测试

因为这是本地运行，真正的生产环境，我们需要把项目打成`jar`包，发布到`hadoop`集群上去

而且注意到`Driver`类，设置的输入输出依旧是windows的地址，把他改成从命令行获取：

```java
FileInputFormat.setInputPaths(job,new Path(args[0]));
FileOutputFormat.setOutputPath(job,new Path(args[1]));
```

然后打开`pom.xml`文件，添加`打jar包`插件，将以下代码添加到`pom.xml`中
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.6.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

然后点击`package`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416174032113.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
等待一会儿：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416174132853.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
打开这个`jar`包所在目录：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416174243181.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
把他拖到`xshell`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416174321305.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
然后输入以下命令测试：

```shell
hadoop jar wc.jar com.wzq.mapreduce.wordcount.WordCountDriver /input /output
```
jar后面的部分要写`Driver`类的全类名

等待片刻：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210416174701742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
测试成功
