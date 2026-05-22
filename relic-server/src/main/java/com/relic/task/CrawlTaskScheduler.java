package com.relic.task;

import com.relic.mapper.CrawlTaskLogMapper;
import com.relic.mapper.CrawlTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlTaskScheduler {

    private final CrawlTaskMapper crawlTaskMapper;
    private final CrawlTaskLogMapper crawlTaskLogMapper;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 * * * * ?")
    public void checkScheduledTasks() {
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

                Integer taskId = (Integer) task.get("id");
                String currentStatus = (String) task.get("status");
                if ("running".equals(currentStatus)) continue;

                executeScheduledTask(task);
            } catch (Exception e) {
                log.warn("检查定时任务时出错: {}", e.getMessage());
            }
        }
    }

    private void executeScheduledTask(Map<String, Object> task) {
        Integer id = (Integer) task.get("id");
        String taskName = (String) task.get("taskName");

        log.info("定时任务触发: id={}, name={}", id, taskName);
        String nowStr = LocalDateTime.now().format(DF);

        crawlTaskMapper.updateStatus(id, "running");
        crawlTaskLogMapper.insert(id, taskName, nowStr, "running", 0, null, 0, 0);

        try {
            Thread.sleep((long) (2000 + Math.random() * 3000));
            int crawledCount = (int) (10 + Math.random() * 100);

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
            crawlTaskMapper.updateRunStats(id, "idle", nowStr, nextRunStr);
            log.info("定时任务执行完成: id={}, count={}", id, crawledCount);
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