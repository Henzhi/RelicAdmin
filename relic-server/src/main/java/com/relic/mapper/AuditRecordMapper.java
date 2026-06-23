package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditRecordMapper {
    List<Map<String, Object>> selectByPage(@Param("contentType") String contentType,
                                           @Param("manualAuditResult") String manualAuditResult,
                                           @Param("sourceType") String sourceType,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("contentType") String contentType,
                     @Param("manualAuditResult") String manualAuditResult,
                     @Param("sourceType") String sourceType,
                     @Param("startDate") String startDate,
                     @Param("endDate") String endDate);

    int updateAuditResult(@Param("id") Long id,
                          @Param("manualAuditResult") String manualAuditResult,
                          @Param("auditorId") Long auditorId,
                          @Param("rejectReason") String rejectReason);

    int batchUpdateResult(@Param("ids") Long[] ids,
                          @Param("manualAuditResult") String manualAuditResult,
                          @Param("auditorId") Long auditorId,
                          @Param("rejectReason") String rejectReason);

    int updateByContentId(@Param("contentId") String contentId,
                          @Param("manualAuditResult") String manualAuditResult,
                          @Param("rejectReason") String rejectReason);

    Map<String, Object> selectStats(@Param("startDate") String startDate,
                                    @Param("endDate") String endDate);

    List<Map<String, Object>> selectAuditorStats(@Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    int insert(@Param("contentId") String contentId, @Param("contentType") String contentType,
               @Param("sourceType") String sourceType, @Param("content") String content,
               @Param("submitterId") Integer submitterId,
               @Param("autoAuditResult") String autoAuditResult,
               @Param("manualAuditResult") String manualAuditResult);

    Map<String, Object> selectById(@Param("id") Long id);

    List<Map<String, Object>> selectContentTypeStats(@Param("startDate") String startDate,
                                                     @Param("endDate") String endDate);
}