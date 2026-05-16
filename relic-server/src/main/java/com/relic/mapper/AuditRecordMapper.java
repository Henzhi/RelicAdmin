package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditRecordMapper {
    List<Map<String, Object>> selectByPage(@Param("contentType") String contentType,
                                           @Param("manualAuditResult") String manualAuditResult,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("contentType") String contentType,
                     @Param("manualAuditResult") String manualAuditResult);

    int updateAuditResult(@Param("id") Long id,
                          @Param("manualAuditResult") String manualAuditResult,
                          @Param("auditorId") Long auditorId,
                          @Param("rejectReason") String rejectReason);
}