package com.relic.mapper;

import com.relic.entity.AdminUser;
import com.relic.vo.AdminUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminUserMapper {
    AdminUser selectById(@Param("id") Integer id);
    AdminUser selectByUsername(@Param("username") String username);
    List<AdminUserVO> selectByPage(@Param("username") String username, @Param("realName") String realName,
                                   @Param("status") String status,
                                   @Param("createdAtStart") String createdAtStart, @Param("createdAtEnd") String createdAtEnd,
                                   @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("username") String username, @Param("realName") String realName, @Param("status") String status,
                     @Param("createdAtStart") String createdAtStart, @Param("createdAtEnd") String createdAtEnd);
    int insert(AdminUser adminUser);
    int update(AdminUser adminUser);
    int updatePassword(@Param("id") Integer id, @Param("passwordHash") String passwordHash);
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    int updateLastLogin(@Param("id") Integer id, @Param("lastLogin") String lastLogin, @Param("lastIp") String lastIp);
    int deleteById(@Param("id") Integer id);
    int batchDelete(@Param("ids") List<Integer> ids);
}