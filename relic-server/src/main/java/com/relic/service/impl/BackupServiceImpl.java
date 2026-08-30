package com.relic.service.impl;

import com.relic.annotation.RequireRole;
import com.relic.constant.RoleConstant;
import com.relic.context.BaseContext;
import com.relic.dto.BackupCreateDTO;
import com.relic.entity.BackupRecord;
import com.relic.mapper.BackupRecordMapper;
import com.relic.mapper.BackupStrategyMapper;
import com.relic.service.BackupService;
import com.relic.utils.BackupCryptoUtil;
import com.relic.websocket.WebSocketServer;
import com.relic.utils.SqlExportUtil;
import com.relic.vo.PageQuery;
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
    private final WebSocketServer webSocketServer;
    private final BackupCryptoUtil backupCryptoUtil;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public PageResultVO<Map<String, Object>> page(Integer status, String backupType, int page, int pageSize) {
        PageQuery pq = PageQuery.of(page, pageSize);
        List<Map<String, Object>> records = backupRecordMapper.selectByPage(status, backupType, pq.getOffset(), pq.getPageSize());
        long total = backupRecordMapper.countByPage(status, backupType);
        return pq.toResult(total, records);
    }

    @Override
    @RequireRole(RoleConstant.SUPER_ADMIN)
    public Map<String, Object> createBackup(BackupCreateDTO dto) {
        // H-04：仅超级管理员可手动创建备份
        Integer operatorId = getCurrentOperatorId();
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
            // M-12：按策略 encrypt_enabled 与密钥可用性决定是否加密备份文件
            boolean encryptEnabled = strategy != null
                    && strategy.get("encryptEnabled") != null
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
                backupRecordMapper.updateStatus(backupId, 1, fileSize, storedFilePath, "备份完成");
                result.put("status", 1);
                result.put("fileSize", fileSize);
                log.info("备份 {} 完成, {} bytes, path={}", backupName, fileSize, storedFilePath);
                webSocketServer.sendEvent("backup", java.util.Map.of(
                        "backupId", backupId, "backupName", backupName, "result", "success"));
            } else {
                backupRecordMapper.updateStatus(backupId, 2, 0L, storedFilePath, "备份失败");
                result.put("status", "failed");
                webSocketServer.sendEvent("backup", java.util.Map.of(
                        "backupId", backupId, "backupName", backupName, "result", "failed"));
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

            String header = "-- RelicAdmin 数据库备份\n"
                    + "-- 导出时间: " + LocalDateTime.now() + "\n";
            // 流式导出：避免大表全量加载内存导致 OOM
            SqlExportUtil.exportAllTables(conn, writer, header);
            return true;
        } catch (Exception e) {
            log.error("JDBC导出失败: {}", e.getMessage(), e);
            return false;
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
    public Map<String, Object> getBackupDetail(Long id) {
        return backupRecordMapper.selectById(id);
    }

    @Override
    @RequireRole(RoleConstant.SUPER_ADMIN)
    public void deleteBackup(Long id) {
        // H-04：仅超级管理员可删除备份
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

    @Override
    public String getBackupRoot() {
        Map<String, Object> strategy = backupStrategyMapper.selectCurrent();
        String storagePath = strategy != null ? (String) strategy.get("storagePath") : null;
        return storagePath != null && !storagePath.isBlank() ? storagePath : "./backups";
    }
}