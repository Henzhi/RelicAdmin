package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AdminUserCreateDTO;
import com.relic.dto.AdminUserUpdateDTO;
import com.relic.entity.AdminUser;
import com.relic.exception.AccountNotFoundException;
import com.relic.exception.PasswordErrorException;
import com.relic.mapper.AdminUserMapper;
import com.relic.mapper.AdminUserRoleMapper;
import com.relic.service.AdminUserService;
import com.relic.vo.AdminUserVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public PageResultVO<AdminUserVO> page(String username, String realName, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<AdminUserVO> records = adminUserMapper.selectByPage(username, realName, status, offset, pageSize);
        long total = adminUserMapper.countByPage(username, realName, status);
//        List<AdminUserVO> records = entities.stream().map(this::toVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public AdminUserVO getById(Integer id) {
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new AccountNotFoundException("管理员账号不存在");
        }
        return toVO(adminUser);
    }

    @Override
    public AdminUserVO getCurrentAdmin() {
        Long id = BaseContext.getCurrentId();
        if (id == null) {
            throw new RuntimeException("管理员未登录");
        }
        return getById(id.intValue());
    }

    @Override
    @Transactional
    public void create(AdminUserCreateDTO dto) {
        AdminUser existing = adminUserMapper.selectByUsername(dto.getUsername());
        if (existing != null) {
            throw new RuntimeException("管理员账号已存在");
        }
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(dto.getUsername());
        adminUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        adminUser.setRealName(dto.getRealName());
        adminUser.setEmail(dto.getEmail());
        adminUser.setPhone(dto.getPhone());
        adminUser.setStatus("active");
        LocalDateTime now = LocalDateTime.now();
        adminUser.setCreatedAt(now);
        adminUser.setUpdatedAt(now);
        adminUserMapper.insert(adminUser);

        if (dto.getRoleIds() != null && dto.getRoleIds().length > 0) {
            String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            for (Integer roleId : dto.getRoleIds()) {
                adminUserRoleMapper.insert(adminUser.getId(), roleId, nowStr);
            }
        }
    }

    @Override
    public void update(Integer id, AdminUserUpdateDTO dto) {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(id);
        adminUser.setRealName(dto.getRealName());
        adminUser.setEmail(dto.getEmail());
        adminUser.setPhone(dto.getPhone());
        adminUser.setAvatarUrl(dto.getAvatarUrl());
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.update(adminUser);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        adminUserRoleMapper.deleteByAdminUserId(id);
        adminUserMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(Integer[] ids) {
        for (Integer id : ids) {
            adminUserRoleMapper.deleteByAdminUserId(id);
        }
        adminUserMapper.batchDelete(Arrays.asList(ids));
    }

    @Override
    public void updateStatus(Integer id, String status) {
        adminUserMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void assignRoles(Integer adminUserId, Integer[] roleIds) {
        adminUserRoleMapper.deleteByAdminUserId(adminUserId);
        if (roleIds != null && roleIds.length > 0) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            for (Integer roleId : roleIds) {
                adminUserRoleMapper.insert(adminUserId, roleId, now);
            }
        }
    }

    @Override
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new AccountNotFoundException("管理员账号不存在");
        }
        if (!passwordEncoder.matches(oldPassword, adminUser.getPasswordHash())) {
            throw new PasswordErrorException("原密码错误");
        }
        adminUserMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    public void resetPassword(Integer id, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new AccountNotFoundException("管理员账号不存在");
        }
        adminUserMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    private AdminUserVO toVO(AdminUser adminUser) {
        return AdminUserVO.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .realName(adminUser.getRealName())
                .email(adminUser.getEmail())
                .phone(adminUser.getPhone())
                .avatarUrl(adminUser.getAvatarUrl())
                .status(adminUser.getStatus())
                .lastLogin(adminUser.getLastLogin())
                .lastIp(adminUser.getLastIp())
                .createdAt(adminUser.getCreatedAt())
                .updatedAt(adminUser.getUpdatedAt())
                .build();
    }
}