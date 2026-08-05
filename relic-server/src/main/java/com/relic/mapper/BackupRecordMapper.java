package com.relic.mapper;

import com.relic.entity.BackupRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BackupRecordMapper {
    List<Map<String, Object>> selectByPage(@Param("status") Integer status,
                                           @Param("backupType") String backupType,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("status") Integer status,
                     @Param("backupType") String backupType);

    int insert(BackupRecord backupRecord);

    Map<String, Object> selectById(@Param("id") Long id);

    /** M-17：按源备份 ID（scope=sourceBackupId=x）查询应急备份记录，按创建时间倒序 */
    List<Map<String, Object>> selectBySourceBackupId(@Param("sourceBackupId") Long sourceBackupId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("fileSize") Long fileSize,
                     @Param("filePath") String filePath,
                     @Param("remark") String remark);

    int deleteById(@Param("id") Long id);

    List<Map<String, Object>> selectExpired(@Param("retentionDays") int retentionDays);

    int deleteByIds(@Param("ids") List<Long> ids);

    long sumFileSize();
}