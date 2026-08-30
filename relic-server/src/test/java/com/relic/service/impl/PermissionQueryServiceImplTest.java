package com.relic.service.impl;

import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.entity.AdminUserRole;
import com.relic.entity.Permission;
import com.relic.mapper.AdminUserRoleMapper;
import com.relic.mapper.PermissionMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 权限查询服务单元测试（RBAC 切面的判定核心）
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermissionQueryServiceImplTest {

    @Mock
    private AdminUserRoleMapper adminUserRoleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionQueryServiceImpl service;

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    private AdminUserRole roleOf(Integer roleId) {
        AdminUserRole role = new AdminUserRole();
        role.setRoleId(roleId);
        return role;
    }

    @Test
    void isSuperAdmin_shouldReturnTrueForSuperAdmin() {
        when(adminUserRoleMapper.selectByAdminUserId(1L)).thenReturn(roleOf(RoleConstant.SUPER_ADMIN));
        assertTrue(service.isSuperAdmin(1L));
    }

    @Test
    void isSuperAdmin_shouldReturnFalseForNormalRole() {
        when(adminUserRoleMapper.selectByAdminUserId(2L)).thenReturn(roleOf(99));
        assertFalse(service.isSuperAdmin(2L));
    }

    @Test
    void isSuperAdmin_shouldReturnFalseWhenRoleRowMissing() {
        // 未分配角色的管理员不应触发 NPE
        when(adminUserRoleMapper.selectByAdminUserId(3L)).thenReturn(null);
        assertFalse(service.isSuperAdmin(3L));
    }

    @Test
    void isSuperAdmin_shouldReturnFalseForNullId() {
        assertFalse(service.isSuperAdmin(null));
    }

    @Test
    void getCurrentAdminRoleId_shouldReadFromBaseContext() {
        BaseContext.setCurrentId(1L);
        when(adminUserRoleMapper.selectByAdminUserId(1L)).thenReturn(roleOf(RoleConstant.SUPER_ADMIN));
        assertEquals(RoleConstant.SUPER_ADMIN, service.getCurrentAdminRoleId());
    }

    @Test
    void getCurrentAdminRoleId_shouldReturnNullWhenNotLoggedIn() {
        assertNull(service.getCurrentAdminRoleId());
    }

    @Test
    void getPermissionCodes_shouldMapPermissionNames() {
        Permission p1 = new Permission();
        p1.setName("user:delete");
        Permission p2 = new Permission();
        p2.setName("user:ban");
        when(permissionMapper.selectByRoleId(99)).thenReturn(List.of(p1, p2));

        Set<String> codes = service.getPermissionCodes(99);
        assertEquals(Set.of("user:delete", "user:ban"), codes);
    }

    @Test
    void getPermissionCodes_shouldReturnEmptySetWhenNoPermission() {
        when(permissionMapper.selectByRoleId(99)).thenReturn(List.of());
        assertTrue(service.getPermissionCodes(99).isEmpty());
    }

    @Test
    void getPermissionCodes_shouldReturnEmptySetForNullRole() {
        assertTrue(service.getPermissionCodes(null).isEmpty());
    }
}
