package com.relic.service.impl;

import com.relic.annotation.RequireRole;
import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.dto.RestoreConfirmDTO;
import com.relic.entity.AdminUser;
import com.relic.entity.BackupRecord;
import com.relic.mapper.AdminUserMapper;
import com.relic.mapper.BackupRecordMapper;
import com.relic.mapper.RestoreRecordMapper;
import com.relic.service.RestoreService;
import com.relic.utils.BackupCryptoUtil;
import com.relic.utils.SqlExportUtil;
import com.relic.vo.PageQuery;
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
import java.sql.SQLException;
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
    private final BackupCryptoUtil backupCryptoUtil;

    @Override
    public PageResultVO<Map<String, Object>> page(Integer status, int page, int pageSize) {
        PageQuery pq = PageQuery.of(page, pageSize);
        List<Map<String, Object>> records = restoreRecordMapper.selectByPage(status, pq.getOffset(), pq.getPageSize());
        long total = restoreRecordMapper.countByPage(status);
        return pq.toResult(total, records);
    }

    @Override
    @RequireRole(RoleConstant.SUPER_ADMIN)
    public Map<String, Object> restore(Long backupId, RestoreConfirmDTO dto) {
        // H-04：仅超级管理员可执行数据恢复（会重建全库）
        Map<String, Object> backup = backupRecordMapper.selectById(backupId);
        if (backup == null) {
            throw new IllegalArgumentException("备份记录不存在");
        }
        Object backupStatus = backup.get("status");
        if (backupStatus == null || !Integer.valueOf(1).equals(backupStatus)) {
            throw new IllegalArgumentException("只能从已完成状态的备份进行恢复");
        }

        Integer operatorId = getCurrentOperatorId();
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
            long emergencySize = exportCurrentToFile(emergencyPath);
            // M-17：应急备份登记入库，失败后可一键还原
            Long emergencyBackupId = registerEmergencyBackup(backupId, backupName, operatorId, emergencyPath, emergencySize);
            log.info("[Restore] 恢复前应急备份已保存并登记: {} (backupRecordId={})", emergencyPath, emergencyBackupId);

            File sqlFile = new File(filePath);
            if (!sqlFile.exists()) {
                throw new IllegalArgumentException("备份文件不存在: " + filePath);
            }
            // M-12：若备份为加密格式，先解密到临时文件再执行
            if (backupCryptoUtil.isEncrypted(sqlFile)) {
                File decrypted = backupCryptoUtil.decrypt(sqlFile);
                if (!backupCryptoUtil.isEncrypted(decrypted)) {
                    sqlFile = decrypted;
                } else {
                    throw new IllegalArgumentException("备份文件解密失败：请确认 BACKUP_ENCRYPT_KEY 配置正确");
                }
            }

            // M-11：恢复前记录关键表行数，用于恢复后一致性校验
            Map<String, Long> beforeCounts = countCriticalTables();

            long executedStatements = executeSqlFile(sqlFile);

            // M-11：恢复后校验关键表行数是否正常（非空校验，防止备份为空时恢复出空库）
            Map<String, Long> afterCounts = countCriticalTables();
            String validation = validateAfterRestore(beforeCounts, afterCounts);

            String remark = "恢复成功，共处理 " + executedStatements + " 行数据。应急备份: " + emergencyPath
                    + "。恢复后校验: " + validation;
            Long restoreRecordId = insertFinalRecord(backupId, backupName, operatorId, 1, remark);
            result.put("status", "success");
            result.put("restoredRows", executedStatements);
            result.put("emergencyBackupId", emergencyBackupId);
            log.info("[Restore] 数据恢复成功: restoreId={}, backupId={}, 行数={}, 校验={}",
                    restoreRecordId, backupId, executedStatements, validation);

        } catch (Exception e) {
            log.error("[Restore] 数据恢复失败: backupId={}, reason={}", backupId, e.getMessage(), e);
            String remark = "恢复失败: " + e.getMessage();
            Long restoreRecordId = insertFinalRecord(backupId, backupName, operatorId, 2, remark);
            log.info("[Restore] 失败记录已写入: restoreId={}", restoreRecordId);
            result.put("status", "failed");
            // M-17：失败时返回应急备份 ID，前端可一键用应急备份还原
            Long emergencyBackupId = findEmergencyBackupId(backupId);
            if (emergencyBackupId != null) {
                result.put("emergencyBackupId", emergencyBackupId);
                result.put("emergencyHint", "恢复过程中出现问题，请使用应急备份还原（backupId="
                        + emergencyBackupId + "）恢复到恢复前的状态");
            }
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

    long executeSqlFile(File sqlFile) throws Exception {
        log.info("[Restore] 开始恢复 SQL 文件: {}", sqlFile.getAbsolutePath());
        StringBuilder sql = new StringBuilder();
        long lineCount = 0;
        long executedCount = 0;
        long failedCount = 0;
        StringBuilder failedDetails = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8));
             Connection conn = dataSource.getConnection()) {

            // 注意：备份文件含 DROP/CREATE TABLE（DDL），MySQL 中 DDL 会隐式提交事务，
            // 因此"全量回滚"在跨表场景下不成立。DML 仍置于事务内批量执行以尽量保证原子性，
            // 失败时如实提示依赖 before_restore 应急备份回滚。
            conn.setAutoCommit(false);

            String line;
            try (Statement st = conn.createStatement()) {
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    sql.append(line).append("\n");

                    if (trimmed.endsWith(";")) {
                        String statement = trimStatement(sql.toString());
                        sql.setLength(0);

                        if (statement.isEmpty() || statement.equals(";")) continue;

                        if (isDDL(statement)) {
                            // DDL 单独执行（隐式提交会打断事务与 batch），先 flush 已有 DML
                            failedCount += flushBatchSafely(st, failedDetails);
                            try {
                                st.execute(statement);
                                executedCount++;
                            } catch (Exception e) {
                                failedCount++;
                                if (failedDetails.length() > 0) failedDetails.append("; ");
                                failedDetails.append("第").append(lineCount).append("行: ").append(e.getMessage());
                                log.warn("[Restore] DDL 执行失败 (第{}行): {}", lineCount, e.getMessage());
                            }
                        } else {
                            // DML 加入批量，每 200 条执行一次
                            st.addBatch(statement);
                            executedCount++;
                            if (executedCount % BATCH_SIZE == 0) {
                                failedCount += flushBatchSafely(st, failedDetails);
                            }
                        }
                    }
                }

                String remaining = trimStatement(sql.toString());
                if (!remaining.isEmpty() && !remaining.equals(";")) {
                    if (isDDL(remaining)) {
                        failedCount += flushBatchSafely(st, failedDetails);
                        try {
                            st.execute(remaining);
                            executedCount++;
                        } catch (Exception e) {
                            failedCount++;
                            failedDetails.append("末尾语句: ").append(e.getMessage());
                            log.warn("[Restore] SQL 执行失败 (末尾语句): {}", e.getMessage());
                        }
                    } else {
                        st.addBatch(remaining);
                        executedCount++;
                    }
                }
                failedCount += flushBatchSafely(st, failedDetails);
            }

            if (failedCount > 0) {
                conn.rollback();
                log.error("[Restore] SQL 恢复存在 {} 条失败语句，事务已回滚。注意：因备份含 DDL（隐式提交），"
                        + "部分表可能已重建，如需还原请使用 before_restore 应急备份: {}", failedCount, failedDetails);
                throw new RuntimeException("SQL 恢复失败，共 " + failedCount + " 条语句执行失败，已回滚。"
                        + "注意：因备份含 DDL（隐式提交），部分表可能已重建，请使用 before_restore 应急备份还原。详情: " + failedDetails);
            }

            conn.commit();
            log.info("[Restore] SQL 恢复完成: {} 行, 执行 {} 条语句", lineCount, executedCount);
            return executedCount;
        }
    }

    private long exportCurrentToFile(String outputFile) {
        try (Connection conn = dataSource.getConnection();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            String header = "-- RelicAdmin 恢复前应急备份\n"
                    + "-- 时间: " + java.time.LocalDateTime.now() + "\n";
            // 流式导出：避免大表全量加载内存导致 OOM
            SqlExportUtil.exportAllTables(conn, writer, header);
            File file = new File(outputFile);
            return file.exists() ? file.length() : 0L;
        } catch (Exception e) {
            log.error("[Restore] 应急备份失败: {}", e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * M-17：将恢复前应急备份登记到 backup_records（type=emergency），
     * 使管理界面可见、可下载、可一键还原。
     *
     * @return 应急备份在 backup_records 中的 ID；登记失败返回 null
     */
    private Long registerEmergencyBackup(Long sourceBackupId, String sourceBackupName,
                                         Integer operatorId, String emergencyPath, long fileSize) {
        try {
            BackupRecord record = new BackupRecord();
            record.setBackupName("应急备份(恢复前)-" + sourceBackupName);
            record.setBackupType("emergency");
            record.setScope("sourceBackupId=" + sourceBackupId);
            record.setFilePath(emergencyPath);
            record.setFileSize(fileSize);
            record.setOperatorId(operatorId);
            record.setStatus(1);
            record.setRemark("数据恢复前自动生成的应急备份，用于恢复失败时一键还原");
            backupRecordMapper.insert(record);
            return record.getId();
        } catch (Exception e) {
            log.error("[Restore] 应急备份登记失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /** M-17：按源备份 ID 查找最近的应急备份记录 */
    private Long findEmergencyBackupId(Long sourceBackupId) {
        try {
            List<Map<String, Object>> list = backupRecordMapper.selectBySourceBackupId(sourceBackupId);
            return (list != null && !list.isEmpty()) ? ((Number) list.get(0).get("id")).longValue() : null;
        } catch (Exception e) {
            log.error("[Restore] 查询应急备份记录失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /** M-11：需要做恢复一致性校验的关键业务表 */
    private static final List<String> CRITICAL_TABLES = Arrays.asList(
            "admin_users", "users", "artifacts", "announcements");

    /**
     * M-11：统计关键业务表行数
     */
    private Map<String, Long> countCriticalTables() {
        Map<String, Long> counts = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            for (String table : CRITICAL_TABLES) {
                try (Statement st = conn.createStatement();
                     java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM `" + table + "`")) {
                    if (rs.next()) {
                        counts.put(table, rs.getLong(1));
                    }
                } catch (Exception ignored) {
                    // 表不存在时跳过（恢复脚本可能未建该表）
                }
            }
        } catch (Exception e) {
            log.warn("[Restore] 恢复前/后统计关键表行数失败: {}", e.getMessage());
        }
        return counts;
    }

    /**
     * M-11：恢复后校验：关键表不应为空（恢复前有数据的关键表）
     */
    private String validateAfterRestore(Map<String, Long> before, Map<String, Long> after) {
        List<String> problems = new ArrayList<>();
        for (String table : CRITICAL_TABLES) {
            Long beforeCount = before.get(table);
            Long afterCount = after.get(table);
            if (beforeCount != null && beforeCount > 0 && (afterCount == null || afterCount == 0)) {
                problems.add(table + " 恢复后为空（恢复前=" + beforeCount + "）");
            }
        }
        if (problems.isEmpty()) {
            return "关键表行数校验通过";
        }
        String detail = String.join("; ", problems);
        log.error("[Restore] 恢复后校验未通过: {}", detail);
        return "存在异常: " + detail;
    }

    /** DML 批量执行批大小 */
    private static final int BATCH_SIZE = 200;

    /** 去除语句首尾空白与结尾分号 */
    private String trimStatement(String raw) {
        String s = raw.trim();
        while (s.endsWith(";")) {
            s = s.substring(0, s.length() - 1).trim();
        }
        return s;
    }

    /** 判断语句是否为 DDL（MySQL 中 DDL 会隐式提交事务，需单独执行） */
    private boolean isDDL(String statement) {
        String upper = statement.toUpperCase().trim();
        return upper.startsWith("DROP")
                || upper.startsWith("CREATE")
                || upper.startsWith("ALTER")
                || upper.startsWith("TRUNCATE")
                || upper.startsWith("RENAME");
    }

    /**
     * 执行当前 Statement 中累积的 DML 批量。
     *
     * @return 本批次失败语句数（0 表示全部成功）
     */
    private int flushBatchSafely(Statement st, StringBuilder failedDetails) {
        try {
            int[] results = st.executeBatch();
            int failed = 0;
            if (results != null) {
                for (int r : results) {
                    if (r == Statement.EXECUTE_FAILED) {
                        failed++;
                    }
                }
            }
            if (failed > 0) {
                if (failedDetails.length() > 0) failedDetails.append("; ");
                failedDetails.append("批量执行中有 ").append(failed).append(" 条语句失败");
                log.warn("[Restore] 批量执行存在 {} 条失败语句", failed);
            }
            return failed;
        } catch (SQLException e) {
            if (failedDetails.length() > 0) failedDetails.append("; ");
            failedDetails.append("批量执行失败: ").append(e.getMessage());
            log.warn("[Restore] 批量执行失败: {}", e.getMessage());
            return 1;
        } finally {
            try {
                st.clearBatch();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取当前操作人 ID，未登录时抛出业务异常，避免拆箱 NPE
     */
    private Integer getCurrentOperatorId() {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            throw new RuntimeException("当前操作人未登录");
        }
        return currentId.intValue();
    }

    @Override
    public Map<String, Object> getRestoreDetail(Long id) {
        return restoreRecordMapper.selectById(id);
    }
}
