package com.relic.service.impl;

import com.relic.dto.PageDTO;
import com.relic.entity.Permission;
import com.relic.entity.Role;
import com.relic.mapper.PermissionMapper;
import com.relic.mapper.RoleMapper;
import com.relic.mapper.RolePermissionMapper;
import com.relic.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public PageDTO<Role> page(String name, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Role> records = roleMapper.selectByPage(name, offset, pageSize);
        long total = roleMapper.countByPage(name);
        return new PageDTO<>(total, records, page, pageSize);
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectAll();
    }

    @Override
    public Role getById(Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return role;
    }

    @Override
    public void create(Role role) {
        Role existing = roleMapper.selectByName(role.getName());
        if (existing != null) {
            throw new RuntimeException("角色名称已存在");
        }
        LocalDateTime now = LocalDateTime.now();
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        roleMapper.insert(role);
    }

    @Override
    public void update(Integer id, Role role) {
        role.setId(id);
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.update(role);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        rolePermissionMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignPermissions(Integer roleId, Integer[] permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && permissionIds.length > 0) {
            for (Integer permId : permissionIds) {
                rolePermissionMapper.insert(roleId, permId);
            }
        }
    }

    @Override
    public List<Permission> getRolePermissions(Integer roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }
}