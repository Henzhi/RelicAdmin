package com.relic.mapper;

import com.relic.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(@Param("id") Integer id);
    User selectByUsername(@Param("username") String username);
    List<User> selectByPage(@Param("username") String username, @Param("nickname") String nickname,
                            @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    long countByPage(@Param("username") String username, @Param("nickname") String nickname, @Param("status") String status);
    int insert(User user);
    int update(User user);
    int updatePassword(@Param("id") Integer id, @Param("passwordHash") String passwordHash);
    int updateStatus(@Param("id") Integer id, @Param("status") String status, @Param("banReason") String banReason);
    int updateAvatar(@Param("id") Integer id, @Param("avatarUrl") String avatarUrl);
    int updateCommentDisabled(@Param("id") Integer id, @Param("commentDisabled") Integer commentDisabled);
    int updateUploadDisabled(@Param("id") Integer id, @Param("uploadDisabled") Integer uploadDisabled);
    int updateLastLogin(@Param("id") Integer id, @Param("lastLogin") String lastLogin, @Param("lastIp") String lastIp);
    int deleteById(@Param("id") Integer id);
}