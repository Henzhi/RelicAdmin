package com.relic.service;

import com.relic.dto.PageDTO;
import com.relic.entity.Role;
import com.relic.entity.Permission;

import java.util.List;

public interface RoleService {
    PageDTO<Role> page(String name, int page, int pageSize);
    List<Role> listAll();
    Role getById(Integer id);
    void create(Role role);
    void update(Integer id, Role role);
    void delete(Integer id);
    void assignPermissions(Integer roleId, Integer[] permissionIds);
    List<Permission> getRolePermissions(Integer roleId);
}