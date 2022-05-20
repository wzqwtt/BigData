

## 一、explain 执行计划

面对大型数据集，通常写一个SQL可能会执行好几天，这太慢了，所以我们就要进行调优，让这个SQL尽可能地跑的时间更短。SQL是基于MapReduce的，使用下面的这个命令可以看到一个SQL的所有阶段（执行计划）

```sql
explain [extended] SQL语句;
```

这个`extended`表示详细展示执行计划，看几个案例：



查看下面这个任务有没有生成MR任务：

```sql
explain select * from emp;
```

![](./img/微信截图_20220322201334.png)



有MR任务的：

```sql
explain select count(*) from emp;
```

![](./img/微信截图_20220322201938.png)



执行计划可以帮我们清晰的看到一个SQL有几个阶段，有多少个MapReduce任务，这可以帮助我们了解SQL是如何执行的



查看详细的执行计划：

```sql
explain extended select count(*) from emp;
```





## 二、Fetch抓取

Fetch抓取是指：**Hive中对某些情况的查询可以不必使用MapReduce计算**，比如：`select * from emp`，在这种情况下Hive可以轻松读取这部分的内容

那么是哪个部分的配置决定Fetch的抓取呢？我们可以打开`hive-default.xml.template`文件找一下：

```xml
<property>
    <name>hive.fetch.task.conversion</name>
    <value>more</value>
    <description>
        Expects one of [none, minimal, more].
        Some select queries can be converted to single FETCH task minimizing latency.
        Currently the query should be single sourced not having any subquery and should not have
        any aggregations or distincts (which incurs RS), lateral views and joins.
        0. none : disable hive.fetch.task.conversion
        1. minimal : SELECT STAR, FILTER on partition columns, LIMIT only
        2. more    : SELECT, FILTER, LIMIT only (support TABLESAMPLE and virtual columns)
    </description>
</property>
```

他的默认值是`more`，还有其他两个参数：

- `none`：指禁用抓取，那么所有的任务都会跑MR
- `minimal`：在执行`*`、`对列分区`、`limit`的时候不跑MR
- `more`：在全局查找、字段查找、limit 查找等都不走 mapreduce



可以在`Hive`窗口下通过使用`set`切换值：

```sql
set hive.fetch.task.conversion=none;
```



## 三、本地模式

Hadoop处理MapReduce任务的时候首先要交给Yarn，Yarn再去分配介个任务给哪台主机，这一部分就造成了额外的时间开销。

所以在**小型数据集**的情况下，我们可以开启本地模式，这样执行时间就会大幅减少，用户可以通过设置`hive.exec.mode.local.auto`为`true`来开启**自动本地模式**，下面这段xml是原始配置

```xml
<property>
    <name>hive.exec.mode.local.auto</name>
    <value>false</value>
    <description>Let Hive determine whether to run in local mode automatically</description>
</property>
```

我们可以在hive窗口通过使用set切换他的值：

```sql
set hive.exec.mode.local.auto=true;
```

执行一段走MR的SQL对比一下时间：

```sql
select count(*) from emp;
```

开启前：33.632s；开启后：7.93s



## 四、表的优化



### 1、小表大表Join（MapJOIN）

在两个表join的时候，一般把小表放在左边，大表放在右边。可以使用MapJOIN让小维度的表放进内存，然后慢慢加载大表数据在Map阶段就完成join。



下图为`MapJOIN`的过程：

![](./img/微信截图_20220322215601.png)

在Map阶段，首先把小表全都读入内存中，然后扫描大表，大表根据每一条记录去内存中和小表的字段完成匹配，在Map端完成JOIN，MapJOIN没有Reduce阶段，所以Map直接输出结果



> 在Hive中已经做了优化，大表小表放在左边右边都是一样的，只要开启MapJOIN都会先搞小表再弄大表



Hive开启`MapJOIN`的配置是：

```xml
<property>
    <name>hive.auto.convert.join</name>
    <value>true</value>
    <description>Whether Hive enables the optimization about converting common join into mapjoin based on the input file size</description>
</property>
```

`MapJOIN`初始化就是开启的，如果想手动开启可以：

```bash
set hive.auto.convert.join = true;
```



还有一个就是Hive对小表识别的标准，在Hive中可以设置一个小表的阈值，默认25M以下就是小表：

```xml
<property>
    <name>hive.mapjoin.smalltable.filesize</name>
    <value>25000000</value>
    <description>
        The threshold for the input file size of the small tables; if the file size is smaller 
        than this threshold, it will try to convert the common join into map join
    </description>
</property>
```

如果想自己设置阈值，可以在Hive窗口：

```bash
set hive.mapjoin.smalltable.filesize = 阈值（单位字节）;
```



**做一个案例，看一下Hive大表Join小表和小表join大表的效率：**

数据来自尚硅谷，其中大表100万条、小表10万条，读者如果需要可以到[尚硅谷官网](http://www.atguigu.com/opensource.shtml#bigdata)下载使用，将数据上传到服务器然后建表，load数据到HDFS：

```bash
[wzq@hadoop102 datas]$ ll | grep table
-rw-r--r--. 1 wzq wzq 129157332 11月 23 2020 bigtable
-rw-r--r--. 1 wzq wzq  13015084 11月 23 2020 smalltable
...

# 创建大表
hive (db_hive)> create table bigtable(id bigint, t bigint, uid string, keyword string, url_rank int, click_num int, click_url string) row format delimited fields terminated by '\t';

# 创建小表
hive (db_hive)> create table smalltable(id bigint, t bigint, uid string, keyword string, url_rank int, click_num int, click_url string) row format delimited fields terminated by '\t';

# join之后的表
hive (db_hive)> create table jointable(id bigint, t bigint, uid string, keyword string, url_rank int, click_num int, click_url string) row format delimited fields terminated by '\t';

# 大表小表导入数据
hive (db_hive)> load data local inpath '/opt/module/datas/bigtable' into table bigtable;
hive (db_hive)> load data local inpath '/opt/module/datas/smalltable' into table smalltable;
```



**小表JOIN大表：**

```sql
insert overwrite table jointable
select
    b.id, b.t, b.uid, b.keyword, b.url_rank, b.click_num, b.click_url
from smalltable s
join bigtable b
on s.id = b.id;
```

看一下他的执行计划，这里我挑出关键的地方：

```
STAGE DEPENDENCIES:
  # 五个执行阶段
  Stage-4 is a root stage , consists of Stage-5, Stage-1  # 从4开始执行
  Stage-5 has a backup stage: Stage-1  # 阶段5是阶段1的备份
  Stage-3 depends on stages: Stage-5   # 阶段3依赖于阶段5
  Stage-1
  Stage-0 depends on stages: Stage-3, Stage-1  # 阶段0依赖于3和1

STAGE PLANS:
  Stage: Stage-4
    Conditional Operator
  
  # 省略了两个阶段

  Stage: Stage-1
    Map Reduce
      # Map
      Map Operator Tree:
          TableScan
            alias: s       # 扫描小表
            Filter Operator  # 过滤掉id为null的字段
              predicate: id is not null (type: boolean)

          TableScan
            alias: b       # 扫描大表
            Filter Operator  # 过掉掉id为null的字段
              predicate: id is not null (type: boolean)

      Reduce Operator Tree:
        Join Operator
          condition map:
               Inner Join 0 to 1  # 执行join

  Stage: Stage-0
    Fetch Operator    # 最后抓取
      limit: -1
      Processor Tree:
        ListSink
```



**大表JOIN小表：**

```sql
insert overwrite table jointable
select
    b.id, b.t, b.uid, b.keyword, b.url_rank, b.click_num, b.click_url
from bigtable b
join smalltable s
on s.id = b.id;
```

这个阶段读者可以自己看一下，他的执行计划和小表JOIN大表是一样的



### 2、大表Join小表

#### 2.1 空key过滤

空值过滤适用于：

- 不是`inner join`
- 用`left join`不希望左边有null值出现



创建一个含有空id的表，并且load：

```bash
# 创建数据库
hive (db_hive)> create table nullidtable(id bigint, t bigint, uid string, keyword string, url_rank int, click_num int, click_url string) row format delimited fields terminated by '\t';

# load
hive (db_hive)> load data local inpath '/opt/module/datas/nullid' into table nullidtable;
```



**测试不过滤空id：**

```sql
insert overwrite table jointable
select n.* 
from nullidtable n
left join bigtable b
on n.id = b.id;
```

这个共运行：`127.684`秒

**测试过滤空id：**

```sql
insert overwrite table jointable
select n.*
from (select * from nullidtable where id is not null) n
left join bigtable b
on n.id = b.id;
```

这个共运行：`81.397`秒



#### 2.2 空key转换

有时虽然某个字段的为空的数据有很多，但相应的数据不是异常数据，必须要包含在join的结果中，此时就可以为为空的字段赋上一个随机值，使得数据均匀的分布到不同的reducer上



**demo：不随机分布**

设置5个`reduce`个数：

```bash
hive (db_hive)> set mapreduce.job.reduces=5;
```

`JOIN`两张表：

```sql
insert overwrite table jointable
select n.*
from nullidtable n
left join bigtable b
on n.id=b.id;
```

![](./img/微信截图_20220322230143.png)

每个reducer执行时间的长短不一样，出现了严重的数据倾斜，**在开发中，我们要尽最大努力避免数据倾斜！**为null值赋一个随机值可以有效的避免数据倾斜



**demo：随机分布，当有null的数据的时候就附上一个随机值**

```sql
insert overwrite table jointable
select n.*
from nullidtable n
left join bigtable b
on nvl(n.id,rand())=b.id;
```

![](./img/微信截图_20220322231330.png )

赋上一个随机值后每个reducer执行时间大体相同，避免了数据倾斜



#### 2.3 分桶表SMB:star:

当两张表规模差不多的时候，如果两张表直接join可能时间会贼长，网上有个帖子对比了使用分桶表join和直接join的时间差，直接join执行时间在2个小时左右，但是用了分桶表执行时间变成了10分钟，大幅度的降低了执行时间



那咋用分桶表？**我们可以把两张表切割一个一个段，然后两个段之间执行join，最后再合并**，如下图所示：

![](./img/微信截图_20220322231249.png)

> 那么这时候就有一个问题：**两张表每段之间执行join，那么有其他id在其他段怎么办？**
>
> 这时候`分桶表`就派上了用场，两张表我们分的桶一样多，而分桶表是根据指定的字段进行哈希，然后再把每个字段各自丢到自己的桶中，那么如果指定相同的桶数，相同的id就必然会被分到一个桶里面！



**案例：对比不使用分桶和使用分桶之间的时间**

> Note：这里Maper和Reducer的个数都为1，才能得到下面的结果！

**不使用分桶**

创建第二张大表，把之前的大表copy一个相同的表出来：

```bash
hive (db_hive)> create table bigtable2(id bigint,
t bigint, uid string,keyword string, url_rank int, click_num int, click_url string)
row format delimited fields terminated by '\t';
hive (db_hive)> load data local inpath '/opt/module/datas/bigtable' into table bigtable2;
```

两个大表之间`join`：

```sql
insert overwrite table jointable
select b1.*
from bigtable b1
join bigtable b2
on b1.id = b2.id;
```

运行时间：`346.158`秒



**使用分桶：**

创建两个分桶表（`桶的个数不要超过可用CPU的核数，这里我设置3`）：

```sql
# 创建第一个分桶表
create table bigtable_buck1(
id bigint,t bigint, uid string,keyword string, url_rank int, click_num int, click_url string)
clustered by(id) sorted by(id) into 3 buckets
row format delimited fields terminated by '\t';

# 创建第二个分桶表
create table bigtable_buck2(
id bigint,t bigint, uid string,keyword string, url_rank int, click_num int, click_url string)
clustered by(id) sorted by(id) into 3 buckets
row format delimited fields terminated by '\t';

# 向两张表load数据
load data local inpath '/opt/module/datas/bigtable' into table bigtable_buck1;
load data local inpath '/opt/module/datas/bigtable' into table bigtable_buck2;
```

> 这里load data报错不要紧！只需要再执行一次就好啦！



使用SMB我们需要开启几个参数：

```bash
hive (db_hive)> set hive.optimize.bucketmapjoin = true;
hive (db_hive)> set hive.optimize.bucketmapjoin.sortedmerge = true; 
hive (db_hive)> set hive.input.format=org.apache.hadoop.hive.ql.io.BucketizedHiveInputFormat;
```



然后我们开始`join`：

```sql
insert overwrite table jointable
select bk1.*
from bigtable_buck1 bk1
join bigtable_buck1 bk2
on bk1.id=bk2.id;
```

运行时间：`205.15`秒



### 3、Group By

默认情况下，Map阶段同一Key数据分发给一个reduce，当一个key数据过大的时候就产生了数据倾斜。



**并不是所有的聚合操作都需要在Reduce端完成，很多聚合操作都可以先在Map端进行部分聚合，最后在Reduce端得出最终结果**

**开启Map端聚合参数设置：**

```bash
# 是否在Map端进行聚合，默认为true
set hive.map.aggr = true;

# 在Map端进行聚合操作的条目数目
set hive.groupby.mapaggr.checkinterval = 100000;

# 有数据倾斜的时候进行负载均衡，默认是false
set hive.groupby.skewindata = true;
```



### 4、Count(distinct) 去重统计

数据量小的时候无所谓，数据量大的情况下，由于 COUNT DISTINCT 操作需要用一个 Reduce Task 来完成，这一个 Reduce 需要处理的数据量太大，就会导致整个 Job 很难完成， 一般 COUNT DISTINCT 使用先 GROUP BY 再 COUNT 的方式替换,但是需要注意 group by 造成 的数据倾斜问题



### 5、尽量避免笛卡尔积

笛卡尔积对两张表的进行了所有字段的组合，数据量会变的非常大，所以要尽量避免笛卡尔积





### 6、行列过滤



列处理：在select中，只拿需要的列，如果有分区尽量使用分区过滤，少用`select *`

行处理：在分区裁剪中，当使用外关联时，如果将副表的过滤条件写在where后面，那么就会先全表关联，之后再过滤



## 五、分区与分桶

具体见博客：[Hive分区与分桶](http://wzqwtt.club/2022/03/19/hive-fen-qu-biao-yu-fen-tong-biao/)



## 六、合理设置Map及Reduce数

### 6.1 复杂文件增加Map数

当 input 的文件都很大，任务逻辑复杂，map 执行非常慢的时候，可以考虑增加 Map 数， 来使得每个 map 处理的数据量减少，从而提高任务的执行效率。

增加 map 的方法为：根据

`computeSliteSize(Math.max(minSize,Math.min(maxSize,blocksize)))=blocksize=128M` 公式， 调整 maxSize 最大值。让 maxSize 最大值低于 blocksize 就可以增加 map 的个数。

### 6.2 小文件合并

在 map 执行前合并小文件，减少 map 数：CombineHiveInputFormat 具有对小文件进行合 并的功能（系统默认的格式）。

HiveInputFormat 没有对小文件合并功能。

```bash
set hive.input.format= org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
```



 Map-Reduce 的任务结束时合并小文件的设置： 

```bash
# 在 map-only 任务结束时合并小文件，默认 true
SET hive.merge.mapfiles = true;

# 在 map-reduce 任务结束时合并小文件，默认 false
SET hive.merge.mapredfiles = true; 

# 合并文件的大小，默认 256M
SET hive.merge.size.per.task = 268435456;

# 当输出文件的平均大小小于该值时，启动一个独立的 map-reduce 任务进行文件 merge 
SET hive.merge.smallfiles.avgsize = 16777216;
```





### 6.3 合理设置Reduce数

调整Reduce个数方法一：

```bash
# 每个 Reduce 处理的数据量默认是 256MB
set hive.exec.reducers.bytes.per.reducer=256000000;

# 每个任务最大的 reduce 数，默认为 1009
set hive.exec.reducers.max=1009;

# 计算 reducer 数的公式
N=min(参数 2，总输入数据量/参数 1)
```

调整Reduce个数方法二：

```bash
# 设置每个 job 的 Reduce 个数
set mapreduce.job.reduces = 15;
```



## 七、并行执行

Hive 会将一个查询转化成一个或者多个阶段。这样的阶段可以是 MapReduce 阶段、抽 样阶段、合并阶段、limit 阶段。或者 Hive 执行过程中可能需要的其他阶段。默认情况下， Hive 一次只会执行一个阶段。不过，某个特定的 job 可能包含众多的阶段，而这些阶段可能 并非完全互相依赖的，也就是说有些阶段是可以并行执行的，这样可能使得整个 job 的执行 时间缩短。不过，如果有更多的阶段可以并行执行，那么 job 可能就越快完成。



过设置参数 hive.exec.parallel 值为 true，就可以开启并发执行。不过，在共享集群中， 需要注意下，如果 job 中并行阶段增多，那么集群利用率就会增加。



```bash
set hive.exec.parallel=true;             //打开任务并行执行

set hive.exec.parallel.thread.number=16; //同一个 sql 允许最大并行度，默认为8。
```



## 八、严格模式

Hive 可以通过设置防止一些危险操作



### 8.1 分区表不使用分区过滤

将 hive.strict.checks.no.partition.filter 设置为 true 时，对于分区表，除非 where 语句中含 有分区字段过滤条件来限制范围，否则不允许执行。





## 8.2 使用 order by 没有 limit 过滤

将 hive.strict.checks.orderby.no.limit 设置为 true 时，对于使用了 order by 语句的查询，要 求必须使用 limit 语句。因为 order by 为了执行排序过程会将所有的结果数据分发到同一个 Reducer 中进行处理，强制要求用户增加这个 LIMIT 语句可以防止 Reducer 额外执行很长一 段时间



### 8.3 笛卡尔积

将 hive.strict.checks.cartesian.product 设置为 true 时，会限制笛卡尔积的查询。对关系型数 据库非常了解的用户可能期望在 执行 JOIN 查询的时候不使用 ON 语句而是使用 where 语 句，这样关系数据库的执行优化器就可以高效地将 WHERE 语句转化成那个 ON 语句。不幸 的是，Hive 并不会执行这种优化，因此，如果表足够大，那么这个查询就会出现不可控的情 况。

## 参考资料

- 尚硅谷Hive学习视频
- [Apache Hive官网](https://hive.apache.org/)
- https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Select
- https://cwiki.apache.org/confluence/display/Hive/HivePlugins
