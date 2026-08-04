package com.relic.service.impl;

import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.entity.AdminUserRole;
import com.relic.exception.InsufficientPermissionsException;
import com.relic.mapper.AdminUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * H-04 超级管理员权限校验守卫（复用组件，避免各 Service 重复实现）
 */
@Component
@RequiredArgsConstructor
public class SuperAdminGuard {

    private final AdminUserRoleMapper adminUserRoleMapper;

    /**
     * 校验当前登录操作人是否为超级管理员，否则抛出权限异常
     */
    public void requireSuperAdmin(String message) {
        if (!isSuperAdmin(BaseContext.getCurrentId())) {
            throw new InsufficientPermissionsException(message);
        }
    }

    /**
     * 判断指定操作人是否为超级管理员（未分配角色视为非超管，避免 NPE）
     */
    public boolean isSuperAdmin(Long currentId) {
        if (currentId == null) {
            return false;
        }
        AdminUserRole adminUserRole = adminUserRoleMapper.selectByAdminUserId(currentId);
        return adminUserRole != null && RoleConstant.SUPER_ADMIN.equals(adminUserRole.getRoleId());
    }
}
