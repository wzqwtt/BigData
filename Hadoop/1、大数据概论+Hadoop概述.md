# 一、大数据概论
大数据主要解决，海量数据的采集、存储和分析计算问题

大量、高速、多样、低价值密度

应用场景：

- 抖音：推荐你喜欢的视频
- 电商站内广告推荐：给用户推荐可能喜欢的商品
- 零售：分析用户消费习惯，为用户购买商品提供方便，从而提升商品销量，经典案例：纸尿裤+啤酒
- 物流仓储：京东物流，上午下单下午送达、下午下单次日上午送达
- 保险、金融、房产
- 人工智能+5G+物联网+虚拟与现实

下图为大数据部门组织结构：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402132532427.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

# 二、Hadoop

## 1、Hadoop是什么
- Hadoop是一个由Apache基金会所开发的分布式系统基础架构

- **主要解决，海量数据的存储和海量数据的分析计算问题**
- 广义上来说，Hadoop通常是指一个更广泛的概念——Hadoop生态圈
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402132749284.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
## 2、Hadoop的发展历史

- **Hadoop创始人Doug Cutting** 为了实现与谷歌类似的全文搜索功能，他在Lucene框架基础上进行优化升级，查询引擎和索引引擎
- 2001年年底Lucene成为Apache基金会的一个子项目
- 对于海量数据的场景，Lucene框架面对与谷歌同样的苦难，存储海量数据困难，检索海量速度慢
- 学习和模仿谷歌解决这些问题的办法：微型版Nutch
- 可以说谷歌是Hadoop的思想之源（谷歌在大数据方面的三篇论文）
  - GFS ---> HDFS
  - Map_Reduce ---> MR
  - BigTable ---> HBase
- 2003—2004年，谷歌公开了部分GFS和MapReduce思想的细节，以此为基础Doug Cutting等人用了**2年业余时间**实现了HDFS和MapReduce机制，使Nutch性能飙升
- 2005年，Hadoop作为Lucene的子项目Nutch的一部分正式引入Apache基金会
- 2006年3月份，Map-Reduce和Nutch Distributed File System（HDFS）分别被纳入到Hadoop项目中，Hadoop就此正式诞生，标志着大数据时代来临
- 名字来源于Doug Cutting儿子的玩具大象



## 3、Hadoop的三大发行版本

分别是：Apache、Cloudera、Hortonworks

- Apache版本最原始，对于入门学习最好——2006
- Cloudera内部集成了很多大数据框架，对应产品CDH——2008
- Hortonworks文档较好，对应产品HDP——2001年。现在已经被Cloudera公司收购，推出新的品牌CDP


## 4、Hadoop的优势

- **高可靠性**：Hadoop底层维护多个数据副本，所以即使Hadoop某个计算元素或存储出现故障，也不会导致数据的丢失

- **高扩展性**：在集群间分配任务数据，可方便的扩展数以千计的节点，动态添加，动态删除

- **高效性**：在MapReduce的思想下，Hadoop是并行工作的，以加快任务处理速度
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133027325.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

- **高容错性**：能够自动将失败的任务重新分配
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040213303648.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
## 5、Hadoop的组成
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133147552.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
### 1）HDFS架构概述

Hadoop Distributed File System，简称HDFS，是一个分布式文件系统

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133239812.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- **NameNode（nn）**：存储文件的元数据，如文件名，文件目录结构，文件属性（生成时间、副本数、文件权限），以及每个文件的块列表和块所在的DataNode等
- **DataNode（dn）**：在本地文件系统存储文件块数据，以及块数据的校验和
- **Secondary NameNode（2nn）**：每隔一段时间对NameNode元数据备份，秘书


### 2）YARN架构概述

Yet Another Resource Negotiator简称YARN，另一种资源协调者，是Hadoop的**资源管理器**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133433762.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
### 3）MapReduce架构概述

MapReduce将计算过程分为两个阶段：**Map和Reduce**

- Map阶段并行处理输入数据
- Reduce阶段对Map结果进行汇总
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133557554.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

### 4）HDFS、YARN、MapReduce三者关系

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133617630.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
# 三、大数据技术生态体系
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133645316.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
# 四、推荐系统框架图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210402133701975.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

