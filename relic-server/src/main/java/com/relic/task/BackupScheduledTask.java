package com.relic.task;

import com.relic.entity.BackupRecord;
import com.relic.mapper.BackupRecordMapper;
import com.relic.mapper.BackupStrategyMapper;
import com.relic.service.BackupService;
import com.relic.utils.BackupCryptoUtil;
import com.relic.websocket.WebSocketServer;
import com.relic.utils.SqlExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupScheduledTask {

    private final BackupStrategyMapper backupStrategyMapper;
    private final BackupRecordMapper backupRecordMapper;
    private final BackupService backupService;
    private final DataSource dataSource;
    private final BackupCryptoUtil backupCryptoUtil;
    private final WebSocketServer webSocketServer;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // 执行时间由 BackupScheduleConfig 依据 backup_strategies.backup_cron 动态触发，
    // 这里不再用 @Scheduled 固定 cron，否则管理员配置的备份时间会被忽略
    @SchedulerLock(name = "autoBackup", lockAtMostFor = "PT2H", lockAtLeastFor = "PT1M")
    public void autoBackup() {
        log.info("=== 定时备份任务检查 ===");
        Map<String, Object> strategy = backupStrategyMapper.selectCurrent();
        if (strategy == null) {
            log.info("未找到备份策略配置，跳过");
            return;
        }
        Object enabled = strategy.get("autoBackupEnabled");
        if (enabled == null || !Integer.valueOf(1).equals(enabled)) {
            log.info("自动备份未启用，跳过");
            return;
        }

        String backupType = strategy.get("backupType") != null ? strategy.get("backupType").toString() : "full";
        String backupName = "自动备份_" + LocalDateTime.now().format(DF);
        String storagePath = strategy.get("storagePath") != null ? strategy.get("storagePath").toString() : "./backups";
        String fileName = backupName.replaceAll("[^a-zA-Z0-9_\\-\\u4e00-\\u9fa5]", "_") + ".sql";
        String filePath = storagePath + File.separator + fileName;

        BackupRecord record = new BackupRecord();
        record.setBackupName(backupName);
        record.setBackupType(backupType);
        record.setScope("全部数据");
        record.setFileSize(0L);
        record.setFilePath(filePath);
        record.setOperatorId(0);
        record.setStatus(0);
        record.setRemark("定时自动备份进行中...");
        backupRecordMapper.insert(record);
        Long backupId = record.getId();

        try {
            File dir = new File(storagePath);
            if (!dir.exists()) dir.mkdirs();

            boolean success = exportViaJdbc(filePath);
            // M-12：按策略 encrypt_enabled 决定是否加密备份文件
            boolean encryptEnabled = strategy.get("encryptEnabled") != null
                    && Integer.valueOf(1).equals(strategy.get("encryptEnabled"));
            String storedFilePath = filePath;
            if (success && encryptEnabled) {
                File plain = new File(filePath);
                File encrypted = backupCryptoUtil.encrypt(plain);
                if (encrypted.exists()) {
                    storedFilePath = encrypted.getAbsolutePath();
                }
            }
            long fileSize = Files.exists(Paths.get(storedFilePath)) ? Files.size(Paths.get(storedFilePath)) : 0L;

            if (success) {
                backupRecordMapper.updateStatus(backupId, 1, fileSize, storedFilePath, "定时备份完成");
                log.info("定时备份完成: {}, {} bytes", backupName, fileSize);
                webSocketServer.sendEvent("backup", java.util.Map.of(
                        "backupId", backupId, "backupName", backupName, "result", "success"));
            } else {
                backupRecordMapper.updateStatus(backupId, 2, 0L, storedFilePath, "备份失败");
                webSocketServer.sendEvent("backup", java.util.Map.of(
                        "backupId", backupId, "backupName", backupName, "result", "failed"));
            }
            backupService.cleanupExpiredBackups();
        } catch (Exception e) {
            log.error("定时备份失败: {}", e.getMessage(), e);
            backupRecordMapper.updateStatus(backupId, 2, 0L, filePath, "备份失败: " + e.getMessage());
        }
    }

    private boolean exportViaJdbc(String outputFile) {
        try (Connection conn = dataSource.getConnection();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(outputFile), "UTF-8"))) {

            String header = "-- RelicAdmin 定时自动备份\n"
                    + "-- 导出时间: " + LocalDateTime.now() + "\n";
            // 流式导出：避免大表全量加载内存导致 OOM
            SqlExportUtil.exportAllTables(conn, writer, header);
            return true;
        } catch (Exception e) {
            log.error("JDBC自动导出失败: {}", e.getMessage(), e);
            return false;
        }
    }
}