package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper {
    int insert(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
    int deleteByRoleId(@Param("roleId") Integer roleId);
    int deleteByRoleAndPermission(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
}