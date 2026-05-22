package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperationLogMapper {
    List<Map<String, Object>> selectByPage(@Param("userId") Integer userId,
                                           @Param("operationType") String operationType,
                                           @Param("targetType") String targetType,
                                           @Param("keyword") String keyword,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("userId") Integer userId,
                     @Param("operationType") String operationType,
                     @Param("targetType") String targetType,
                     @Param("keyword") String keyword,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

    List<Map<String, Object>> selectAllForExport(@Param("userId") Integer userId,
                                                  @Param("operationType") String operationType,
                                                  @Param("targetType") String targetType,
                                                  @Param("keyword") String keyword,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime);

    int insert(@Param("userId") Long userId,
               @Param("operationType") String operationType,
               @Param("targetType") String targetType,
               @Param("targetId") String targetId,
               @Param("oldValue") String oldValue,
               @Param("newValue") String newValue,
               @Param("ip") String ip,
               @Param("userAgent") String userAgent);
}