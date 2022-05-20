# 一、数据压缩概述

**压缩的好处和坏处：**
- 压缩的优点：减少磁盘IO、减少磁盘存储空间
- 压缩的缺点：增加CPU开销

**压缩原则：**
- 运算密集型的Job，少用压缩
- IO密集型的Job，多用压缩

# 二、MapReduce支持的压缩编码
## 1、压缩算法对比介绍

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421151337920.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 2、压缩性能对比

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421151350465.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

还有一个压缩算法`Snappy`，它的压缩速度在`250MB/s`，解压速度在`500MB/s`


# 三、压缩方式的选择
压缩方式选择时重点考虑：==压缩/解压速度、压缩率（压缩后存储大小）、压缩后是否可以支持切片==

- `Gzip`压缩：
	- 优点：压缩率比较高
	- 缺点：不支持`Split`；压缩/解压速度一般
- `Bzip2`压缩：
	- 优点：压缩率高；支持`Split`
	- 缺点：压缩/解压速度慢
- `Lzo`压缩：
	- 优点：压缩/解压速度比较快；支持`Split`
	- 缺点：压缩率一般；==想支持切片需要额外创建索引==
- `Snappy`压缩：
	- 优点：压缩和解压缩速度块
	- 缺点：不支持`Split`；压缩率一般


# 四、压缩位置选择
压缩可以在`MapReduce`作用的任意阶段启用

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421151846966.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
# 五、压缩参数配置
为了支持多种压缩/解压缩算法，`Hadoop`引入了编码/解码器：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421151932629.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

要在`Hadoop`中启用压缩，可以配置如下参数：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210421152130202.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 六、压缩案例实操
## 1、Map端采用压缩
只需要在`Driver`类写下面的代码就好了：
```java
// 开启 map 端输出压缩
conf.setBoolean("mapreduce.map.output.compress", true);
// 设置 map 端输出压缩方式
conf.setClass("mapreduce.map.output.compress.codec", BZip2Codec.class,CompressionCodec.class);
```
`Mapper和Reducer`保持不变

## 2、Reduce端采用压缩
只需要在`Driver`类写下面的代码就好了：
```java
// 设置 reduce 端输出压缩开启
FileOutputFormat.setCompressOutput(job, true);
// 设置压缩的方式
FileOutputFormat.setOutputCompressorClass(job, BZip2Codec.class); 
```
`Mapper和Reducer`保持不变，在这里配置输出结果也是压缩包
