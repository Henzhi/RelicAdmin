-- ============================================================
-- 备份策略配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS backup_strategies (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    auto_backup_enabled TINYINT DEFAULT 0 COMMENT '是否开启自动备份：1-是 0-否',
    backup_cron VARCHAR(50) DEFAULT '0 0 2 * * ?' COMMENT 'Cron表达式，默认每日凌晨2点',
    backup_type VARCHAR(20) DEFAULT 'full' COMMENT '默认备份类型：full/incremental/export',
    retention_days INT DEFAULT 30 COMMENT '备份保留天数',
    encrypt_enabled TINYINT DEFAULT 1 COMMENT '是否加密存储：1-是 0-否',
    notify_on_failure TINYINT DEFAULT 1 COMMENT '失败是否通知：1-是 0-否',
    storage_path VARCHAR(500) DEFAULT './backups' COMMENT '备份文件存储路径',
    storage_warning_threshold BIGINT DEFAULT 10737418240 COMMENT '存储告警阈值（字节），默认10GB',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间'
) COMMENT='备份策略配置表';

INSERT IGNORE INTO backup_strategies (auto_backup_enabled, backup_cron, backup_type, retention_days, encrypt_enabled, notify_on_failure, storage_path, storage_warning_threshold, status, created_at)
VALUES (0, '0 0 2 * * ?', 'full', 30, 1, 1, './backups', 10737418240, 1, NOW());

-- ============================================================
-- 数据恢复记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS restore_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    backup_id BIGINT UNSIGNED NOT NULL COMMENT '关联的备份记录ID',
    backup_name VARCHAR(200) COMMENT '备份名称快照',
    operator_id INT UNSIGNED NOT NULL COMMENT '操作人ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0-进行中 1-成功 2-失败',
    remark TEXT COMMENT '备注/失败原因',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_backup_id (backup_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_created_at (created_at)
) COMMENT='数据恢复记录表';