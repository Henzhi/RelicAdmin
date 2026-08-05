-- =====================================================
-- V2: 移除爬取任务模块（2026-08-05）
-- 决策：爬取模块下架，删除两张表及其数据
-- 表: crawl_tasks, crawl_task_logs
-- =====================================================

DROP TABLE IF EXISTS `crawl_task_logs`;
DROP TABLE IF EXISTS `crawl_tasks`;
