
# 一、什么是ETL数据清洗？
`ETL`英文名：`Extract-Transform-Load`，用来讲数据从来源端经过抽取（`Extract`）、转换（`Transform`）、加载（`Load`）至目的端的过程

在运行核心业务`MapReduce`之前，往往要先对数据进行清洗，清理掉不符合用户要求的数据。==清理的过程往往只需要运行Mapper呈现，不需要运行Reduce程序==


# 二、案例实操

## 1、需求分析
需求：去除日志中字段个数小于等于11的日志

输入数据（字段间用空格分隔）：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421141301926.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

期望输出的每个字段都大于11


## 2、撸代码

在`Mapper`阶段：只需要将一行数据切割，判断长度是否大于11就可以了：
```java
package com.wzq.mapreduce.etl;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LogMapper extends Mapper<LongWritable, Text,Text, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1、获取数据
        String line = value.toString();

        //2、ETL数据清洗
        boolean res = check(line);

        //3、判断是否符合规则，然后写入
        if(!res){
            return;
        }
        context.write(value,NullWritable.get());

    }

    private boolean check(String line) {
        String[] split = line.split(" ");
        if(split.length > 11){
            return true;
        }
        return false;
    }
}
```
`Driver`阶段需要设置`ReduceTask`个数为0，不需要做其他变动：

```java
//不需要Reduce阶段，所以设置为0
job.setNumReduceTasks(0);
```
测试：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421141536979.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

