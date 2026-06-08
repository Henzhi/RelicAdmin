package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserUploadMapper {
    List<Map<String, Object>> selectByPage(@Param("userId") Integer userId,
                                           @Param("username") String username,
                                           @Param("mediaType") String mediaType,
                                           @Param("status") String status,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countByPage(@Param("userId") Integer userId,
                     @Param("username") String username,
                     @Param("mediaType") String mediaType,
                     @Param("status") String status,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);
    /**
     * 查询尚未同步到 user_behaviors 的上传记录
     */
    List<Map<String, Object>> selectUnsynced(@Param("limit") int limit);
}