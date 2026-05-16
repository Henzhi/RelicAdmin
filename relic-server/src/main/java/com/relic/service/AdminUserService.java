package com.relic.service;

import com.relic.dto.AdminUserCreateDTO;
import com.relic.dto.AdminUserUpdateDTO;
import com.relic.vo.AdminUserVO;
import com.relic.vo.PageResultVO;

public interface AdminUserService {
    PageResultVO<AdminUserVO> page(String username, String realName, String status, int page, int pageSize);
    AdminUserVO getById(Integer id);
    AdminUserVO getCurrentAdmin();
    void create(AdminUserCreateDTO dto);
    void update(Integer id, AdminUserUpdateDTO dto);
    void delete(Integer id);
    void batchDelete(Integer[] ids);
    void updateStatus(Integer id, String status);
    void assignRoles(Integer adminUserId, Integer[] roleIds);
    void updatePassword(Integer id, String oldPassword, String newPassword);
    void resetPassword(Integer id, String newPassword);
}