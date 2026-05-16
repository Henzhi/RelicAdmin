package com.relic.mapper;

import com.relic.entity.UserPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    UserPost selectById(@Param("id") Integer id);
    List<UserPost> selectByPage(@Param("username") String username,
                                @Param("status") String status,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
    long countByPage(@Param("username") String username, @Param("status") String status);
    int updateStatus(@Param("id") Integer id, @Param("status") String status,
                     @Param("auditBy") Integer auditBy, @Param("auditTime") String auditTime,
                     @Param("rejectReason") String rejectReason);
    int deleteById(@Param("id") Integer id);
}