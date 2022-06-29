package com.wzq;

import jdk.nashorn.internal.codegen.Namespace;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;

public class HBaseDDLNamespace {

    // 声明一个静态属性获取HBase连接
    public static Connection connection = HBaseConnection.connection;

    /**
     * 获取所有可用的命名空间
     *
     * @return 返回所有可用的命名空间
     * @throws IOException 创建Admin、关闭Admin抛出的异常
     */
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

    /**
     * 创建命名空间
     *
     * @param namespace 命名空间名称
     */
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

    /**
     * 删除指定的命名空间
     *
     * @param namespace 命名空间
     * @throws IOException
     */
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

    /**
     * 修改某个命名空间
     *
     * @param namespace 传递一个命名空间
     */
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

    public static void main(String[] args) throws IOException {
        // 测试创建namespace
        // 应该先保证连接没问题，再来调用相关方法
//        createNamespace("wzq");

        // 测试获取所有可用的namespace
//        String[] namespaces = listNamespace();
//        for (String namespace : namespaces) {
//            System.out.println(namespace);
//        }

        // 测试删除指定的namespace
//        deleteNamespace("wzq");

        // 测试修改指定的namespace的user属性
        modifyNamespace("wzq");

        // 其他code
        System.out.println("其他code");

        // 关闭HBase连接
        HBaseConnection.closeConnection();
    }
}
