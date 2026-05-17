package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface BackupStrategyMapper {
    Map<String, Object> selectCurrent();

    int update(@Param("id") Integer id,
               @Param("autoBackupEnabled") Integer autoBackupEnabled,
               @Param("backupCron") String backupCron,
               @Param("backupType") String backupType,
               @Param("retentionDays") Integer retentionDays,
               @Param("encryptEnabled") Integer encryptEnabled,
               @Param("notifyOnFailure") Integer notifyOnFailure,
               @Param("storagePath") String storagePath,
               @Param("storageWarningThreshold") Long storageWarningThreshold,
               @Param("status") Integer status);
}