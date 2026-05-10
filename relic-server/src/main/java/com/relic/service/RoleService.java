package com.relic.service;

import com.relic.dto.*;
import com.relic.vo.PageResultVO;
import com.relic.vo.RoleVO;
import com.relic.vo.PermissionVO;

import java.util.List;

public interface RoleService {
    PageResultVO<RoleVO> page(String name, int page, int pageSize);
    List<RoleVO> listAll();
    RoleVO getById(Integer id);
    void create(RoleCreateDTO dto);
    void update(Integer id, RoleUpdateDTO dto);
    void delete(Integer id);
    void assignPermissions(Integer roleId, Integer[] permissionIds);
    List<PermissionVO> getRolePermissions(Integer roleId);
}