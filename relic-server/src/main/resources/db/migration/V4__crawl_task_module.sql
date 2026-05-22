-- ============================================================
-- 爬取任务模块 - 数据库迁移脚本
-- ============================================================

CREATE TABLE IF NOT EXISTS crawl_tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_code VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编码',
    source_url VARCHAR(500) NOT NULL COMMENT '数据源URL',
    source_type VARCHAR(30) NOT NULL DEFAULT 'web' COMMENT '数据源类型: web-网页, api-接口, rss-RSS源',
    crawl_rule TEXT COMMENT '爬取规则JSON',
    priority INT NOT NULL DEFAULT 1 COMMENT '优先级: 1-低, 5-中, 10-高',
    cron_expression VARCHAR(100) COMMENT '定时表达式',
    max_retry INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    retry_delay INT NOT NULL DEFAULT 60 COMMENT '重试间隔(秒)',
    timeout_seconds INT NOT NULL DEFAULT 300 COMMENT '超时时间(秒)',
    status VARCHAR(20) NOT NULL DEFAULT 'idle' COMMENT '状态: idle-空闲, running-运行中, paused-已暂停, completed-已完成, failed-失败',
    last_run_time DATETIME COMMENT '最后执行时间',
    next_run_time DATETIME COMMENT '下次执行时间',
    total_runs INT NOT NULL DEFAULT 0 COMMENT '总执行次数',
    success_runs INT NOT NULL DEFAULT 0 COMMENT '成功次数',
    fail_runs INT NOT NULL DEFAULT 0 COMMENT '失败次数',
    description VARCHAR(500) COMMENT '任务描述',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用: 1-启用, 0-禁用',
    created_by INT COMMENT '创建者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_enabled (enabled),
    INDEX idx_next_run_time (next_run_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爬取任务表';

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

INSERT INTO crawl_tasks (task_name, task_code, source_url, source_type, priority, cron_expression, max_retry, retry_delay, description, status) VALUES
('文物资讯爬取', 'relic_news', 'https://example.com/relic-news', 'web', 5, '0 0 9 * * ?', 3, 120, '每日爬取文物相关新闻资讯', 'idle'),
('博物馆展览信息', 'museum_exhibits', 'https://example.com/museum-api/exhibits', 'api', 10, '0 0 10 * * ?', 2, 60, '定时获取博物馆最新展览信息', 'idle');