package com.relic.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理端角色校验注解。
 *
 * <p>标注在管理端 Service 方法上，由 {@link com.relic.aspect.PermissionCheckAspect}
 * 在方法执行前校验当前登录管理员的角色：命中 {@link #value()} 中任一角色即放行，
 * 未分配角色或角色不匹配时抛出权限异常。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /** 允许调用的角色ID集合（见 RoleConstant） */
    int[] value();
}
