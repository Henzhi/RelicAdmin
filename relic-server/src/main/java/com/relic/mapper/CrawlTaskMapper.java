package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CrawlTaskMapper {
    List<Map<String, Object>> selectByPage(@Param("status") String status, @Param("priority") Integer priority,
                                           @Param("offset") int offset, @Param("limit") int limit);

    long countByPage(@Param("status") String status, @Param("priority") Integer priority);

    Map<String, Object> selectById(@Param("id") Integer id);

    int insert(@Param("taskName") String taskName, @Param("taskCode") String taskCode,
               @Param("sourceUrl") String sourceUrl, @Param("sourceType") String sourceType,
               @Param("crawlRule") String crawlRule, @Param("priority") Integer priority,
               @Param("cronExpression") String cronExpression, @Param("maxRetry") Integer maxRetry,
               @Param("retryDelay") Integer retryDelay, @Param("timeoutSeconds") Integer timeoutSeconds,
               @Param("description") String description, @Param("enabled") Integer enabled,
               @Param("createdBy") Integer createdBy);

    int update(@Param("id") Integer id, @Param("taskName") String taskName,
               @Param("sourceUrl") String sourceUrl, @Param("sourceType") String sourceType,
               @Param("crawlRule") String crawlRule, @Param("priority") Integer priority,
               @Param("cronExpression") String cronExpression, @Param("maxRetry") Integer maxRetry,
               @Param("retryDelay") Integer retryDelay, @Param("timeoutSeconds") Integer timeoutSeconds,
               @Param("description") String description, @Param("enabled") Integer enabled);

    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    int updateRunStats(@Param("id") Integer id, @Param("status") String status,
                       @Param("lastRunTime") String lastRunTime, @Param("nextRunTime") String nextRunTime);

    int deleteById(@Param("id") Integer id);

    int checkTaskCodeExists(@Param("taskCode") String taskCode);

    List<Map<String, Object>> selectEnabledScheduled();
}