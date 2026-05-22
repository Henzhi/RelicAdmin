package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {

    Map<String, Object> selectDashboardOverview();

    List<Map<String, Object>> selectUserTrend(@Param("days") int days);

    List<Map<String, Object>> selectArtifactByMuseum();

    List<Map<String, Object>> selectArtifactByType();

    List<Map<String, Object>> selectArtifactByDynasty();

    long countOnlineUsers(@Param("minutes") int minutes);

    long countTodayNewUsers();

    long countTodayContent();

    long countAuditBacklog();

    List<Map<String, Object>> selectVisitTrend(@Param("days") int days);

    List<Map<String, Object>> selectDataGrowth(@Param("days") int days);

    List<Map<String, Object>> selectActiveAlerts();

    List<Map<String, Object>> selectAlertPage(@Param("alertType") String alertType,
                                               @Param("severity") String severity,
                                               @Param("status") String status,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    long countAlerts(@Param("alertType") String alertType,
                     @Param("severity") String severity,
                     @Param("status") String status);

    int resolveAlert(@Param("id") Long id,
                     @Param("resolvedBy") Integer resolvedBy,
                     @Param("resolveRemark") String resolveRemark);

    int insertAlert(@Param("alertType") String alertType,
                    @Param("severity") String severity,
                    @Param("title") String title,
                    @Param("message") String message,
                    @Param("source") String source);
}