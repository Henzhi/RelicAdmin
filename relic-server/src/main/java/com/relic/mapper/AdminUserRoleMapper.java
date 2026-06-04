package com.relic.mapper;

import com.relic.entity.AdminUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminUserRoleMapper {
    int insert(@Param("adminUserId") Integer adminUserId, @Param("roleId") Integer roleId, @Param("createdAt") String createdAt);
    int deleteByAdminUserId(@Param("adminUserId") Integer adminUserId);

    @Select("select id,admin_user_id,role_id,created_at from admin_user_role where admin_user_id=#{id}")
    AdminUserRole selectByAdminUserId(Long id);
}