package com.relic.controller.admin;

import com.relic.result.Result;
import com.relic.service.StatisticsService;
import com.relic.vo.PageResultVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "管理端-数据统计", description = "系统监控看板统计数据")
public class StatisticsAdminController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(statisticsService.getDashboardOverview());
    }

    @GetMapping("/user-trend")
    public Result<Object> userTrend(@RequestParam(defaultValue = "30") int days) {
        return Result.success(statisticsService.getUserTrend(days));
    }

    @GetMapping("/visit-trend")
    public Result<Object> visitTrend(@RequestParam(defaultValue = "30") int days) {
        return Result.success(statisticsService.getVisitTrend(days));
    }

    @GetMapping("/data-growth")
    public Result<Object> dataGrowth(@RequestParam(defaultValue = "30") int days) {
        return Result.success(statisticsService.getDataGrowth(days));
    }

    @GetMapping("/artifact-by-museum")
    public Result<Object> artifactByMuseum() {
        return Result.success(statisticsService.getArtifactByMuseum());
    }

    @GetMapping("/artifact-by-type")
    public Result<Object> artifactByType() {
        return Result.success(statisticsService.getArtifactByType());
    }

    @GetMapping("/artifact-by-dynasty")
    public Result<Object> artifactByDynasty() {
        return Result.success(statisticsService.getArtifactByDynasty());
    }

    @GetMapping("/alerts/page")
    public Result<PageResultVO<Map<String, Object>>> alertPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status) {
        return Result.success(statisticsService.getAlertPage(alertType, severity, status, page, pageSize));
    }

    @PutMapping("/alerts/{id}/resolve")
    public Result<Void> resolveAlert(@PathVariable Long id,
                                      @RequestParam(required = false) String remark) {
        statisticsService.resolveAlert(id, null, remark);
        return Result.success();
    }
}