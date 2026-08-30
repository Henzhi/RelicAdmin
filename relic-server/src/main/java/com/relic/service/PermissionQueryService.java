package com.relic.service;

import java.util.Set;

/**
 * 管理端角色/权限查询服务，供权限校验切面与业务代码复用。
 */
public interface PermissionQueryService {

    /**
     * 查询当前登录管理员的角色ID（BaseContext 中的用户ID）。
     *
     * @return 角色ID；未登录或未分配角色时返回 null
     */
    Integer getCurrentAdminRoleId();

    /**
     * 判断指定管理员是否为超级管理员（未分配角色视为非超管，不抛NPE）。
     */
    boolean isSuperAdmin(Long adminId);

    /**
     * 查询角色被授予的权限码集合（permissions.name）。
     * 结果带 5 分钟 Redis 缓存，权限变更最迟 5 分钟生效。
     */
    Set<String> getPermissionCodes(Integer roleId);
}
