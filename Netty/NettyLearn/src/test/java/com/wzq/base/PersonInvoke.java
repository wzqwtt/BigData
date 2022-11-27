package com.wzq.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wzq
 * @create 2022-11-26 22:20
 */
public class PersonInvoke {

    public static void main(String[] args) throws Exception {
        // 通过反射获取类
        Class<?> personClass = getPersonClass();
        System.out.println("==========================");

        // 获取被public对象修饰的所有成员变量
//        getPersonFields(personClass);
//        System.out.println("==========================");
//
//        // 获取clz的某个public成员变量
//        getPersonField(personClass);
//        System.out.println("==========================");
//
//        // getDeclaredFields 可以不受修饰符private \ protected限制
//        getPersonDeclaredFields(personClass);
//        System.out.println("==========================");

        // 获取构造方法
//        getPersonConstructor(personClass);

        // 获取成员方法
        getPersonMethod(personClass);

    }

    public static void getPersonMethod(Class<?> clz) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        // 获取所有public成员方法
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            System.out.println(method);
        }

        // 获取指定名称的方法
        System.out.println("获取指定名称的方法:");
        Method getAgeMethod = clz.getMethod("getAge");
        System.out.println(getAgeMethod);

        // 执行方法
        // 获得类
        Constructor<?> constructor = clz.getConstructor(int.class,
                String.class,
                long.class,
                long.class,
                float.class,
                int.class);
        Object wzq = constructor.newInstance(1, "wzq", 1, 1, 1, 1);

        // 执行方法
        int age = (int) getAgeMethod.invoke(wzq);
        System.out.println(age);
    }

    public static void getPersonConstructor(Class<?> clz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 1、获取所有构造方法
        System.out.println("获取所有构造方法:");
        Constructor<?>[] constructors = clz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println(constructor);
        }

        // 2、获取指定构造方法
        // 空参构造方法
        System.out.println("空参构造方法:");
        Constructor<?> constructor1 = clz.getConstructor();
        System.out.println(constructor1);

        // 带参构造方法
        System.out.println("带参构造方法:");
        Constructor<?> constructor2 = clz.getConstructor(int.class, String.class, long.class, long.class, float.class, int.class);
        System.out.println(constructor2);

        // 获取构造方法之后，利用它来创建对象
        System.out.println("空参创建对象:");
        // 第一种方法：
        Object person = constructor1.newInstance();
        System.out.println(person);

        // 第二种方法：
        Object person1 = clz.newInstance();
        System.out.println(person1);

        System.out.println("带参创建对象:");
        Object wzq = constructor2.newInstance(20, "wzq", 1, 1, 1, 1);
        System.out.println(wzq);
    }

    public static void getPersonDeclaredFields(Class<?> clz) throws NoSuchFieldException {
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField);
        }

        // 获取具体某个属性
        Field id = clz.getDeclaredField("id");
        System.out.println(id);

        Field name = clz.getDeclaredField("name");
        System.out.println(name);
    }

    public static void getPersonField(Class<?> clz) {
        try {
            // 获取clz的某个public成员变量
            Field id = clz.getField("id");
            System.out.println(id);

            Field grade = clz.getField("grade");
            System.out.println(grade);

            // 获取不是public修饰的，或者不存在的变量会报错
            /*
             * java.lang.NoSuchFieldException: name
             * 	at java.lang.Class.getField(Class.java:1703)
             * 	at com.wzq.base.PersonInvoke.getPersonField(PersonInvoke.java:36)
             * 	at com.wzq.base.PersonInvoke.main(PersonInvoke.java:21)
             */
//            Field name = clz.getField("name");
//            System.out.println(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void getPersonFields(Class<?> clz) {
        // 获取成员变量 Field[] getFields()
        // 该方法是用于获取类中所有被public所修饰的成员变量（包括父类）
        Field[] fields = clz.getFields();
        System.out.println("成员变量: ");
        for (Field field : fields) {
            System.out.println(field);
        }
    }

    // 获取Class对象
    public static Class<?> getPersonClass() throws ClassNotFoundException {
        // 第一种方式，Class.forName("全类名")
        Class<?> clz1 = Class.forName("com.wzq.base.Person");
        System.out.println(clz1);

        // 第二种方式，类名.class
        Class<Person> clz2 = Person.class;
        System.out.println(clz2);

        // 第三种方式，对象.getClass()
        Person person = new Person();
        Class<? extends Person> clz3 = person.getClass();
        System.out.println(clz3);

        System.out.println(clz1 == clz2);
        System.out.println(clz1 == clz3);

        return clz1;
    }

}
