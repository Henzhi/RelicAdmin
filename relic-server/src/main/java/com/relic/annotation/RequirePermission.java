package com.relic.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理端权限码校验注解。
 *
 * <p>标注在管理端 Service 方法上，由 {@link com.relic.aspect.PermissionCheckAspect}
 * 校验当前管理员的「角色 → role_permissions → permissions.name」权限链：
 * 超级管理员天然放行；其他角色需被授予 {@link #value()} 中任一权限码。
 * 权限码与权限管理界面中的「权限标识」一致（如 user:delete）。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /** 所需权限码，命中其一即放行 */
    String[] value();
}
