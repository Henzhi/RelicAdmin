package com.relic.service.impl;

import com.relic.cache.CacheNames;
import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.entity.AdminUserRole;
import com.relic.entity.Permission;
import com.relic.mapper.AdminUserRoleMapper;
import com.relic.mapper.PermissionMapper;
import com.relic.service.PermissionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionQueryServiceImpl implements PermissionQueryService {

    private final AdminUserRoleMapper adminUserRoleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public Integer getCurrentAdminRoleId() {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return null;
        }
        AdminUserRole role = adminUserRoleMapper.selectByAdminUserId(currentId);
        return role != null ? role.getRoleId() : null;
    }

    @Override
    public boolean isSuperAdmin(Long adminId) {
        if (adminId == null) {
            return false;
        }
        AdminUserRole role = adminUserRoleMapper.selectByAdminUserId(adminId);
        return role != null && role.getRoleId() != null && role.getRoleId() == RoleConstant.SUPER_ADMIN;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.ROLE_PERMISSIONS, key = "#roleId")
    public Set<String> getPermissionCodes(Integer roleId) {
        if (roleId == null) {
            return Set.of();
        }
        List<Permission> permissions = permissionMapper.selectByRoleId(roleId);
        if (permissions == null || permissions.isEmpty()) {
            return Set.of();
        }
        return permissions.stream()
                .map(Permission::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
