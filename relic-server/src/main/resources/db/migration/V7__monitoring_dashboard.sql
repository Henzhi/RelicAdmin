-- ============================================================
-- V7: 系统监控看板 - 异常告警表
-- ============================================================

CREATE TABLE IF NOT EXISTS system_alerts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    alert_type VARCHAR(50) NOT NULL COMMENT '告警类型: interface_timeout/db_failure/disk_full/high_memory/cpu_overload/service_error',
    severity VARCHAR(20) NOT NULL DEFAULT 'warning' COMMENT '严重级别: info/warning/critical',
    title VARCHAR(200) NOT NULL COMMENT '告警标题',
    message TEXT COMMENT '告警详情',
    source VARCHAR(100) COMMENT '告警来源(服务名/模块名)',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-已激活, resolved-已解决, ignored-已忽略',
    resolved_by INT COMMENT '处理人ID',
    resolved_at DATETIME COMMENT '处理时间',
    resolve_remark VARCHAR(500) COMMENT '处理备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_alert_type (alert_type),
    INDEX idx_severity (severity),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统告警记录';

INSERT IGNORE INTO system_alerts (alert_type, severity, title, message, source, status, created_at) VALUES
('service_error', 'info', '系统监控已启用', '系统监控看板V7已部署，告警系统正常运行中', 'monitoring', 'resolved', NOW());