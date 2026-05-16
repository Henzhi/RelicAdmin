package com.relic.mapper;

import com.relic.entity.UserComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserCommentMapper {
    UserComment selectById(@Param("id") Integer id);
    List<Map<String, Object>> selectByPage(@Param("keyword") String keyword,
                                           @Param("userId") Integer userId,
                                           @Param("username") String username,
                                           @Param("artifactId") Integer artifactId,
                                           @Param("status") String status,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countByPage(@Param("keyword") String keyword,
                     @Param("userId") Integer userId,
                     @Param("username") String username,
                     @Param("artifactId") Integer artifactId,
                     @Param("status") String status,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);
}