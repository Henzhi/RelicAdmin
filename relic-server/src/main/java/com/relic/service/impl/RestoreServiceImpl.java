package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.RestoreConfirmDTO;
import com.relic.dto.RestoreRecordCreateDTO;
import com.relic.entity.AdminUser;
import com.relic.mapper.AdminUserMapper;
import com.relic.mapper.BackupRecordMapper;
import com.relic.mapper.RestoreRecordMapper;
import com.relic.service.RestoreService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestoreServiceImpl implements RestoreService {

    private final RestoreRecordMapper restoreRecordMapper;
    private final BackupRecordMapper backupRecordMapper;
    private final AdminUserMapper adminUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @Override
    public PageResultVO<Map<String, Object>> page(Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = restoreRecordMapper.selectByPage(status, offset, pageSize);
        long total = restoreRecordMapper.countByPage(status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public Map<String, Object> restore(Long backupId, RestoreConfirmDTO dto) {
        Map<String, Object> backup = backupRecordMapper.selectById(backupId);
        if (backup == null) {
            throw new IllegalArgumentException("备份记录不存在");
        }
        Object backupStatus = backup.get("status");
        if (backupStatus == null || !Integer.valueOf(1).equals(backupStatus)) {
            throw new IllegalArgumentException("只能从已完成状态的备份进行恢复");
        }

        Integer operatorId = Math.toIntExact(BaseContext.getCurrentId());
        String backupName = (String) backup.get("backupName");
        String filePath = (String) backup.get("filePath");

        String confirmPassword = dto.getConfirmPassword();
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("请输入确认密码");
        }
        AdminUser adminUser = adminUserMapper.selectById(operatorId);
        if (adminUser == null) {
            throw new IllegalArgumentException("管理员账号不存在");
        }
        if (!passwordEncoder.matches(confirmPassword, adminUser.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误，恢复操作已取消");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("backupId", backupId);
        result.put("backupName", backupName);
        log.warn("[Restore] 管理员 {} 发起数据恢复: backupId={}, backupName={}", operatorId, backupId, backupName);

        try {
            String emergencyPath = filePath + ".before_restore.sql";
            exportCurrentToFile(emergencyPath);
            log.info("[Restore] 恢复前应急备份已保存: {}", emergencyPath);

            File sqlFile = new File(filePath);
            if (!sqlFile.exists()) {
                throw new IllegalArgumentException("备份文件不存在: " + filePath);
            }

            long executedStatements = executeSqlFile(sqlFile);

            String remark = "恢复成功，共处理 " + executedStatements + " 行数据。应急备份: " + emergencyPath;
            Long restoreRecordId = insertFinalRecord(backupId, backupName, operatorId, 1, remark);
            result.put("status", "success");
            result.put("restoredRows", executedStatements);
            log.info("[Restore] 数据恢复成功: restoreId={}, backupId={}, 行数={}", restoreRecordId, backupId, executedStatements);

        } catch (Exception e) {
            log.error("[Restore] 数据恢复失败: backupId={}, reason={}", backupId, e.getMessage(), e);
            String remark = "恢复失败: " + e.getMessage();
            Long restoreRecordId = insertFinalRecord(backupId, backupName, operatorId, 2, remark);
            log.info("[Restore] 失败记录已写入: restoreId={}", restoreRecordId);
            result.put("status", "failed");
        }

        return result;
    }

    private Long insertFinalRecord(Long backupId, String backupName, Integer operatorId, Integer status, String remark) {
        Long insertedId = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO restore_records (backup_id, backup_name, operator_id, status, remark, created_at) VALUES (?, ?, ?, ?, ?, NOW())",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, backupId);
            ps.setString(2, backupName);
            ps.setInt(3, operatorId);
            ps.setInt(4, status);
            ps.setString(5, remark);
            ps.executeUpdate();
            try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    insertedId = rs.getLong(1);
                }
            }
            if (insertedId == null) {
                try (Statement st = conn.createStatement();
                     java.sql.ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (rs.next()) {
                        insertedId = rs.getLong(1);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[Restore] 写入恢复记录失败: {}", e.getMessage(), e);
        }
        log.info("[Restore] 恢复记录写入完成: id={}, status={}, backupId={}", insertedId, status, backupId);
        return insertedId;
    }

    private long executeSqlFile(File sqlFile) throws Exception {
        log.info("[Restore] 开始恢复 SQL 文件: {}", sqlFile.getAbsolutePath());
        StringBuilder sql = new StringBuilder();
        long lineCount = 0;
        long executedCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8));
             Connection conn = dataSource.getConnection()) {

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                sql.append(line).append("\n");

                if (trimmed.endsWith(";")) {
                    String statement = sql.toString().trim();
                    sql.setLength(0);

                    if (statement.isEmpty() || statement.equals(";")) continue;

                    try (Statement st = conn.createStatement()) {
                        st.execute(statement);
                        executedCount++;
                    } catch (Exception e) {
                        log.warn("[Restore] SQL 执行警告 (第{}行): {}", lineCount, e.getMessage());
                    }
                }
            }

            String remaining = sql.toString().trim();
            if (!remaining.isEmpty()) {
                try (Statement st = conn.createStatement()) {
                    st.execute(remaining);
                    executedCount++;
                }
            }

            conn.commit();
            log.info("[Restore] SQL 恢复完成: {} 行, 执行 {} 条语句", lineCount, executedCount);
            return executedCount;
        }
    }

    private void exportCurrentToFile(String outputFile) {
        try (Connection conn = dataSource.getConnection();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            writer.write("-- RelicAdmin 恢复前应急备份");
            writer.newLine();
            writer.write("-- 时间: " + java.time.LocalDateTime.now());
            writer.newLine();
            writer.newLine();
            writer.write("SET NAMES utf8mb4;");
            writer.newLine();
            writer.write("SET FOREIGN_KEY_CHECKS = 0;");
            writer.newLine();
            writer.newLine();

            List<String> tables = new ArrayList<>();
            try (Statement st = conn.createStatement();
                 java.sql.ResultSet rs = st.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    String t = rs.getString(1);
                    if (!"flyway_schema_history".equals(t)) tables.add(t);
                }
            }

            for (String table : tables) {
                writer.write("DROP TABLE IF EXISTS `" + table + "`;");
                writer.newLine();
                try (Statement st = conn.createStatement();
                     java.sql.ResultSet rs = st.executeQuery("SHOW CREATE TABLE `" + table + "`")) {
                    if (rs.next()) {
                        writer.write(rs.getString(2) + ";");
                        writer.newLine();
                        writer.newLine();
                    }
                }

                List<String> columns = new ArrayList<>();
                try (Statement st = conn.createStatement();
                     java.sql.ResultSet rs = st.executeQuery("SELECT * FROM `" + table + "`")) {
                    java.sql.ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();
                    for (int i = 1; i <= colCount; i++) columns.add(meta.getColumnName(i));

                    while (rs.next()) {
                        writer.write("INSERT INTO `" + table + "` (");
                        writer.write(String.join(", ", columns.stream().map(c -> "`" + c + "`").toArray(String[]::new)));
                        writer.write(") VALUES (");
                        for (int i = 1; i <= colCount; i++) {
                            if (i > 1) writer.write(", ");
                            String val = rs.getString(i);
                            if (val == null) {
                                writer.write("NULL");
                            } else {
                                writer.write("'" + val.replace("\\", "\\\\").replace("'", "\\'") + "'");
                            }
                        }
                        writer.write(");");
                        writer.newLine();
                    }
                }
                writer.newLine();
            }

            writer.write("SET FOREIGN_KEY_CHECKS = 1;");
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            log.error("[Restore] 应急备份失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getRestoreDetail(Long id) {
        return restoreRecordMapper.selectById(id);
    }
}
