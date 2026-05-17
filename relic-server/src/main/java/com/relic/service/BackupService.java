package com.relic.service;

import com.relic.dto.BackupCreateDTO;
import com.relic.vo.PageResultVO;

import java.util.Map;

public interface BackupService {
    PageResultVO<Map<String, Object>> page(Integer status, String backupType, int page, int pageSize);
    Map<String, Object> createBackup(BackupCreateDTO dto);
    Map<String, Object> getBackupDetail(Long id);
    void deleteBackup(Long id);
    void cleanupExpiredBackups();
    long getStorageUsage();
}