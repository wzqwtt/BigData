package com.wzq.annotation;

import java.lang.annotation.*;

/**
 * 被Inherited注解修饰的注解具有继承性，即TestInheritedAnnotation的子类具有该注解
 *
 * @author wzq
 * @create 2022-09-09 22:07
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TestInheritedAnnotation {

    String[] values();

    int number();

}
