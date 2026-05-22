-- ============================================================
-- 系统配置模块 - 数据库迁移脚本
-- ============================================================

CREATE TABLE IF NOT EXISTS system_configs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(30) NOT NULL DEFAULT 'string' COMMENT '值类型: string, number, boolean, json',
    config_group VARCHAR(50) NOT NULL DEFAULT 'general' COMMENT '配置分组: general-通用, security-安全, file-文件, notification-通知, crawler-爬取',
    description VARCHAR(300) COMMENT '配置说明',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    editable TINYINT NOT NULL DEFAULT 1 COMMENT '是否可编辑',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS datasource_configs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ds_name VARCHAR(100) NOT NULL COMMENT '数据源名称',
    ds_key VARCHAR(50) NOT NULL UNIQUE COMMENT '数据源标识',
    ds_type VARCHAR(30) NOT NULL DEFAULT 'mysql' COMMENT '数据源类型: mysql, postgresql, api, file',
    host VARCHAR(200) NOT NULL COMMENT '主机地址',
    port INT NOT NULL COMMENT '端口',
    database_name VARCHAR(100) COMMENT '数据库名',
    username VARCHAR(100) COMMENT '用户名',
    password_encrypted VARCHAR(500) COMMENT '加密密码',
    extra_params TEXT COMMENT '额外连接参数JSON',
    max_pool_size INT NOT NULL DEFAULT 10 COMMENT '最大连接池',
    status VARCHAR(20) NOT NULL DEFAULT 'untested' COMMENT '状态: untested, connected, failed',
    last_test_time DATETIME COMMENT '最后测试时间',
    last_test_result VARCHAR(100) COMMENT '最后测试结果',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ds_key (ds_key),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源连接配置表';

INSERT INTO system_configs (config_key, config_name, config_value, config_type, config_group, description, sort_order) VALUES
('site_name', '站点名称', 'RelicAdmin文物管理系统', 'string', 'general', '系统站点显示名称', 1),
('site_description', '站点描述', '综合性文物管理和数据采集平台', 'string', 'general', '系统站点描述', 2),
('page_size', '默认分页大小', '10', 'number', 'general', '列表查询默认分页条数', 3),
('max_upload_size', '最大上传大小(MB)', '10', 'number', 'file', '文件上传大小限制', 10),
('allowed_file_types', '允许上传的文件类型', '["jpg","jpeg","png","gif","webp"]', 'json', 'file', '允许上传的图片格式', 11),
('log_level', '日志级别', 'INFO', 'string', 'general', '系统日志输出级别: DEBUG, INFO, WARN, ERROR', 4),
('enable_registration', '开放注册', 'true', 'boolean', 'security', '是否允许新用户注册', 20),
('enable_notification', '启用通知', 'true', 'boolean', 'notification', '是否启用系统通知功能', 30),
('crawler_global_timeout', '爬取全局超时(秒)', '300', 'number', 'crawler', '爬取任务全局默认超时', 40),
('crawler_user_agent', '爬取User-Agent', 'Mozilla/5.0 (compatible; RelicCrawler/1.0)', 'string', 'crawler', '爬虫请求头User-Agent', 41);