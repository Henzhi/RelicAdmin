package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {
    List<Map<String, Object>> selectByPage(@Param("userId") Integer userId,
                                           @Param("username") String username,
                                           @Param("behaviorType") String behaviorType,
                                           @Param("keyword") String keyword,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countByPage(@Param("userId") Integer userId,
                     @Param("username") String username,
                     @Param("behaviorType") String behaviorType,
                     @Param("keyword") String keyword,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);
}