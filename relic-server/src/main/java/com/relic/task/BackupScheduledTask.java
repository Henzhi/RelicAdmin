package com.relic.task;

import com.relic.entity.BackupRecord;
import com.relic.mapper.BackupRecordMapper;
import com.relic.mapper.BackupStrategyMapper;
import com.relic.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupScheduledTask {

    private final BackupStrategyMapper backupStrategyMapper;
    private final BackupRecordMapper backupRecordMapper;
    private final BackupService backupService;
    private final DataSource dataSource;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String DB_NAME = "seitem";
    private static final Set<String> EXCLUDE_TABLES = new HashSet<>(Arrays.asList("flyway_schema_history"));

    @Scheduled(cron = "0 0 3 * * ?")
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
            long fileSize = Files.exists(Paths.get(filePath)) ? Files.size(Paths.get(filePath)) : 0L;

            if (success) {
                backupRecordMapper.updateStatus(backupId, 1, fileSize, filePath, "定时备份完成");
                log.info("定时备份完成: {}, {} bytes", backupName, fileSize);
            } else {
                backupRecordMapper.updateStatus(backupId, 2, 0L, filePath, "备份失败");
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

            writer.write("-- RelicAdmin 定时自动备份");
            writer.newLine();
            writer.write("-- 导出时间: " + LocalDateTime.now());
            writer.newLine();
            writer.write("-- 数据库: " + DB_NAME);
            writer.newLine();
            writer.newLine();
            writer.write("SET NAMES utf8mb4;");
            writer.newLine();
            writer.write("SET FOREIGN_KEY_CHECKS = 0;");
            writer.newLine();
            writer.newLine();

            List<String> tables = new ArrayList<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    String table = rs.getString(1);
                    if (!EXCLUDE_TABLES.contains(table)) tables.add(table);
                }
            }

            for (String table : tables) {
                writer.write("-- " + table);
                writer.newLine();
                writer.write("DROP TABLE IF EXISTS `" + table + "`;");
                writer.newLine();

                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SHOW CREATE TABLE `" + table + "`")) {
                    if (rs.next()) {
                        writer.write(rs.getString(2) + ";");
                        writer.newLine();
                        writer.newLine();
                    }
                }

                List<String> columns = new ArrayList<>();
                List<int[]> columnTypes = new ArrayList<>();
                List<List<String>> allRows = new ArrayList<>();
                try (Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                     ResultSet rs = st.executeQuery("SELECT * FROM `" + table + "`")) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();
                    for (int i = 1; i <= colCount; i++) {
                        columns.add(meta.getColumnName(i));
                        columnTypes.add(new int[]{meta.getColumnType(i)});
                    }
                    while (rs.next()) {
                        List<String> row = new ArrayList<>();
                        for (int i = 1; i <= colCount; i++) {
                            row.add(rs.getString(i));
                        }
                        allRows.add(row);
                    }
                }

                if (!allRows.isEmpty()) {
                    int total = allRows.size();
                    int batchSize = 500;
                    int batches = (total + batchSize - 1) / batchSize;
                    int colCount = columns.size();

                    for (int batch = 0; batch < batches; batch++) {
                        int start = batch * batchSize;
                        int end = Math.min(start + batchSize, total);

                        writer.write("INSERT INTO `" + table + "` (");
                        writer.write(columns.stream().map(c -> "`" + c + "`")
                                .collect(Collectors.joining(", ")));
                        writer.write(") VALUES");
                        writer.newLine();

                        for (int i = start; i < end; i++) {
                            List<String> row = allRows.get(i);
                            StringBuilder values = new StringBuilder("  (");
                            for (int j = 0; j < colCount; j++) {
                                if (j > 0) values.append(", ");
                                String val = row.get(j);
                                if (val == null) {
                                    values.append("NULL");
                                } else {
                                    int sqlType = columnTypes.get(j)[0];
                                    if (isStringType(sqlType)) {
                                        String escaped = val.replace("\\", "\\\\").replace("'", "\\'")
                                                .replace("\n", "\\n").replace("\r", "\\r");
                                        values.append("'").append(escaped).append("'");
                                    } else {
                                        values.append(val);
                                    }
                                }
                            }
                            values.append(")");
                            if (i != end - 1) {
                                values.append(",");
                            }
                            writer.write(values.toString());
                            writer.newLine();
                        }
                        writer.write(";");
                        writer.newLine();
                    }
                }
                writer.newLine();
            }

            writer.write("SET FOREIGN_KEY_CHECKS = 1;");
            writer.newLine();
            writer.flush();
            log.info("JDBC自动导出完成，共 {} 张表", tables.size());
            return true;
        } catch (Exception e) {
            log.error("JDBC自动导出失败: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isStringType(int sqlType) {
        return sqlType == java.sql.Types.VARCHAR ||
               sqlType == java.sql.Types.CHAR ||
               sqlType == java.sql.Types.LONGVARCHAR ||
               sqlType == java.sql.Types.CLOB ||
               sqlType == java.sql.Types.NVARCHAR ||
               sqlType == java.sql.Types.NCHAR ||
               sqlType == java.sql.Types.LONGNVARCHAR ||
               sqlType == java.sql.Types.DATE ||
               sqlType == java.sql.Types.TIME ||
               sqlType == java.sql.Types.TIMESTAMP ||
               sqlType == java.sql.Types.TIME_WITH_TIMEZONE ||
               sqlType == java.sql.Types.TIMESTAMP_WITH_TIMEZONE;
    }
}