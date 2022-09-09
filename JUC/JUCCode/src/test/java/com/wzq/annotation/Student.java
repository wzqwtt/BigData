package com.wzq.annotation;

import org.junit.Test;

import java.lang.annotation.Annotation;

/**
 * @author wzq
 * @create 2022-09-09 22:10
 */
@TestInheritedAnnotation(values = {"value"}, number = 10)
class Person {
}

public class Student extends Person {
    @Test
    public void test() {
        Class clazz = Student.class;
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation.toString());
        }
    }
}