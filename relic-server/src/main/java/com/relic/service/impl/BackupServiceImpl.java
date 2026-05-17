package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.BackupCreateDTO;
import com.relic.entity.BackupRecord;
import com.relic.mapper.BackupRecordMapper;
import com.relic.mapper.BackupStrategyMapper;
import com.relic.service.BackupService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupServiceImpl implements BackupService {

    private final BackupRecordMapper backupRecordMapper;
    private final BackupStrategyMapper backupStrategyMapper;
    private final DataSource dataSource;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String DB_NAME = "seitem";

    // 排除的 Flyway 系统表
    private static final Set<String> EXCLUDE_TABLES = new HashSet<>(Arrays.asList("flyway_schema_history"));

    @Override
    public PageResultVO<Map<String, Object>> page(Integer status, String backupType, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = backupRecordMapper.selectByPage(status, backupType, offset, pageSize);
        long total = backupRecordMapper.countByPage(status, backupType);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public Map<String, Object> createBackup(BackupCreateDTO dto) {
        Integer operatorId = Math.toIntExact(BaseContext.getCurrentId());
        String backupName = dto.getBackupName() != null ? dto.getBackupName()
                : "手动备份_" + LocalDateTime.now().format(DF);
        String backupType = dto.getBackupType() != null ? dto.getBackupType() : "full";
        String scope = dto.getScope() != null ? dto.getScope() : "全部数据";

        Map<String, Object> strategy = backupStrategyMapper.selectCurrent();
        String storagePath = strategy != null ? (String) strategy.get("storagePath") : "./backups";
        String fileName = backupName.replaceAll("[^a-zA-Z0-9_\\-\\u4e00-\\u9fa5]", "_") + ".sql";
        String filePath = storagePath + File.separator + fileName;

        BackupRecord record = new BackupRecord();
        record.setBackupName(backupName);
        record.setBackupType(backupType);
        record.setScope(scope);
        record.setFileSize(0L);
        record.setFilePath(filePath);
        record.setOperatorId(operatorId);
        record.setStatus(0);
        record.setRemark("备份进行中...");
        backupRecordMapper.insert(record);
        Long backupId = record.getId();

        Map<String, Object> result = new HashMap<>();
        result.put("id", backupId);
        result.put("backupName", backupName);
        result.put("status", "running");

        try {
            File dir = new File(storagePath);
            if (!dir.exists()) dir.mkdirs();

            boolean success = exportViaJdbc(filePath);
            long fileSize = Files.exists(Paths.get(filePath)) ? Files.size(Paths.get(filePath)) : 0L;

            if (success) {
                backupRecordMapper.updateStatus(backupId, 1, fileSize, filePath, "备份完成");
                result.put("status", 1);
                result.put("fileSize", fileSize);
                log.info("备份 {} 完成, {} bytes", backupName, fileSize);
            } else {
                backupRecordMapper.updateStatus(backupId, 2, 0L, filePath, "备份失败");
                result.put("status", "failed");
            }
        } catch (Exception e) {
            log.error("备份失败: {}", e.getMessage(), e);
            backupRecordMapper.updateStatus(backupId, 2, 0L, filePath, "备份失败: " + e.getMessage());
            result.put("status", "failed");
        }
        return result;
    }

    private boolean exportViaJdbc(String outputFile) {
        try (Connection conn = dataSource.getConnection();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(outputFile), "UTF-8"))) {

            writer.write("-- RelicAdmin 数据库备份");
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
                writer.write("-- ----------------------------");
                writer.newLine();
                writer.write("-- 表结构: " + table);
                writer.newLine();
                writer.write("-- ----------------------------");
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

                writer.write("-- ----------------------------");
                writer.newLine();
                writer.write("-- 表数据: " + table);
                writer.newLine();
                writer.write("-- ----------------------------");
                writer.newLine();

                List<String> columns = new ArrayList<>();
                List<int[]> columnTypes = new ArrayList<>();
                List<List<String>> allRows = new ArrayList<>();
                try (Statement st = conn.createStatement();
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
                            int colCount = columns.size();
                            for (int j = 0; j < colCount; j++) {
                                if (j > 0) values.append(", ");
                                String val = row.get(j);
                                if (val == null) {
                                    values.append("NULL");
                                } else {
                                    int sqlType = columnTypes.get(j)[0];
                                    if (isStringType(sqlType)) {
                                        values.append(escapeSql(val));
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
            log.info("JDBC导出完成，共 {} 张表", tables.size());
            return true;

        } catch (Exception e) {
            log.error("JDBC导出失败: {}", e.getMessage(), e);
            return false;
        }
    }

    private String escapeSql(String val) {
        String escaped = val.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\0", "\\0");
        return "'" + escaped + "'";
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

    @Override
    public Map<String, Object> getBackupDetail(Long id) {
        return backupRecordMapper.selectById(id);
    }

    @Override
    public void deleteBackup(Long id) {
        Map<String, Object> record = backupRecordMapper.selectById(id);
        if (record != null) {
            String filePath = (String) record.get("filePath");
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        backupRecordMapper.deleteById(id);
        log.info("备份记录 {} 已删除", id);
    }

    @Override
    public void cleanupExpiredBackups() {
        Map<String, Object> strategy = backupStrategyMapper.selectCurrent();
        int retentionDays = 30;
        if (strategy != null && strategy.get("retentionDays") != null) {
            retentionDays = (Integer) strategy.get("retentionDays");
        }

        List<Map<String, Object>> expired = backupRecordMapper.selectExpired(retentionDays);
        if (expired.isEmpty()) return;

        for (Map<String, Object> record : expired) {
            String filePath = (String) record.get("filePath");
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        List<Long> ids = expired.stream().map(r -> Long.valueOf(r.get("id").toString())).collect(Collectors.toList());
        backupRecordMapper.deleteByIds(ids);
        log.info("清理了 {} 个过期备份记录", ids.size());
    }

    @Override
    public long getStorageUsage() {
        Long sum = backupRecordMapper.sumFileSize();
        return sum != null ? sum : 0L;
    }
}