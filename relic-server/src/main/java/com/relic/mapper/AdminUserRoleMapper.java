package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminUserRoleMapper {
    int insert(@Param("adminUserId") Integer adminUserId, @Param("roleId") Integer roleId, @Param("createdAt") String createdAt);
    int deleteByAdminUserId(@Param("adminUserId") Integer adminUserId);
}