package com.relic.mapper;

import com.relic.entity.UserComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    UserComment selectById(@Param("id") Integer id);
    List<UserComment> selectByPage(@Param("username") String username,
                                   @Param("status") String status,
                                   @Param("artifactId") Integer artifactId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    long countByPage(@Param("username") String username,
                     @Param("status") String status,
                     @Param("artifactId") Integer artifactId);
    int updateStatus(@Param("id") Integer id, @Param("status") String status,
                     @Param("auditBy") Integer auditBy, @Param("auditTime") String auditTime,
                     @Param("rejectReason") String rejectReason);
    int deleteById(@Param("id") Integer id);
}