package com.relic.mapper;

import com.relic.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    Permission selectById(@Param("id") Integer id);
    List<Permission> selectAll();
    List<Permission> selectByPage(@Param("name") String name, @Param("module") String module,
                                   @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("name") String name, @Param("module") String module);
    int insert(Permission permission);
    int update(Permission permission);
    int deleteById(@Param("id") Integer id);
    List<Permission> selectByRoleId(@Param("roleId") Integer roleId);
}