package com.wzq.base.project;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 读取prop.properties，创建配置文件中指定的对象
 *
 * @author wzq
 * @create 2022-11-27 20:34
 */
public class Main {

    public static void main(String[] args) {
        try {
            // 创建配置文件对象
            Properties properties = new Properties();

            // 加载配置文件
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("prop.properties");
            properties.load(inputStream);

            // 获取配置文件中定义的数据
            String className = properties.getProperty("className");
            String methodName = properties.getProperty("methodName");

            // 通过反射获取对象
            Class<?> aClass = Class.forName(className);
            // 通过构造方法新建实例
            // Constructor<?> constructor = aClass.getConstructor();
            // Object o = constructor.newInstance();

            // 直接创建实例
            Object o = aClass.newInstance();

            // 获取方法并执行
            Method method = aClass.getMethod(methodName);
            Object invoke = method.invoke(o);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
