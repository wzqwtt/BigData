本节开始HBase Shell的简单操作，进入`HBase Shell`很简单，只需要写入以下命令就可以了（当然要保证HBase是启动的）：

```bash
[wzq@hadoop102 ~]$ hbase shell
```



# 一、Help

Help！Help！:fearful: ！不会使用HBase Shell命令的时候一定要学会使用`help`指令，在窗口中直接输入help指令：

```bash
hbase:002:0> help
HBase Shell, version 2.4.11, r7e672a0da0586e6b7449310815182695bc6ae193, Tue Mar 15 10:31:00 PDT 2022
Type 'help "COMMAND"', (e.g. 'help "get"' -- the quotes are necessary) for help on a specific command.
Commands are grouped. Type 'help "COMMAND_GROUP"', (e.g. 'help "general"') for help on a command group.

COMMAND GROUPS:
  Group name: general
  Commands: processlist, status, table_help, version, whoami

  Group name: ddl
  Commands: alter, alter_async, alter_status, clone_table_schema, create, describe, disable, disable_all, drop, drop_all, enable, enable_all, exists, get_table, is_disabled, is_enabled, list, list_regions, locate_region, show_filters

  Group name: namespace
  Commands: alter_namespace, create_namespace, describe_namespace, drop_namespace, list_namespace, list_namespace_tables

  Group name: dml
  Commands: append, count, delete, deleteall, get, get_counter, get_splits, incr, put, scan, truncate, truncate_preserve

...
此处有省略
...
```



发送`help`命令之后，会显示出`HBase`的所有可用命令，每个命令都有对应的Group，一般我们操作HBase都是在Java里面写代码的，所以显得HBase Shell命令就不那么重要了，所以这次只学习一些简单的常用命令



遇到命令不会用，可以直接打`help '<命令>'`，会列出这个命令的示例用法，比如：

```bash
hbase:006:0> help 'list'
List all user tables in hbase. Optional regular expression parameter could
be used to filter the output. Examples:

  hbase> list
  hbase> list 'abc.*'
  hbase> list 'ns:abc.*'
  hbase> list 'ns:.*'
```

显示`list`命令的用法，list命令可以列出所有用户在hbase中的表，也可以加一些正则表达式来过滤输出

有了这个例子就不怕有什么命令不会使用啦！:wink:



# 二、namespace

`namespace`就相当于`MySQL`里面的数据库了，在[关于HBase你应该知道这些！](./1、关于HBase你应该知道这些.md)里面对namespace有介绍



关于`namespace`这一块有以下几个命令：

```bash
hbase:002:0> help
...
  Group name: namespace
  Commands: alter_namespace, create_namespace, describe_namespace, drop_namespace, list_namespace, list_namespace_tables
...  
```

其中，创建namespace的命令是：`create_namespace`，不会用啊，用一下`help`：

```bash
hbase:007:0> help 'create_namespace'
Create namespace; pass namespace name,
and optionally a dictionary of namespace configuration.
Examples:

  hbase> create_namespace 'ns1'
  hbase> create_namespace 'ns1', {'PROPERTY_NAME'=>'PROPERTY_VALUE'}
```

这个命令的意思就是创建一个命名空间，也可以以字典的形式为这个命名空间增加一些额外的配置，比如，创建一个`bigdata`命名空间：

```bash
hbase:008:0> create_namespace 'bigdata'
```



查看所有的命名空间使用：（`list_namespace`）

```bash
hbase:009:0> list_namespace
NAMESPACE                                                                                  bigdata                                                                                    default                                                                                   hbase                                                                                     
3 row(s)
Took 0.0443 seconds
```



也可以查看某个命名空间的详细信息：（`describe_namespace`）

```bash
hbase:002:0> describe_namespace 'bigdata'
DESCRIPTION                                                                               
{NAME => 'bigdata'}                                                                       
Quota is disabled
Took 1.2531 seconds
```



还有其他的一些命令，读者可以试着使用`help`命令查看如何使用，自己把玩一下

# 三、DDL

学到这里的读者想必对DDL和DML都不陌生了，其中DDL就是数据定义语言，DML就是数据查询语言

DDL主要做的就是对表的一些操作，而DML主要做的是对表里面数据的查询等操作



使用`help`命令可以列出关于DDL所有的命令：

```bash
hbase:002:0> help
...
  Group name: ddl
  Commands: alter, alter_async, alter_status, clone_table_schema, create, describe, disable, disable_all, drop, drop_all, enable, enable_all, exists, get_table, is_disabled, is_enabled, list, list_regions, locate_region, show_filters
...
```



## 1、创建表

创建表的命令是`create`，help一下看这个命令怎么使用：

```bash
hbase:007:0> help 'create'
# 创建一个表，传递一个表名和一组列族（至少一个），以及可选的一些表格的配置
Creates a table. Pass a table name, and a set of column family
specifications (at least one), and, optionally, table configuration.
Column specification can be a simple string (name), or a dictionary
(dictionaries are described below in main help output), necessarily
including NAME attribute.
Examples:

# 在命名空间ns1中创建一个表t1，
Create a table with namespace=ns1 and table qualifier=t1
  # 在不是default命名空间中创建表需要加上命名空间的名字，{NAME=>'列族名字',配置信息}
  hbase> create 'ns1:t1', {NAME => 'f1', VERSIONS => 5}
  # 这个VERSIONS配置代表的是版本，每条信息最多维护几个保存

# 在default命名空间中创建表t1，可以不带namespace名
Create a table with namespace=default and table qualifier=t1
  hbase> create 't1', {NAME => 'f1'}, {NAME => 'f2'}, {NAME => 'f3'}
  hbase> # The above in shorthand would be the following: 可以简写为以下形式
  hbase> create 't1', 'f1', 'f2', 'f3'
  hbase> create 't1', {NAME => 'f1', VERSIONS => 1, TTL => 2592000, BLOCKCACHE => true}
  hbase> create 't1', {NAME => 'f1', CONFIGURATION => {'hbase.hstore.blockingStoreFiles' => '10'}}
  hbase> create 't1', {NAME => 'f1', IS_MOB => true, MOB_THRESHOLD => 1000000, MOB_COMPACT_PARTITION_POLICY => 'weekly'}

# 表的配置信息可以放在最后面
Table configuration options can be put at the end.
Examples:

  hbase> create 'ns1:t1', 'f1', SPLITS => ['10', '20', '30', '40']
  hbase> create 't1', 'f1', SPLITS => ['10', '20', '30', '40']
  hbase> create 't1', 'f1', SPLITS_FILE => 'splits.txt', OWNER => 'johndoe'
  hbase> create 't1', {NAME => 'f1', VERSIONS => 5}, METADATA => { 'mykey' => 'myvalue' }
  hbase> # Optionally pre-split the table into NUMREGIONS, using
  hbase> # SPLITALGO ("HexStringSplit", "UniformSplit" or classname)
  hbase> create 't1', 'f1', {NUMREGIONS => 15, SPLITALGO => 'HexStringSplit'}
  hbase> create 't1', 'f1', {NUMREGIONS => 15, SPLITALGO => 'HexStringSplit', REGION_REPLICATION => 2, CONFIGURATION => {'hbase.hregion.scan.loadColumnFamiliesOnDemand' => 'true'}}
  hbase> create 't1', 'f1', {SPLIT_ENABLED => false, MERGE_ENABLED => false}
  hbase> create 't1', {NAME => 'f1', DFS_REPLICATION => 1}

You can also keep around a reference to the created table:

  hbase> t1 = create 't1', 'f1'

Which gives you a reference to the table named 't1', on which you can then
call methods
```

哇！这么多内容，看来这个命令比较重要！我也在注释中描述了这个命令怎么使用，大家可以看看上面的注释

可以在bigdata命名空间中创建一个表格`student`，他有info和msg两个列族，其中info列族维护的版本数是5个，不写列族版本默认维护1个版本：

```bash
hbase:008:0> create 'bigdata:student',{NAME=>'info',VERSIONS=>5},{NAME=>'msg'}
```



## 2、查看表

查看表可以使用`list`或者`descirbe`命令



- `list`：查看所有表名：

  ```bash
  hbase:010:0> list
  TABLE                                                                                 bigdata:student                                                                       
  1 row(s)
  Took 0.0125 seconds                                                                   
  ```

- `descirbe`：查看表的详细信息（列出了列族的详细信息）

  ```bash
  hbase:011:0> describe 'bigdata:student'
  Table bigdata:student is ENABLED                                                             
  bigdata:student                                                                              
  COLUMN FAMILIES DESCRIPTION                                                                  
  {NAME => 'info', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '5', KEEP_DELETED_CE
  LLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION => 'NONE', TTL => 'FOREVER', MIN_V
  ERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}        
  
  {NAME => 'msg', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '1', KEEP_DELETED_CEL
  LS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION => 'NONE', TTL => 'FOREVER', MIN_VE
  RSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}         
  
  2 row(s)
  Quota is disabled
  Took 0.1689 seconds
  ```

  

当然我们也可以在web页面看到这个表：

![image-20220626201608375](img/image-20220626201608375.png)



## 3、修改表

表创建时写的所有和列族相关的信息，后续都可以通过`alter`来修改，包括增加和删除列族



- 增加列族和修改信息都使用覆盖的方法：

  ```bash
  hbase:012:0> alter 'bigdata:student',{NAME=>'f1',VERSIONS=>3}
  ```

- 删除信息使用的特殊的语法：

  ```bash
  hbase:015:0> alter 'bigdata:student','delete'=>'f1'
  ```

  

## 4、删除表

删除表使用`drop`命令：

```bash
hbase:016:0> drop 'bigdata:student'

ERROR: Table bigdata:student is enabled. Disable it first.

For usage try 'help "drop"'

Took 0.0210 seconds
```

但是当咱们删除表的时候却发现报错了，这是因为我们要删的这个`student`表目前是`可用`状态，**所以删除一个表需要事先把该表禁用再删除**



所以HBase删除表有两个步骤：

```bash
# 先禁用
hbase:017:0> disable 'bigdata:student'
# 再删除
hbase:018:0> drop 'bigdata:student'
```





# 四、DML

关于DML的命令行有这些：

```bash
hbase:002:0> help
...
  Group name: dml
  Commands: append, count, delete, deleteall, get, get_counter, get_splits, incr, put, scan, truncate, truncate_preserve
...
```



在开始之前，先把咱们刚刚删除的表创建出来：

```bash
hbase:020:0> create 'bigdata:student',{NAME=>'info',VERSIONS=>5},{NAME=>'msg'}
```



## 1、写入数据

在HBase中写入数据，只能添加结构中最底层的cell：

```bash
# put 表名,row key,列族:列,值
put 'bigdata:student','1001','info:name','zhangsan'
put 'bigdata:student','1001','info:name','lisi'
put 'bigdata:student','1001','info:age','18'
put 'bigdata:student','1002','info:name','wtt'
put 'bigdata:student','1003','info:name','wzq'
```

如果重复写入相同的rowKey，相同列的数据，会写入多个版本进行覆盖



## 2、读取数据



读取数据有两个方法：get和scan

- `get`：最大范围是一行数据，也可以进行列的过滤，读取数据的结果为多行cell

  ```bash
  hbase:015:0> get 'bigdata:student','1001'
  COLUMN                   CELL                                                                
   info:age                timestamp=2022-06-26T20:31:34.131, value=18                         
   info:name               timestamp=2022-06-26T20:31:34.052, value=lisi                       
  1 row(s)
  Took 0.1419 seconds                                                                          
  hbase:016:0> get 'bigdata:student','1001',{COLUMN=>'info:name',VERSIONS=>2}
  COLUMN                   CELL                                                                
   info:name               timestamp=2022-06-26T20:31:34.052, value=lisi                       
   info:name               timestamp=2022-06-26T20:31:33.927, value=zhangsan                   
  1 row(s)
  Took 0.0160 seconds
  ```

- `scan`是扫描数据，能够读取多行数据，推荐使用`startRow`和`stopRow`来控制读取的数据，默认范围是左闭右开：

  ```bash
  hbase:017:0> scan 'bigdata:student',{STARTROW=>'1001',STOPROW=>'1003'}
  ROW                      COLUMN+CELL                                                         
   1001                    column=info:age, timestamp=2022-06-26T20:31:34.131, value=18        
   1001                    column=info:name, timestamp=2022-06-26T20:31:34.052, value=lisi     
   1002                    column=info:name, timestamp=2022-06-26T20:31:34.214, value=wtt      
  2 row(s)
  Took 0.0499 seconds
  ```



## 3、删除数据

删除数据的方法有两个：`delete`和`deleteall`

- `delete`：删除一个版本的数据，即为1个cell，不填写版本默认删除最新的一个版本

  ```bash
  hbase:019:0> delete 'bigdata:student','1001','info:name'
  ```

- `deleteall`：删除所有版本的数据，即为当前行当前列的多个cell

  ```bash
  hbase:020:0> deleteall 'bigdata:student','1001','info:name'
  ```


> HBase Shell在实际开发中很难用到，所以只做了简单的介绍，有不会命令可以使用`help`命令查询怎么使用！关于HBase的操作，将在API操作中详解！

# 参考资料

- [尚硅谷 HBase 2.x 学习视频](https://www.bilibili.com/video/BV1PZ4y1i7gZ)
- [HBase 官方文档](https://hbase.apache.org/2.3/book.html)