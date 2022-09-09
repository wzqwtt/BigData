package com.wzq.annotation;

import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用自定义注解
 *
 * @author wzq
 * @create 2022-09-09 22:22
 */
public class TestMethodAnnotation {

    @Override
    @MyMethodAnnotation(title = "toString method", description = "ovveride toString method")
    public String toString() {
        return "Ovveride toString method";
    }

    @Deprecated
    @MyMethodAnnotation(title = "old static method", description = "deprecated old static method")
    public static void oldMethod() {
        System.out.println("old method; don't use it.");
    }


    @SuppressWarnings({"unchecked", "deprecation"})
    @MyMethodAnnotation(title = "test method", description = "suppress warning static method")
    public static void genericTest() throws FileNotFoundException {
        List l = new ArrayList();
        l.add("abc");
        oldMethod();
    }

    /**
     * 用反射接口获取注解信息
     */
    public static void main(String[] args) {
        try {
            // 获取所有method
            Method[] methods = TestMethodAnnotation.class.getClassLoader()
                    .loadClass(("com.wzq.annotation.TestMethodAnnotation"))
                    .getMethods();

            for (Method method : methods) {
                // 方法上是否有MyMethodAnnotation注解
                if (method.isAnnotationPresent(MyMethodAnnotation.class)) {
                    try {
                        // 获取并遍历方法上的所有注解
                        for (Annotation anno : method.getDeclaredAnnotations()) {
                            System.out.println("Annotation in Method '" + method + "' : " + anno);
                        }

                        // 获取MyMethodAnnotation对象信息
                        MyMethodAnnotation methodAnno = method.getAnnotation(MyMethodAnnotation.class);
                        System.out.println(methodAnno.title());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
