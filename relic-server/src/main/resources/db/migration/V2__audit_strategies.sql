-- ============================================================
-- 审核策略配置表
-- V2: 内容审核系统 - 审核策略配置
-- ============================================================

CREATE TABLE IF NOT EXISTS audit_strategies (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    content_type VARCHAR(50) NOT NULL COMMENT '内容类型：post/comment/upload',
    auto_mode VARCHAR(20) DEFAULT 'auto_review' COMMENT '自动审核模式：auto_pass/auto_review/auto_reject',
    enable_sensitive_check TINYINT DEFAULT 1 COMMENT '启用敏感词检测：1-是 0-否',
    enable_image_check TINYINT DEFAULT 0 COMMENT '启用图片审核：1-是 0-否',
    risk_threshold INT DEFAULT 3 COMMENT '风险阈值，1-5，超过则转人工',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间'
) COMMENT='审核策略配置表';

-- 插入默认策略（每种内容类型一条）
INSERT IGNORE INTO audit_strategies (content_type, auto_mode, enable_sensitive_check, enable_image_check, risk_threshold, status, created_at)
VALUES
    ('post', 'auto_review', 1, 0, 2, 1, NOW()),
    ('comment', 'auto_review', 1, 0, 2, 1, NOW()),
    ('upload', 'auto_review', 1, 1, 3, 1, NOW());