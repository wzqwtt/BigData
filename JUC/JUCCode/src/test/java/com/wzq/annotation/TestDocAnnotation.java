package com.wzq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author wzq
 * @create 2022-09-09 22:05
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TestDocAnnotation {

    public String value() default "default";

}
