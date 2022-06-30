package com.wzq;

import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseDDLTable {

    // 声明一个静态的HBase客户端连接
    public static Connection connection = HBaseConnection.connection;

    /**
     * 判断表是否存在
     *
     * @param namespace 表所属命名空间
     * @param tableName 表名
     * @return 返回一个布尔值，true代表存在，false不存在
     * @throws IOException 抛出获取admin、释放admin实例的异常
     */
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

    /**
     * 创建一个Table
     *
     * @param namespace      命名空间
     * @param tableName      表格名称
     * @param columnFamilies 变长参数，可以传递多个列族信息
     * @throws IOException 抛出创建Admin、释放Admin实例的异常
     */
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

    /**
     * 修改表格中一个列族的版本
     *
     * @param namespace    命名空间
     * @param tableName    表格
     * @param columnFamily 列族
     * @param version      版本
     * @throws IOException 抛出创建Admin、释放Admin实例的异常
     */
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

    /**
     * 删除表格
     *
     * @param namespace 命名空间名称
     * @param tableName 表格名称
     * @return 删除成功返回true，否则返回false
     * @throws IOException 抛出创建Admin、释放Admin实例的异常
     */
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

    public static void main(String[] args) throws IOException {
        // 测试表是否存在
        System.out.println(isTableExists("bigdata", "student"));    // 该表存在
        System.out.println(isTableExists("wzq", "wzq"));    // 该表不存在
        System.out.println(isTableExists("wtt", "wtt"));    // 该命名空间不存在

        // 测试创建表格
        createTable("test", "person", "info", "msg");

        // 测试修改表格的版本号
        modifyTable("test", "person", "info", 6);

        // 测试删除表格
        deleteTable("test", "person");

        // 关闭connection连接
        HBaseConnection.closeConnection();
    }
}