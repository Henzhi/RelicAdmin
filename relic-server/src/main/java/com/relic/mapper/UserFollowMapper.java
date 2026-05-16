package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserFollowMapper {
    List<Map<String, Object>> selectByPage(@Param("followerId") Integer followerId,
                                           @Param("followeeId") Integer followeeId,
                                           @Param("username") String username,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countByPage(@Param("followerId") Integer followerId,
                     @Param("followeeId") Integer followeeId,
                     @Param("username") String username,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);
}