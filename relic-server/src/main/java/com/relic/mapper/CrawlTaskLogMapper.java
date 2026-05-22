package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CrawlTaskLogMapper {
    List<Map<String, Object>> selectByPage(@Param("taskId") Integer taskId, @Param("status") String status,
                                           @Param("offset") int offset, @Param("limit") int limit);

    long countByPage(@Param("taskId") Integer taskId, @Param("status") String status);

    int insert(@Param("taskId") Integer taskId, @Param("taskName") String taskName,
               @Param("startTime") String startTime, @Param("status") String status,
               @Param("crawledCount") Integer crawledCount, @Param("errorMessage") String errorMessage,
               @Param("retryCount") Integer retryCount, @Param("executorId") Integer executorId);

    int updateResult(@Param("id") Integer id, @Param("endTime") String endTime,
                     @Param("status") String status, @Param("crawledCount") Integer crawledCount,
                     @Param("errorMessage") String errorMessage);

    List<Map<String, Object>> selectRecentByTaskId(@Param("taskId") Integer taskId, @Param("limit") int limit);
}