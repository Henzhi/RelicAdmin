package com.relic.service.impl;

import com.relic.mapper.StatisticsMapper;
import com.relic.service.StatisticsService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;
    private static final int ONLINE_MINUTES = 15;

    @Override
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = statisticsMapper.selectDashboardOverview();
        if (overview == null) {
            overview = new HashMap<>();
        }
        overview.put("onlineUsers", statisticsMapper.countOnlineUsers(ONLINE_MINUTES));
        overview.put("todayActiveUsers", statisticsMapper.countOnlineUsers(1440));
        overview.put("todayNewUsers", statisticsMapper.countTodayNewUsers());
        overview.put("todayContentCount", statisticsMapper.countTodayContent());
        overview.put("auditBacklog", statisticsMapper.countAuditBacklog());
        return overview;
    }

    @Override
    public Object getUserTrend(int days) {
        return statisticsMapper.selectUserTrend(days);
    }

    @Override
    public Object getVisitTrend(int days) {
        return statisticsMapper.selectVisitTrend(days);
    }

    @Override
    public Object getDataGrowth(int days) {
        return statisticsMapper.selectDataGrowth(days);
    }

    @Override
    public Object getArtifactByMuseum() {
        return statisticsMapper.selectArtifactByMuseum();
    }

    @Override
    public Object getArtifactByType() {
        return statisticsMapper.selectArtifactByType();
    }

    @Override
    public Object getArtifactByDynasty() {
        return statisticsMapper.selectArtifactByDynasty();
    }

    @Override
    public PageResultVO<Map<String, Object>> getAlertPage(String alertType, String severity, String status,
                                                           int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = statisticsMapper.selectAlertPage(alertType, severity, status, offset, pageSize);
        long total = statisticsMapper.countAlerts(alertType, severity, status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void resolveAlert(Long id, Integer resolvedBy, String resolveRemark) {
        statisticsMapper.resolveAlert(id, resolvedBy, resolveRemark);
    }
}