package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行公共字段自动填充处理
 */
@Target(ElementType.METHOD)             // 该注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)     // 运行时生效
public @interface AutoFill {
    // 枚举：数据库操作类型，INSERT 和 UPDATE
    // TODO 1. 在 Java 注解中，可以定义元素（成员）来接受参数，value 是其中一个常用的元素名称。
    //         当使用注解时，可以使用 value 元素来设置注解的值，而无需在注解使用时显式指定元素名称。
    // TODO 2. 在Java中，当一个注解只有一个元素，且该元素名称为value时，在使用注解时可以省略元素名称，
    //         直接将值作为注解的参数传递给 value() 方法
    OperationType value();
}
