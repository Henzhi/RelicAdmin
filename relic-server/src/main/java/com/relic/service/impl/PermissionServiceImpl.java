package com.relic.service.impl;

import com.relic.dto.PageDTO;
import com.relic.entity.Permission;
import com.relic.mapper.PermissionMapper;
import com.relic.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public PageDTO<Permission> page(String name, String module, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Permission> records = permissionMapper.selectByPage(name, module, offset, pageSize);
        long total = permissionMapper.countByPage(name, module);
        return new PageDTO<>(total, records, page, pageSize);
    }

    @Override
    public List<Permission> listAll() {
        return permissionMapper.selectAll();
    }

    @Override
    public void create(Permission permission) {
        permission.setCreatedAt(LocalDateTime.now());
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Integer id, Permission permission) {
        permission.setId(id);
        permissionMapper.update(permission);
    }

    @Override
    public void delete(Integer id) {
        permissionMapper.deleteById(id);
    }
}