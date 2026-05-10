package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.PermissionCreateDTO;
import com.relic.dto.PermissionUpdateDTO;
import com.relic.entity.Permission;
import com.relic.mapper.PermissionMapper;
import com.relic.service.PermissionService;
import com.relic.vo.PageResultVO;
import com.relic.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public PageResultVO<PermissionVO> page(String name, String module, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Permission> entities = permissionMapper.selectByPage(name, module, offset, pageSize);
        long total = permissionMapper.countByPage(name, module);
        List<PermissionVO> records = entities.stream().map(VoConverter::toPermissionVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public List<PermissionVO> listAll() {
        return permissionMapper.selectAll().stream().map(VoConverter::toPermissionVO).collect(Collectors.toList());
    }

    @Override
    public void create(PermissionCreateDTO dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setDisplayName(dto.getDisplayName());
        permission.setModule(dto.getModule());
        permission.setCreatedAt(LocalDateTime.now());
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Integer id, PermissionUpdateDTO dto) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setDisplayName(dto.getDisplayName());
        permission.setModule(dto.getModule());
        permissionMapper.update(permission);
    }

    @Override
    public void delete(Integer id) {
        permissionMapper.deleteById(id);
    }
}