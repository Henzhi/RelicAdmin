package com.relic.service.impl;

import com.relic.annotation.RequireRole;
import com.relic.constant.MessageConstant;
import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.dto.AdminUserCreateDTO;
import com.relic.dto.AdminUserUpdateDTO;
import com.relic.entity.AdminUser;
import com.relic.entity.AdminUserRole;
import com.relic.exception.*;
import com.relic.mapper.AdminUserMapper;
import com.relic.mapper.AdminUserRoleMapper;
import com.relic.service.AdminUserService;
import com.relic.service.PermissionQueryService;
import com.relic.vo.AdminUserVO;
import com.relic.vo.PageQuery;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRoleValueException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PermissionQueryService permissionQueryService;

    @Override
    public PageResultVO<AdminUserVO> page(String username, String realName, String status,
                                          String createdAtStart, String createdAtEnd, int page, int pageSize) {
        PageQuery pq = PageQuery.of(page, pageSize);
        List<AdminUserVO> records = adminUserMapper.selectByPage(username, realName, status, createdAtStart, createdAtEnd, pq.getOffset(), pq.getPageSize());
        long total = adminUserMapper.countByPage(username, realName, status, createdAtStart, createdAtEnd);
        return pq.toResult(total, records);
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
    @RequireRole(RoleConstant.SUPER_ADMIN)
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
        //改用单选
        if (dto.getRoleId() != null) {
            String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            adminUserRoleMapper.insert(adminUser.getId(), dto.getRoleId(), nowStr);
//            for (Integer roleId : dto.getRoleIds()) {
//                adminUserRoleMapper.insert(adminUser.getId(), roleId, nowStr);
//            }
        }
    }

    @Override
    public void update(Integer id, AdminUserUpdateDTO dto) {
        //目前操作人id
        Long currentId = BaseContext.getCurrentId();
        //权限验证：本人或超级管理员
        if(!Long.valueOf(id).equals(currentId)&&
                !permissionQueryService.isSuperAdmin(currentId)){
            //不是超级管理员不允许更新其他管理员信息
            throw new InsufficientPermissionsException(MessageConstant.PERMISSION_DENIED);
        }
        //管理员自己可以更新自己的信息
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
    @RequireRole(RoleConstant.SUPER_ADMIN)
    @Transactional
    public void delete(Integer id) {
        if(Long.valueOf(id).equals(BaseContext.getCurrentId())){
            throw new IllegalOperationException("不能删除自己");
        }
        adminUserRoleMapper.deleteByAdminUserId(id);
        adminUserMapper.deleteById(id);
    }

    @Override
    @RequireRole(RoleConstant.SUPER_ADMIN)
    @Transactional
    public void batchDelete(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            throw new IllegalArgumentException("请选择要删除的管理员");
        }
        Long currentId = BaseContext.getCurrentId();
        List<Integer> idList = Arrays.asList(ids);
        if (currentId != null && idList.contains(currentId.intValue())) {
            throw new IllegalOperationException("不能删除自己");
        }
        // 系统必须至少保留一名超级管理员
        long deletingSuperAdmins = 0;
        for (Integer id : idList) {
            AdminUserRole targetRole = adminUserRoleMapper.selectByAdminUserId(id.longValue());
            if (targetRole != null && targetRole.getRoleId() != null
                    && targetRole.getRoleId() == RoleConstant.SUPER_ADMIN) {
                deletingSuperAdmins++;
            }
        }
        if (deletingSuperAdmins > 0
                && deletingSuperAdmins >= adminUserRoleMapper.countByRoleId(RoleConstant.SUPER_ADMIN)) {
            throw new IllegalOperationException("不能删除最后一名超级管理员");
        }
        for (Integer id : idList) {
            adminUserRoleMapper.deleteByAdminUserId(id);
        }
        adminUserMapper.batchDelete(idList);
    }

    @Override
    @RequireRole(RoleConstant.SUPER_ADMIN)
    public void updateStatus(Integer id, String status) {
        adminUserMapper.updateStatus(id, status);
    }

    @Override
    @RequireRole(RoleConstant.SUPER_ADMIN)
    @Transactional
    public void assignRoles(Integer adminUserId, Integer roleId) throws InvalidRoleValueException {
        adminUserRoleMapper.deleteByAdminUserId(adminUserId);
        if (roleId != null) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            adminUserRoleMapper.insert(adminUserId, roleId, now);
            //弃用多选
//            for (Integer roleId : roleIds) {
//                adminUserRoleMapper.insert(adminUserId, roleId, now);
//            }
        }else{
            throw new InvalidRoleValueException("无效的角色值");
        }
    }

    @Override
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        //目前操作人id
        Long currentId = BaseContext.getCurrentId();
        //权限验证：本人或超级管理员
        if(!Long.valueOf(id).equals(currentId)&&
                !permissionQueryService.isSuperAdmin(currentId)){
            //不是超级管理员不允许更新其他管理员密码
            throw new InsufficientPermissionsException(MessageConstant.PERMISSION_DENIED);
        }
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new AccountNotFoundException("管理员账号不存在");
        }
        if (oldPassword != null && oldPassword.equals(newPassword)) {
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_SAME_AS_OLD);
        }
        if (!passwordEncoder.matches(oldPassword, adminUser.getPasswordHash())) {
            throw new PasswordErrorException("原密码错误");
        }
        adminUserMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    public void resetPassword(Integer id, String newPassword) {
        //目前操作人id
        Long currentId = BaseContext.getCurrentId();
        //权限验证：本人或超级管理员
        if(!Long.valueOf(id).equals(currentId)&&
                !permissionQueryService.isSuperAdmin(currentId)){
            //不是超级管理员不允许重置其他管理员密码
            throw new InsufficientPermissionsException(MessageConstant.PERMISSION_DENIED);
        }
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