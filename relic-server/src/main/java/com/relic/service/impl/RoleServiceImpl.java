package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.RoleCreateDTO;
import com.relic.dto.RoleUpdateDTO;
import com.relic.entity.Permission;
import com.relic.entity.Role;
import com.relic.mapper.PermissionMapper;
import com.relic.mapper.RoleMapper;
import com.relic.mapper.RolePermissionMapper;
import com.relic.service.RoleService;
import com.relic.vo.PageResultVO;
import com.relic.vo.PermissionVO;
import com.relic.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public PageResultVO<RoleVO> page(String name, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Role> entities = roleMapper.selectByPage(name, offset, pageSize);
        long total = roleMapper.countByPage(name);
        List<RoleVO> records = entities.stream().map(VoConverter::toRoleVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public List<RoleVO> listAll() {
        return roleMapper.selectAll().stream().map(VoConverter::toRoleVO).collect(Collectors.toList());
    }

    @Override
    public RoleVO getById(Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return VoConverter.toRoleVO(role);
    }

    @Override
    public void create(RoleCreateDTO dto) {
        Role existing = roleMapper.selectByName(dto.getName());
        if (existing != null) {
            throw new RuntimeException("角色名称已存在");
        }
        Role role = new Role();
        role.setName(dto.getName());
        role.setDisplayName(dto.getDisplayName());
        role.setDescription(dto.getDescription());
        LocalDateTime now = LocalDateTime.now();
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        roleMapper.insert(role);
    }

    @Override
    public void update(Integer id, RoleUpdateDTO dto) {
        Role role = new Role();
        role.setId(id);
        role.setDisplayName(dto.getDisplayName());
        role.setDescription(dto.getDescription());
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
    public List<PermissionVO> getRolePermissions(Integer roleId) {
        return permissionMapper.selectByRoleId(roleId).stream()
                .map(VoConverter::toPermissionVO).collect(Collectors.toList());
    }
}