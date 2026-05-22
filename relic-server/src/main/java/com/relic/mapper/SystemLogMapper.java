package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemLogMapper {
    List<Map<String, Object>> selectByPage(@Param("logLevel") String logLevel,
                                           @Param("module") String module,
                                           @Param("keyword") String keyword,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("logLevel") String logLevel,
                     @Param("module") String module,
                     @Param("keyword") String keyword,
                     @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

    List<Map<String, Object>> selectAllForExport(@Param("logLevel") String logLevel,
                                                  @Param("module") String module,
                                                  @Param("keyword") String keyword,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime);

    int insert(@Param("logLevel") String logLevel,
               @Param("module") String module,
               @Param("message") String message,
               @Param("detail") String detail);
}