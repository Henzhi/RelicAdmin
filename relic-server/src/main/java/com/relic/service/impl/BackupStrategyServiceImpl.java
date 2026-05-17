package com.relic.service.impl;

import com.relic.dto.BackupStrategyUpdateDTO;
import com.relic.mapper.BackupStrategyMapper;
import com.relic.service.BackupStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupStrategyServiceImpl implements BackupStrategyService {

    private final BackupStrategyMapper backupStrategyMapper;

    @Override
    public Map<String, Object> getStrategy() {
        return backupStrategyMapper.selectCurrent();
    }

    @Override
    public void updateStrategy(Integer id, BackupStrategyUpdateDTO dto) {
        backupStrategyMapper.update(
                id,
                dto.getAutoBackupEnabled() != null ? dto.getAutoBackupEnabled() : 0,
                dto.getBackupCron() != null ? dto.getBackupCron() : "0 0 2 * * ?",
                dto.getBackupType() != null ? dto.getBackupType() : "full",
                dto.getRetentionDays() != null ? dto.getRetentionDays() : 30,
                dto.getEncryptEnabled() != null ? dto.getEncryptEnabled() : 1,
                dto.getNotifyOnFailure() != null ? dto.getNotifyOnFailure() : 1,
                dto.getStoragePath() != null ? dto.getStoragePath() : "./backups",
                dto.getStorageWarningThreshold() != null ? dto.getStorageWarningThreshold() : 10737418240L,
                dto.getStatus() != null ? dto.getStatus() : 1
        );
        log.info("备份策略 id={} 已更新", id);
    }
}