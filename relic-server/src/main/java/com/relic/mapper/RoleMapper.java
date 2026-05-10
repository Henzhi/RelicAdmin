package com.relic.mapper;

import com.relic.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role selectById(@Param("id") Integer id);
    Role selectByName(@Param("name") String name);
    List<Role> selectAll();
    List<Role> selectByPage(@Param("name") String name, @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("name") String name);
    int insert(Role role);
    int update(Role role);
    int deleteById(@Param("id") Integer id);
    List<Role> selectByUserId(@Param("userId") Integer userId);
}