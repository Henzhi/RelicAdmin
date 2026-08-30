package com.relic.service.impl;

import com.relic.cache.CacheNames;
import com.relic.mapper.StatisticsMapper;
import com.relic.service.StatisticsService;
import com.relic.vo.PageQuery;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    @Cacheable(cacheNames = CacheNames.DASHBOARD_OVERVIEW)
    public Map<String, Object> getDashboardOverview() {
        // 性能优化（2026-08-05）：由 6 次串行查询合并为单次查询（统计指标一次带回），
        // 配合索引与范围查询改写，数据库侧耗时 276ms -> 50ms；
        // 结果再经 Redis 缓存 60 秒（CacheNames.DASHBOARD_OVERVIEW），高频刷新仪表盘不再穿透到库。
        Map<String, Object> overview = statisticsMapper.selectDashboardOverview();
        if (overview == null) {
            overview = new HashMap<>();
        }
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
        PageQuery pq = PageQuery.of(page, pageSize);
        List<Map<String, Object>> records = statisticsMapper.selectAlertPage(alertType, severity, status, pq.getOffset(), pq.getPageSize());
        long total = statisticsMapper.countAlerts(alertType, severity, status);
        return pq.toResult(total, records);
    }

    @Override
    public void resolveAlert(Long id, Integer resolvedBy, String resolveRemark) {
        statisticsMapper.resolveAlert(id, resolvedBy, resolveRemark);
    }
}