package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper {
    //弃用
//    int insert(@Param("userId") Integer userId, @Param("roleId") Integer roleId,
//               @Param("grantedBy") Integer grantedBy, @Param("grantedAt") String grantedAt);
    int deleteByUserId(@Param("userId") Integer userId);
    int deleteByUserAndRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}