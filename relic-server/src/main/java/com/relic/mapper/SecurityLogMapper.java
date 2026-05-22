package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SecurityLogMapper {
    List<Map<String, Object>> selectByPage(@Param("eventType") String eventType,
                                           @Param("userId") Integer userId,
                                           @Param("keyword") String keyword,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("eventType") String eventType,
                     @Param("userId") Integer userId,
                     @Param("keyword") String keyword,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

    List<Map<String, Object>> selectAllForExport(@Param("eventType") String eventType,
                                                  @Param("userId") Integer userId,
                                                  @Param("keyword") String keyword,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime);

    int insert(@Param("userId") Long userId,
               @Param("eventType") String eventType,
               @Param("ip") String ip,
               @Param("detail") String detail);
}