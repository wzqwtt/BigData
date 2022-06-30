package com.wzq;

import javafx.scene.control.Tab;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.FileDescriptor;
import java.io.IOException;

public class HBaseDML {

    // 声明一个静态属性，用于获取HBase Connection
    public static Connection connection = HBaseConnection.connection;

    /**
     * 插入数据
     *
     * @param namespace    命名空间
     * @param tableName    表格名称
     * @param rowKey       主键
     * @param columnFamily 列族名称
     * @param columnName   列名称
     * @param value        值
     */
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

    /**
     * 读取对应的一行中的某一列
     *
     * @param namespace    命名空间
     * @param tableName    表名
     * @param rowKey       主键
     * @param columnFamily 列族
     * @param columnName   列名
     */
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

    /**
     * 扫描表
     *
     * @param namespace 命名空间
     * @param tableName 表名称
     * @param startRow  开始的row，默认包含
     * @param stopRow   结束的row，默认不包含
     */
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

    /**
     * 带过滤的扫描
     *
     * @param namespace    命名空间
     * @param tableName    表格名称
     * @param startRow     开始的row 包含
     * @param stopRow      结束的row 不包含
     * @param columnFamily 列族
     * @param columnName   列名
     * @param value        值
     */
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

    /**
     * 删除一行中的一列数据
     *
     * @param namespace    命名空间
     * @param tableName    表名
     * @param rowKey       主键
     * @param columnFamily 列族
     * @param columnName   列名
     */
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

    public static void main(String[] args) throws IOException {

        // 测试put数据
        putCell("bigdata", "student", "2001", "info", "age", "80");
        putCell("bigdata", "student", "2019", "info", "name", "abc");
        putCell("bigdata", "student", "2005", "info", "age", "30");

        // 测试读取数据
        getCells("bigdata", "student", "2001", "info", "name");

        // 测试扫描指定行的数据
        scanRows("bigdata", "student", "1003", "2021");

        // 测试扫描指定value的数据
        filterScan("bigdata", "student", "1001", "2022",
                "info", "name", "haha");

        // 测试删除某行的某列数据
        deleteColumn("bigdata", "student", "2005", "info", "age");

        // 关闭connection连接
        HBaseConnection.closeConnection();
    }

}