package com.relic.service;

import com.relic.dto.PageDTO;
import com.relic.entity.Permission;

import java.util.List;

public interface PermissionService {
    PageDTO<Permission> page(String name, String module, int page, int pageSize);
    List<Permission> listAll();
    void create(Permission permission);
    void update(Integer id, Permission permission);
    void delete(Integer id);
}