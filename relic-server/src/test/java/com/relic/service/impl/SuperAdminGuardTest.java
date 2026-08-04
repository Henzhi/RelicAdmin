package com.relic.service.impl;

import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.entity.AdminUserRole;
import com.relic.exception.InsufficientPermissionsException;
import com.relic.mapper.AdminUserRoleMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * H-04：超级管理员权限守卫单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SuperAdminGuardTest {

    @Mock
    private AdminUserRoleMapper adminUserRoleMapper;

    private SuperAdminGuard buildGuard() {
        return new SuperAdminGuard(adminUserRoleMapper);
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    @Test
    void superAdmin_shouldPass() {
        BaseContext.setCurrentId(1L);
        AdminUserRole role = new AdminUserRole();
        role.setRoleId(RoleConstant.SUPER_ADMIN);
        when(adminUserRoleMapper.selectByAdminUserId(1L)).thenReturn(role);

        SuperAdminGuard guard = buildGuard();
        assertDoesNotThrow(() -> guard.requireSuperAdmin("无权限"));
    }

    @Test
    void nonSuperAdmin_shouldThrow() {
        BaseContext.setCurrentId(2L);
        AdminUserRole role = new AdminUserRole();
        role.setRoleId(RoleConstant.CONTENT_AUDITOR);
        when(adminUserRoleMapper.selectByAdminUserId(2L)).thenReturn(role);

        SuperAdminGuard guard = buildGuard();
        assertThrows(InsufficientPermissionsException.class,
                () -> guard.requireSuperAdmin("只有超级管理员才能执行该操作"));
    }

    @Test
    void noRole_shouldBeTreatedAsNonSuperAdmin() {
        BaseContext.setCurrentId(3L);
        when(adminUserRoleMapper.selectByAdminUserId(3L)).thenReturn(null);

        SuperAdminGuard guard = buildGuard();
        assertThrows(InsufficientPermissionsException.class,
                () -> guard.requireSuperAdmin("只有超级管理员才能执行该操作"));
    }

    @Test
    void noLogin_shouldThrow() {
        // 未登录（BaseContext 无当前用户）
        SuperAdminGuard guard = buildGuard();
        assertThrows(InsufficientPermissionsException.class,
                () -> guard.requireSuperAdmin("只有超级管理员才能执行该操作"));
    }
}
