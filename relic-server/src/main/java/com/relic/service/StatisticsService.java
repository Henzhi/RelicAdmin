package com.relic.service;

import com.relic.vo.PageResultVO;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getDashboardOverview();

    Object getUserTrend(int days);

    Object getVisitTrend(int days);

    Object getDataGrowth(int days);

    Object getArtifactByMuseum();

    Object getArtifactByType();

    Object getArtifactByDynasty();

    PageResultVO<Map<String, Object>> getAlertPage(String alertType, String severity, String status, int page, int pageSize);

    void resolveAlert(Long id, Integer resolvedBy, String resolveRemark);
}