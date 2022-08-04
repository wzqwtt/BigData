

本Blog介绍HBase DML API操作，[该部分所有的代码都可以点我直达！](https://github.com/wzqwtt/BigData/blob/master/HBase/HBaseCode/src/main/java/com/wzq/HBaseDML.java)，当然我在代码中也添加了大量的注释，读者也完全可以自己看code！

因为HBase的链接是重量级的，所以不建议用的时候再去建立连接，而是直接创建一个单例的连接，用的时候直接去拿，[创建连接在上一篇Blog有学习过](https://github.com/wzqwtt/BigData/blob/master/HBase/4%E3%80%81HBase-DDL-API%E6%93%8D%E4%BD%9C.md#%E4%B8%80%E8%8E%B7%E5%8F%96hbase%E8%BF%9E%E6%8E%A5)



在这篇Blog种，主要就是DML操作了，DML操作主要用户HBase Connection的Table实例，本Blog包括以下内容：



**向表格种插入数据 put ：**

```java
public static void putCell(String namespace, String tableName, String rowKey, String columnFamily,
                           String columnName, String value) throws IOException {

    // 0、判断表是否存在
    if (!HBaseDDLTable.isTableExists(namespace, tableName)) {
        System.out.println("表" + namespace + ":" + tableName + "不存在，无法添加数据");
        return;
    }

    // 1、获取Table
    Table table = connection.getTable(TableName.valueOf(namespace, tableName));

    // 2、调用方法插入数据
    // 2.1 创建一个put对象
    Put put = new Put(Bytes.toBytes(rowKey));

    // 2.2 给put对象添加属性
    put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes(value));

    // 2.3 将对象写入对应方法
    try {
        table.put(put);
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、关闭Table
    table.close();
}
```

**读取某行的某一列 get ：**

```java
public static void getCells(String namespace, String tableName, String rowKey, String columnFamily,
                            String columnName) throws IOException {
    // 0、判断表是否存在
    if (!HBaseDDLTable.isTableExists(namespace, tableName)) {
        System.out.println("表" + namespace + ":" + tableName + "不存在，无法添加数据");
        return;
    }

    // 1、获取Table
    Table table = connection.getTable(TableName.valueOf(namespace, tableName));

    // 2、读取对应的一行中的某一列
    // 2.1 创建Get对象
    Get get = new Get(Bytes.toBytes(rowKey));

    // 2.2 如果直接调用get方法读取数据，此时读取一整行数据
    //     如果想读取某一列数据，需要添加对应的参数
    get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));

    // 设置读取数据的版本
    get.readAllVersions();

    try {
        // 2.3 读取数据得到result对象
        Result result = table.get(get);

        // 2.4 处理数据
        Cell[] cells = result.rawCells();

        // 2.5 测试方法直接把数据打印到控制台
        // 实际开发中，数据应该返回交给额外的方法专门处理数据
        for (Cell cell : cells) {
            // cell存储数据比较底层
            String value = new String(CellUtil.cloneValue(cell));
            System.out.println(value);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、关闭Table
    table.close();
}
```



**扫描表 getScanner ：**

```java
public static void scanRows(String namespace, String tableName, String startRow,
                            String stopRow) throws IOException {
    // 0、判断表是否存在
    if (!HBaseDDLTable.isTableExists(namespace, tableName)) {
        System.out.println("表" + namespace + ":" + tableName + "不存在，无法扫描数据");
        return;
    }

    // 1、获取table
    Table table = connection.getTable(TableName.valueOf(namespace, tableName));

    // 2、扫描表
    // 2.1 创建一个Scan对象
    Scan scan = new Scan();

    // 2.2 设置startRow，默认包含startRow这一行
    // 如果startRow的rowkey不存在，那么从最接近startRow的下一行开始scan
    scan.withStartRow(Bytes.toBytes(startRow));

    // 2.3 设置stopRow，默认不包含stopRow这一行
    scan.withStopRow(Bytes.toBytes(stopRow));

    // 2.4 得到结果
    try {
        ResultScanner scanner = table.getScanner(scan);
        // result来记录一行数据，cell[]
        // ResultScanner来记录多行数据，result[]
        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                String row = new String(CellUtil.cloneRow(cell));   // 行号
                String value = new String(CellUtil.cloneValue(cell));   // 值
                String family = new String(CellUtil.cloneFamily(cell));   // 列族
                String columnName = new String(CellUtil.cloneQualifier(cell));   // 列名
                System.out.print(row + "-" + family + "-" + columnName + "-" + value + "\t");
            }
            System.out.println();   // 换行输出
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、关闭table
    table.close();
}
```



**带过滤的扫描 getScanner ：**

```java
public static void filterScan(String namespace, String tableName, String startRow, String stopRow,
                              String columnFamily, String columnName, String value) throws IOException {
    // 0、判断表是否存在
    if (!HBaseDDLTable.isTableExists(namespace, tableName)) {
        System.out.println("表" + namespace + ":" + tableName + "不存在，无法扫描数据");
        return;
    }

    // 1、获取table
    Table table = connection.getTable(TableName.valueOf(namespace, tableName));

    // 2、带过滤的扫描
    // 2.1 创建一个scan对象
    Scan scan = new Scan();
    // 2.2 添加扫描的起始参数
    scan.withStartRow(Bytes.toBytes(startRow));
    scan.withStopRow(Bytes.toBytes(stopRow));

    // 2.3 添加过滤；注意：可以添加多个过滤
    FilterList filterList = new FilterList();

    // 创建过滤器，过滤器有两类
    //  (1) 结果只保留当前列的数据
    ColumnValueFilter columnValueFilter = new ColumnValueFilter(
        Bytes.toBytes(columnFamily),    // 列族
        Bytes.toBytes(columnName),  // 列名
        CompareOperator.EQUAL,  // 比较关系
        Bytes.toBytes(value)    // 值
    );

    //        filterList.addFilter(columnValueFilter);

    // (2) 结果保留整行数据，<b>结果同时会保留没用当前要过滤的列的数据</b>
    SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
        Bytes.toBytes(columnFamily),
        Bytes.toBytes(columnName),
        CompareOperator.EQUAL,
        Bytes.toBytes(value)
    );

    filterList.addFilter(singleColumnValueFilter);

    // 2.4 添加过滤器到scan对象
    scan.setFilter(filterList);

    try {
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                String val = new String(CellUtil.cloneValue(cell));
                String column = new String(CellUtil.cloneQualifier(cell));
                String row = new String(CellUtil.cloneRow(cell));
                String family = new String(CellUtil.cloneFamily(cell));
                System.out.print(row + "-" + family + "-" + column + "-" + val + "\t");
            }
            System.out.println();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、释放table
    table.close();
}
```



**删除一行中的一列数据 delete ：**

```java
public static void deleteColumn(String namespace, String tableName, String rowKey, String columnFamily,
                                String columnName) throws IOException {
    // 0、判断表是否存在
    if (!HBaseDDLTable.isTableExists(namespace, tableName)) {
        System.out.println("表" + namespace + ":" + tableName + "不存在，无法删除数据");
        return;
    }

    // 1、获取table
    Table table = connection.getTable(TableName.valueOf(namespace, tableName));

    // 2、删除数据
    try {
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        // addColumn 删除一个版本
        // delete.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
        // addColumns 删除所有版本的列数据
        delete.addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
        table.delete(delete);
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 3、释放table
    table.close();
}
```



# 参考资料

- [尚硅谷 HBase 2.x 学习视频](https://www.bilibili.com/video/BV1PZ4y1i7gZ)
- [HBase 官方文档](https://hbase.apache.org/2.3/book.html)