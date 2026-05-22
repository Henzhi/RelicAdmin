package com.relic.task;

import com.relic.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertCheckScheduler {

    private final StatisticsMapper statisticsMapper;
    private final DataSource dataSource;

    @Scheduled(fixedRate = 60000)
    public void checkDatabaseConnection() {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(5)) {
                statisticsMapper.insertAlert("db_failure", "critical",
                        "数据库连接异常", "数据库连接校验失败，无法建立有效连接", "database");
                log.error("Database connection check failed");
            }
        } catch (Exception e) {
            statisticsMapper.insertAlert("db_failure", "critical",
                    "数据库连接失败", "数据库连接异常: " + e.getMessage(), "database");
            log.error("Database connection error", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void checkDiskSpace() {
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                long freeSpace = root.getFreeSpace();
                long totalSpace = root.getTotalSpace();
                if (totalSpace > 0) {
                    double usagePercent = 100.0 * (totalSpace - freeSpace) / totalSpace;
                    if (usagePercent > 90) {
                        statisticsMapper.insertAlert("disk_full", "warning",
                                "磁盘空间不足",
                                String.format("磁盘 %s 使用率已达 %.1f%%，剩余空间 %.2f GB",
                                        root.getPath(), usagePercent, freeSpace / (1024.0 * 1024.0 * 1024.0)),
                                "system");
                    }
                }
            }
        }
    }
}