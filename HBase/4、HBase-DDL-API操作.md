:laughing:欢迎大家来看我的Blog，本篇Blog主要写了如何使用HBase Java API建立连接、操作Namespace和Table，就是DDL啦！[所有的code都可以点击我看到！](https://github.com/wzqwtt/BigData/tree/master/HBase/HBaseCode/src/main/java/com/wzq)下面开始吧！

首先需要在服务器上动HBase：（这就相当于HBase的服务端）

```bash
# 启动hadoop
[wzq@hadoop102 ~]$ myhadoop.sh start
# 启动zookeeper
[wzq@hadoop102 ~]$ zk.sh start
# 启动hbase
[wzq@hadoop102 ~]$ start-hbase.sh
```



code中每一行都有注释，所以这里就只做一些简单的介绍好啦！（有时间一定补回来！）



# 一、获取HBase连接

[获取连接的code点我直达](https://github.com/wzqwtt/BigData/blob/ac6893a683c0f2cbcfc0e2c0ceb12f50399678f2/HBase/HBaseCode/src/main/java/com/wzq/HBaseConnection.java)

因为HBase Connection比较重，所以官方也是建议我们使用单例的模式来创建Connection：

```java
public static Connection connection = null;
```

这里首先声明了一个连接静态的静态数据，在类加载的时候就获取HBase连接，关于HBase的配置可以直接读取[hbase-site.xml](https://github.com/wzqwtt/BigData/blob/ac6893a683c0f2cbcfc0e2c0ceb12f50399678f2/HBase/HBaseCode/src/main/resources/hbase-site.xml)文件：

```java
static {
    // 1、创建HBase连接，直接读取hbase-site.xml文件，
    // 默认使用同步连接
    try {
        // 创建连接会抛出异常，这里直接catch一下
        connection = ConnectionFactory.createConnection();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

> 在hbase-site.xml中只写了zookeeper的连接，这是因为HBase是通过zookeeper实现分布式的！



当然咱们也需要一个关闭连接的方法了：

```java
// 关闭连接
public static void closeConnection() throws IOException {
    // 判断连接是否为空
    if (connection != null) {
        connection.close();
    }
}
```

所以，以后使用的时候直接调用这个类的`connection`属性就获取到连接了

需要说明的是这个类里面有两个非常重要的属性，后面的DDL和DML都是通过这两个属性实现操作的：

- `Admin`：DDL，负责namespace、table的增加、删除、修改
- `Table`：DML，负责table里面数据的增删改查

# 二、Namespace

HBase中的namespace就相当于MySQL中的数据库，一个namespace里面可以有很多table，[这个部分的code在这里](https://github.com/wzqwtt/BigData/blob/ac6893a683c0f2cbcfc0e2c0ceb12f50399678f2/HBase/HBaseCode/src/main/java/com/wzq/HBaseDDLNamespace.java)

这个类首先有一个静态属性Connection，代表HBase的连接：

```java
public static Connection connection = HBaseConnection.connection;
```



操作Namespace和Table有一个三步走战略：

- 通过connection获取Admin连接
- 操作namespace或table
- 释放Admin连接

下面所有的code都遵循这个三步走战略



## 1、获取所有可用的命名空间

```java
public static String[] listNamespace() throws IOException {
    // 1、获取连接，因为admin是轻量级的，建议即用即创建即释放
    Admin admin = connection.getAdmin();

    // 2、获取所有可用的命名空间
    String[] namespaces = null;
    try {
        namespaces = admin.listNamespaces();
    } catch (IOException e) {
        System.out.println("Something wrong... I can feel it");
        e.printStackTrace();
    }

    // 3、关闭连接
    admin.close();
    return namespaces;
}
```



## 2、创建命名空间

```java
public static void createNamespace(String namespace) throws IOException {
    // 1、获取Admin
    // 此处异常先不抛出，等待方法写完统一处理
    // Admin的连接是轻量级的，并且不是线程安全的，不推荐池化或者缓存这个连接
    // 用到了就获取，用不到就不管
    Admin admin = connection.getAdmin();

    // 2、调用方法，创建命名空间
    // 代码相对Shell更加底层，Shell能够实现的功能，代码一定能实现
    // 所以需要填写完整的命名空间描述

    // 2.1 创建命名空间描述建造者
    NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(namespace);

    // 2.2 给命名空间添加需求
    builder.addConfiguration("user", "wzq");

    // 2.3 使用Builder构造出对应的添加完参数的对象，完成创建
    // 创建命名空间出现的问题，都属于本方法自身的问题，不应该抛出
    try {
        admin.createNamespace(builder.build());
    } catch (IOException e) {
        e.printStackTrace();    // 打印栈追踪
        System.out.println("命名空间" + namespace + "已经存在");
    }

    // 3、关闭Admin
    admin.close();
}
```

这里涉及一个设计模式：**建造者模式**，因为创建命名空间调用的`createNamespace`，他需要传递一个参数`NamespaceDescriptor`命名空间描述，由于这个类的构造方法是私有的，所以需要使用HBase提供的建造者构建出来，再添加需求，最后放入参数里面。



## 3、删除指定的命名空间

```java
public static void deleteNamespace(String namespace) throws IOException {
    // 1、获取Admin
    Admin admin = connection.getAdmin();

    // 2、删除指定的namespace，异常不抛，在这里处理
    try {
        admin.deleteNamespace(namespace);
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("命名空间" + namespace + "不存在");
    }

    // 3、关闭Admin
    admin.close();
}
```



## 4、修改某个命名空间



```java
public static void modifyNamespace(String namespace) throws IOException {
    // 1、获取Admin
    Admin admin = connection.getAdmin();

    // 2、修改namespace
    // 2.1 创建NamespaceDescriptor的Builder
    NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(namespace);

    // 2.2 增加配置
    builder.addConfiguration("user", "wtt");

    // 2.3 修改namespace
    try {
        admin.modifyNamespace(builder.build());
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("命名空间" + namespace + "不存在");
    }

    // 3、关闭Admin
    admin.close();
}
```



# 三、Table

Table就是表，[这部分的code可以点我直达](https://github.com/wzqwtt/BigData/blob/master/HBase/HBaseCode/src/main/java/com/wzq/HBaseDDLTable.java)

这一部分涉及大量的建造者模式…………

## 1、判断表是否存在



```java
public static boolean isTableExists(String namespace, String tableName) throws IOException {
    // 1、获取admin，admin是轻量级的，建议即用即获取即释放
    Admin admin = connection.getAdmin();

    // 2、判断table是否存在
    boolean exists = false;
    try {
        // TableName类的构造方法是私有的，于是可以使用静态方法valueOf构造一个TableName
        exists = admin.tableExists(TableName.valueOf(namespace, tableName));
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、关闭连接
    admin.close();

    // 4、返回最终结果
    return exists;
}
```

## 2、创建一个Table



```java
public static void createTable(String namespace, String tableName, String... columnFamilies) throws IOException {
    // 0.1、判断是否至少有一个列族
    if (columnFamilies.length == 0) {
        System.out.println("创建表格至少有1个列族");
        return;
    }

    // 0.2、判断是否存在
    if (isTableExists(namespace, tableName)) {
        System.out.println("表格" + namespace + ":" + tableName + "已存在");
        return;
    }

    // 1、获取Admin
    Admin admin = connection.getAdmin();

    // 2、创建表格
    // 2.1 创建TableDescriptor表格描述建造者
    TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder
        .newBuilder(TableName.valueOf(namespace, tableName));

    // 2.2 遍历所有列族
    for (String columnFamily : columnFamilies) {
        // 2.3 创建列族描述的建造者
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder
            .newBuilder(Bytes.toBytes(columnFamily));

        // 2.4 往建造者里面添加参数，这里添加一个最大版本为5
        columnFamilyDescriptorBuilder.setMaxVersions(5);

        // 2.5 往表格描述建造者添加列族
        tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());
    }

    // 2.6 创建表格
    try {
        admin.createTable(tableDescriptorBuilder.build());
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("未知错误");
    }

    // 3、释放Admin
    admin.close();
}
```



## 3、修改表格中的一个列族的信息



```java
public static void modifyTable(String namespace, String tableName, String columnFamily, int version) throws IOException {
    // 0、增加健壮性
    // 0.1 判断表格是否存在，不存在直接返回
    if (!isTableExists(namespace, tableName)) {
        System.out.println("表格" + namespace + ":" + tableName + "不存在！无法进行修改");
        return;
    }

    // 1、获取Admin实例
    Admin admin = connection.getAdmin();

    // 2、调用方法修改表格
    try {
        // 2.0 调用之前的表格描述
        TableDescriptor descriptor = admin.getDescriptor(TableName.valueOf(namespace, tableName));

        // 2.1 创建一个表格描述建造者
        // 如果使用TableName方法，相当于创建一个新的表格描述建造者，没有之前的信息
        // 如果想要修改之前表格信息，必须用方法填写一个旧的表格描述
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(descriptor);

        // 2.2 修改对应表格描述建造者的信息
        // 获取旧的列族描述
        ColumnFamilyDescriptor columnFamilyDescriptor = descriptor.getColumnFamily(Bytes.toBytes(columnFamily));
        // 创建旧的列族描述建造者
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder
            .newBuilder(columnFamilyDescriptor);
        // 修改版本号
        columnFamilyDescriptorBuilder.setMaxVersions(version);
        // 使用表格描述建造者修改列族信息
        tableDescriptorBuilder.modifyColumnFamily(columnFamilyDescriptorBuilder.build());

        // 修改表格
        admin.modifyTable(tableDescriptorBuilder.build());
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、释放Admin实例
    admin.close();
}
```



## 4、删除表格



```java
public static boolean deleteTable(String namespace, String tableName) throws IOException {
    // 0、判断表格是否存在
    if (!isTableExists(namespace, tableName)) {
        System.out.println("表格" + namespace + ":" + tableName + "不存在！无法进行删除");
        return false;
    }

    // 1、获取Admin实例
    Admin admin = connection.getAdmin();

    // 2、删除表格
    try {
        TableName table = TableName.valueOf(namespace, tableName);
        admin.disableTable(table);  // 2.1 将表格设置为不可用
        admin.deleteTable(table);   // 2.2 删除表格
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、释放Admin
    admin.close();
    // 最终返回
    return true;
}
```





# 参考资料

- [尚硅谷 HBase 2.x 学习视频](https://www.bilibili.com/video/BV1PZ4y1i7gZ)
- [HBase 官方文档](https://hbase.apache.org/2.3/book.html)