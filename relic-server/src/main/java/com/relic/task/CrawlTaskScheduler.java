package com.relic.task;

import com.relic.mapper.CrawlTaskLogMapper;
import com.relic.mapper.CrawlTaskMapper;
import com.relic.utils.CrawlExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 爬取任务调度器
 *
 * <p>M-05 整改说明：
 * 已接入真实爬虫 {@link CrawlExecutor}，支持 web / api / rss 三种数据源，
 * 由 {@code relic.crawl.scheduler-enabled} 开关控制调度是否启用（默认开启）。
 * 历史模拟实现（随机数）已删除。</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlTaskScheduler {

    private final CrawlTaskMapper crawlTaskMapper;
    private final CrawlTaskLogMapper crawlTaskLogMapper;
    private final CrawlExecutor crawlExecutor;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** M-05：调度开关，默认开启（已接入真实爬虫） */
    @Value("${relic.crawl.scheduler-enabled:true}")
    private boolean schedulerEnabled;

    @Scheduled(cron = "0 * * * * ?")
    public void checkScheduledTasks() {
        if (!schedulerEnabled) {
            // 幂等：仅启动时提示一次，避免每 60s 刷日志
            return;
        }
        List<Map<String, Object>> tasks = crawlTaskMapper.selectEnabledScheduled();
        if (tasks.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        for (Map<String, Object> task : tasks) {
            try {
                String cronExpr = (String) task.get("cronExpression");
                if (cronExpr == null || cronExpr.isBlank()) continue;

                CronExpression cron = CronExpression.parse(cronExpr);
                Object nextRunObj = task.get("nextRunTime");
                LocalDateTime nextRun = null;
                if (nextRunObj != null) {
                    if (nextRunObj instanceof LocalDateTime) {
                        nextRun = (LocalDateTime) nextRunObj;
                    } else {
                        nextRun = LocalDateTime.parse(nextRunObj.toString(), DF);
                    }
                }

                LocalDateTime shouldRun = cron.next(now.minusMinutes(1));
                if (shouldRun == null) continue;
                if (shouldRun.isAfter(now) || (nextRun != null && nextRun.isAfter(now))) continue;

                String currentStatus = (String) task.get("status");
                if ("running".equals(currentStatus)) continue;

                executeScheduledTask(task);
            } catch (Exception e) {
                log.warn("检查定时任务时出错: {}", e.getMessage());
            }
        }
    }

    /**
     * M-05：执行真实爬取任务，状态流转与日志记录保持与原实现一致。
     */
    private void executeScheduledTask(Map<String, Object> task) {
        Integer id = (Integer) task.get("id");
        String taskName = (String) task.get("taskName");

        log.info("定时任务触发: id={}, name={}", id, taskName);
        String nowStr = LocalDateTime.now().format(DF);

        crawlTaskMapper.updateStatus(id, "running");
        crawlTaskLogMapper.insert(id, taskName, nowStr, "running", 0, null, 0, 0);

        try {
            String sourceUrl = (String) task.get("sourceUrl");
            String sourceType = (String) task.get("sourceType");
            String crawlRule = (String) task.get("crawlRule");
            Integer timeoutSeconds = task.get("timeoutSeconds") != null
                    ? ((Number) task.get("timeoutSeconds")).intValue() : 300;

            List<Map<String, Object>> items = crawlExecutor.crawl(sourceUrl, sourceType, crawlRule, timeoutSeconds);
            int crawledCount = items == null ? 0 : items.size();

            String endTimeStr = LocalDateTime.now().format(DF);
            List<Map<String, Object>> lastLog = crawlTaskLogMapper.selectRecentByTaskId(id, 1);
            if (lastLog != null && !lastLog.isEmpty()) {
                Object logIdObj = lastLog.get(0).get("id");
                Integer logId = null;
                if (logIdObj instanceof Long) logId = ((Long) logIdObj).intValue();
                else if (logIdObj instanceof Integer) logId = (Integer) logIdObj;

                if (logId != null) {
                    crawlTaskLogMapper.updateResult(logId, endTimeStr, "success", crawledCount, null);
                }
            }

            String cronExpr = (String) task.get("cronExpression");
            String nextRunStr = null;
            if (cronExpr != null && !cronExpr.isBlank()) {
                CronExpression cron = CronExpression.parse(cronExpr);
                LocalDateTime nextRun = cron.next(LocalDateTime.now());
                if (nextRun != null) nextRunStr = nextRun.format(DF);
            }
            crawlTaskMapper.updateRunStats(id, "completed", nowStr, nextRunStr);
            log.info("定时任务执行完成: id={}, crawled={}", id, crawledCount);
        } catch (Exception e) {
            log.error("定时任务执行失败: id={}, error={}", id, e.getMessage());
            String endTimeStr = LocalDateTime.now().format(DF);
            List<Map<String, Object>> lastLog = crawlTaskLogMapper.selectRecentByTaskId(id, 1);
            if (lastLog != null && !lastLog.isEmpty()) {
                Object logIdObj = lastLog.get(0).get("id");
                Integer logId = null;
                if (logIdObj instanceof Long) logId = ((Long) logIdObj).intValue();
                else if (logIdObj instanceof Integer) logId = (Integer) logIdObj;

                if (logId != null) {
                    crawlTaskLogMapper.updateResult(logId, endTimeStr, "failed", 0, e.getMessage());
                }
            }
            crawlTaskMapper.updateRunStats(id, "failed", nowStr, null);
        }
    }
}
