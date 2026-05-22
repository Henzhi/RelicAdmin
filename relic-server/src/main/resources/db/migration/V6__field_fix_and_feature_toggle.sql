-- ============================================================
-- V6: 修复Sql-create.md手动建表与Flyway迁移的字段差异
--     + 爬取任务日志表 + 功能开关种子数据
-- ============================================================

DROP PROCEDURE IF EXISTS v6_add_col;

CREATE PROCEDURE v6_add_col(IN tbl VARCHAR(64), IN col VARCHAR(64), IN col_def TEXT)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = tbl AND COLUMN_NAME = col
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', tbl, '` ADD COLUMN `', col, '` ', col_def);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;

-- ============================================================
-- 1. 修复 system_configs 字段缺失
--    Sql-create.md 版: id, config_key, config_value, description, updated_at, updated_by
--    V5版需补充: config_name, config_type, config_group, sort_order, editable, status, created_at
-- ============================================================
CALL v6_add_col('system_configs', 'config_name', "VARCHAR(100) NOT NULL DEFAULT '' COMMENT '配置名称' AFTER config_key");
CALL v6_add_col('system_configs', 'config_type', "VARCHAR(30) NOT NULL DEFAULT 'string' COMMENT '值类型: string/number/boolean/json' AFTER config_name");
CALL v6_add_col('system_configs', 'config_group', "VARCHAR(50) NOT NULL DEFAULT 'general' COMMENT '配置分组' AFTER config_type");
CALL v6_add_col('system_configs', 'sort_order', "INT NOT NULL DEFAULT 0 COMMENT '排序' AFTER description");
CALL v6_add_col('system_configs', 'editable', "TINYINT NOT NULL DEFAULT 1 COMMENT '是否可编辑' AFTER sort_order");
CALL v6_add_col('system_configs', 'status', "TINYINT NOT NULL DEFAULT 1 COMMENT '状态' AFTER editable");
CALL v6_add_col('system_configs', 'created_at', "DATETIME COMMENT '创建时间' AFTER status");

-- 补充 system_configs 已有记录的 config_name（用config_key兜底确保NOT NULL）
UPDATE system_configs SET config_name = config_key WHERE config_name = '';
-- 补充创建时间
UPDATE system_configs SET created_at = COALESCE(updated_at, NOW()) WHERE created_at IS NULL;

-- 新增 config_group 索引
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'system_configs' AND INDEX_NAME = 'idx_config_group');
SET @sql = IF(@idx_exists = 0,
    'ALTER TABLE system_configs ADD INDEX idx_config_group (config_group)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 2. 修复 crawl_tasks 字段缺失
--    Sql-create.md 版字段: id, museum_id, start_time, end_time, status, items_crawled, items_new, items_updated, error_message
--    V4版字段: id, task_name, task_code, source_url, source_type, crawl_rule, priority...
--    旧字段与新产品形态不兼容，直接添加新列 + 删除旧列
-- ============================================================
CALL v6_add_col('crawl_tasks', 'task_name', "VARCHAR(100) NOT NULL DEFAULT '' COMMENT '任务名称' AFTER id");
CALL v6_add_col('crawl_tasks', 'task_code', "VARCHAR(50) NOT NULL DEFAULT '' COMMENT '任务编码' AFTER task_name");
CALL v6_add_col('crawl_tasks', 'source_url', "VARCHAR(500) NOT NULL DEFAULT '' COMMENT '数据源URL' AFTER task_code");
CALL v6_add_col('crawl_tasks', 'source_type', "VARCHAR(30) NOT NULL DEFAULT 'web' COMMENT '数据源类型' AFTER source_url");
CALL v6_add_col('crawl_tasks', 'crawl_rule', "TEXT COMMENT '爬取规则JSON' AFTER source_type");
CALL v6_add_col('crawl_tasks', 'priority', "INT NOT NULL DEFAULT 1 COMMENT '优先级' AFTER crawl_rule");
CALL v6_add_col('crawl_tasks', 'cron_expression', "VARCHAR(100) COMMENT '定时表达式' AFTER priority");
CALL v6_add_col('crawl_tasks', 'max_retry', "INT NOT NULL DEFAULT 3 COMMENT '最大重试次数' AFTER cron_expression");
CALL v6_add_col('crawl_tasks', 'retry_delay', "INT NOT NULL DEFAULT 60 COMMENT '重试间隔(秒)' AFTER max_retry");
CALL v6_add_col('crawl_tasks', 'timeout_seconds', "INT NOT NULL DEFAULT 300 COMMENT '超时时间(秒)' AFTER retry_delay");
CALL v6_add_col('crawl_tasks', 'last_run_time', "DATETIME COMMENT '最后执行时间' AFTER timeout_seconds");
CALL v6_add_col('crawl_tasks', 'next_run_time', "DATETIME COMMENT '下次执行时间' AFTER last_run_time");
CALL v6_add_col('crawl_tasks', 'total_runs', "INT NOT NULL DEFAULT 0 COMMENT '总执行次数' AFTER next_run_time");
CALL v6_add_col('crawl_tasks', 'success_runs', "INT NOT NULL DEFAULT 0 COMMENT '成功次数' AFTER total_runs");
CALL v6_add_col('crawl_tasks', 'fail_runs', "INT NOT NULL DEFAULT 0 COMMENT '失败次数' AFTER success_runs");
CALL v6_add_col('crawl_tasks', 'description', "VARCHAR(500) COMMENT '任务描述' AFTER fail_runs");
CALL v6_add_col('crawl_tasks', 'enabled', "TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用' AFTER description");
CALL v6_add_col('crawl_tasks', 'created_by', "INT COMMENT '创建者ID' AFTER enabled");
CALL v6_add_col('crawl_tasks', 'created_at', "DATETIME COMMENT '创建时间' AFTER created_by");
CALL v6_add_col('crawl_tasks', 'updated_at', "DATETIME COMMENT '更新时间' AFTER created_at");

-- 删除Sql-create.md的旧字段
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS museum_id;
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS start_time;
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS end_time;
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS items_crawled;
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS items_new;
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS items_updated;
ALTER TABLE crawl_tasks DROP COLUMN IF EXISTS error_message;

-- 修改status列兼容新取值
ALTER TABLE crawl_tasks MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'idle'
    COMMENT '状态: idle-空闲, running-运行中, paused-已暂停, completed-已完成, failed-失败';

-- 补充索引
SET @idx1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crawl_tasks' AND INDEX_NAME = 'idx_task_code');
SET @sql = IF(@idx1 = 0, 'ALTER TABLE crawl_tasks ADD UNIQUE INDEX idx_task_code (task_code)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx2 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crawl_tasks' AND INDEX_NAME = 'idx_status');
SET @sql = IF(@idx2 = 0, 'ALTER TABLE crawl_tasks ADD INDEX idx_status (status)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crawl_tasks' AND INDEX_NAME = 'idx_enabled');
SET @sql = IF(@idx3 = 0, 'ALTER TABLE crawl_tasks ADD INDEX idx_enabled (enabled)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx4 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crawl_tasks' AND INDEX_NAME = 'idx_next_run_time');
SET @sql = IF(@idx4 = 0, 'ALTER TABLE crawl_tasks ADD INDEX idx_next_run_time (next_run_time)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 补充created_at默认值
UPDATE crawl_tasks SET created_at = NOW(), updated_at = NOW() WHERE created_at IS NULL;

-- ============================================================
-- 3. 创建 crawl_task_logs（Sql-create.md中没有）
-- ============================================================
CREATE TABLE IF NOT EXISTS crawl_task_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL COMMENT '任务ID',
    task_name VARCHAR(100) COMMENT '任务名称(冗余)',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status VARCHAR(20) NOT NULL DEFAULT 'running' COMMENT '执行状态: running, success, failed',
    crawled_count INT NOT NULL DEFAULT 0 COMMENT '爬取数量',
    error_message TEXT COMMENT '错误信息',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '当前重试次数',
    executor_id INT COMMENT '执行者ID(0=系统)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    FOREIGN KEY (task_id) REFERENCES crawl_tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爬取任务执行日志';

-- ============================================================
-- 4. 插入功能开关种子数据（config_group='feature_toggle'）
-- ============================================================
INSERT IGNORE INTO system_configs (config_key, config_name, config_value, config_type, config_group, description, sort_order, status, created_at) VALUES
('feat_user_registration', '开放用户注册', 'true', 'boolean', 'feature_toggle', '控制前台用户注册功能的开启/关闭', 1, 1, NOW()),
('feat_user_comment', '用户评论功能', 'true', 'boolean', 'feature_toggle', '控制用户发表评论功能的开启/关闭', 2, 1, NOW()),
('feat_user_upload', '用户上传功能', 'true', 'boolean', 'feature_toggle', '控制在Web/App端使用上传功能', 3, 1, NOW()),
('feat_user_post', '用户动态发布', 'true', 'boolean', 'feature_toggle', '控制用户发布动态功能的开启/关闭', 4, 1, NOW()),
('feat_user_browse', '文物浏览功能', 'true', 'boolean', 'feature_toggle', '控制前台文物浏览功能的开启/关闭', 5, 1, NOW()),
('feat_auto_audit', '自动审核功能', 'true', 'boolean', 'feature_toggle', '控制自动审核功能的开启/关闭', 6, 1, NOW()),
('feat_notify_push', '消息推送功能', 'true', 'boolean', 'feature_toggle', '控制系统通知推送功能的开启/关闭', 7, 1, NOW()),
('feat_search_recommend', '搜索推荐功能', 'true', 'boolean', 'feature_toggle', '控制搜索与推荐功能的开启/关闭', 8, 1, NOW()),
('feat_maintenance_mode', '维护模式', 'false', 'boolean', 'feature_toggle', '开启后前台显示维护页面，管理员仍可正常访问后台', 9, 1, NOW());

-- ============================================================
-- 5. 补充V4种子数据（仅当表中无数据时）
-- ============================================================
SET @cnt = (SELECT COUNT(*) FROM crawl_tasks);
SET @sql = IF(@cnt = 0,
    "INSERT INTO crawl_tasks (task_name, task_code, source_url, source_type, priority, cron_expression, max_retry, retry_delay, description, status, created_at) VALUES ('文物资讯爬取', 'relic_news', 'https://example.com/relic-news', 'web', 5, '0 0 9 * * ?', 3, 120, '每日爬取文物相关新闻资讯', 'idle', NOW()), ('博物馆展览信息', 'museum_exhibits', 'https://example.com/museum-api/exhibits', 'api', 10, '0 0 10 * * ?', 2, 60, '定时获取博物馆最新展览信息', 'idle', NOW())",
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 6. 补充V5种子数据（system_configs 缺少记录时）
-- ============================================================
INSERT IGNORE INTO system_configs (config_key, config_name, config_value, config_type, config_group, description, sort_order, status, created_at) VALUES
('site_name', '站点名称', 'RelicAdmin文物管理系统', 'string', 'general', '系统站点显示名称', 1, 1, NOW()),
('site_description', '站点描述', '综合性文物管理和数据采集平台', 'string', 'general', '系统站点描述', 2, 1, NOW()),
('page_size', '默认分页大小', '10', 'number', 'general', '列表查询默认分页条数', 3, 1, NOW()),
('max_upload_size', '最大上传大小(MB)', '10', 'number', 'file', '文件上传大小限制', 10, 1, NOW()),
('allowed_file_types', '允许上传的文件类型', '["jpg","jpeg","png","gif","webp"]', 'json', 'file', '允许上传的图片格式', 11, 1, NOW()),
('log_level', '日志级别', 'INFO', 'string', 'general', '系统日志输出级别: DEBUG, INFO, WARN, ERROR', 4, 1, NOW()),
('enable_registration', '开放注册', 'true', 'boolean', 'security', '是否允许新用户注册', 20, 1, NOW()),
('enable_notification', '启用通知', 'true', 'boolean', 'notification', '是否启用系统通知功能', 30, 1, NOW()),
('crawler_global_timeout', '爬取全局超时(秒)', '300', 'number', 'crawler', '爬取任务全局默认超时', 40, 1, NOW()),
('crawler_user_agent', '爬取User-Agent', 'Mozilla/5.0 (compatible; RelicCrawler/1.0)', 'string', 'crawler', '爬虫请求头User-Agent', 41, 1, NOW());

-- ============================================================
-- 7. 清理临时存储过程
-- ============================================================
DROP PROCEDURE v6_add_col;