package com.relic.annotation;

import com.relic.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段的自动填充
 */
@Target(ElementType.METHOD)  //指定注解加载什么位置（方法上)
@Retention(RetentionPolicy.RUNTIME)  //指定注解在什么级别可用
public @interface AutoFill {
    /**
     * 填充类型
     * UPDATE:更新
     * INSERT:插入
     */
    OperationType value();
}
