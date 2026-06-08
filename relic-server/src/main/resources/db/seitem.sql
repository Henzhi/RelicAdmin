-- MySQL dump 10.13  Distrib 8.4.7, for Win64 (x86_64)
--
-- Host: mysql6.sqlpub.com    Database: seitem
-- ------------------------------------------------------
-- Server version	8.4.3-SQLPub-0.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '5d613700-f2dc-11f0-8131-00163e119ad8:1-30274391,
d81d839c-de43-11f0-ae04-525400143134:1-2925252';

--
-- Table structure for table `admin_user_role`
--

DROP TABLE IF EXISTS `admin_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_user_id` int NOT NULL,
  `role_id` int unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `admin_user_role_ibfk_1` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `admin_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `admin_users`
--

DROP TABLE IF EXISTS `admin_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '管理员用户名',
  `password_hash` varchar(255) NOT NULL COMMENT 'BCrypt加密密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像地址',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-启用, banned-禁用',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员用户表（独立于前台用户表）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcements`
--

DROP TABLE IF EXISTS `announcements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcements` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `position` varchar(50) DEFAULT 'global' COMMENT '展示位置：global/home/user_center',
  `target_audience` varchar(50) DEFAULT 'all' COMMENT '目标受众：all/admin/users',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-发布 0-下线',
  `created_by` int unsigned DEFAULT NULL COMMENT '创建人ID',
  `view_count` int DEFAULT '0' COMMENT '查看次数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_position` (`position`),
  KEY `idx_time_range` (`start_time`,`end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appeal_records`
--

DROP TABLE IF EXISTS `appeal_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appeal_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `penalty_id` bigint unsigned NOT NULL COMMENT '关联的处罚记录ID',
  `user_id` int unsigned NOT NULL COMMENT '申诉用户ID',
  `appeal_reason` text NOT NULL COMMENT '申诉理由',
  `evidence` text COMMENT '证据（JSON或文本）',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/approved/rejected',
  `review_result` varchar(20) DEFAULT NULL COMMENT '复审结果',
  `reviewer_id` int unsigned DEFAULT NULL COMMENT '复审人ID',
  `review_time` datetime DEFAULT NULL COMMENT '复审时间',
  `review_remark` text COMMENT '复审备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_penalty_id` (`penalty_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申诉记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifact_artist`
--

DROP TABLE IF EXISTS `artifact_artist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifact_artist` (
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `artist_id` int unsigned NOT NULL COMMENT '艺术家ID',
  `relationship_type` varchar(50) DEFAULT NULL COMMENT '关系类型（creator/collector等）',
  KEY `idx_artifact_artist` (`artifact_id`,`artist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物与艺术家关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifact_images`
--

DROP TABLE IF EXISTS `artifact_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifact_images` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `image_path` varchar(500) DEFAULT NULL COMMENT '本地存储路径',
  `is_primary` tinyint DEFAULT NULL COMMENT '是否主图 0/1',
  `sort_order` int DEFAULT NULL COMMENT '排序序号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物多图片';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifact_likes`
--

DROP TABLE IF EXISTS `artifact_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifact_likes` (
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `liked_at` datetime DEFAULT NULL COMMENT '点赞时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物点赞';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifact_location`
--

DROP TABLE IF EXISTS `artifact_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifact_location` (
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `location_id` int unsigned NOT NULL COMMENT '地点ID',
  `role` varchar(50) DEFAULT NULL COMMENT '角色（出土地/制作地等）',
  KEY `idx_artifact_location` (`artifact_id`,`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物与地点关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifact_tags`
--

DROP TABLE IF EXISTS `artifact_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifact_tags` (
  `artifact_id` int unsigned NOT NULL,
  `tag_id` int unsigned NOT NULL,
  PRIMARY KEY (`artifact_id`,`tag_id`),
  KEY `idx_artifact_tags_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物与标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifact_types`
--

DROP TABLE IF EXISTS `artifact_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifact_types` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '类型名称（如：绘画、瓷器、青铜器、书法、雕塑、玉器等）',
  `description` text COMMENT '类型描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物类型管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artifacts`
--

DROP TABLE IF EXISTS `artifacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artifacts` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `object_id` varchar(100) NOT NULL COMMENT '博物馆原始ID或系统生成唯一标识',
  `title_zh` varchar(500) DEFAULT NULL COMMENT '中文名称',
  `title_en` varchar(500) NOT NULL COMMENT '英文原始名称',
  `time_period` varchar(200) DEFAULT NULL COMMENT '年代描述（原始）',
  `dynasty_id` int unsigned DEFAULT NULL COMMENT '所属朝代ID',
  `type` varchar(100) DEFAULT NULL COMMENT '文物类型（如 Painting, Ceramics）',
  `material` varchar(200) DEFAULT NULL COMMENT '材质',
  `description` text COMMENT '文物介绍文本',
  `dimensions` varchar(200) DEFAULT NULL COMMENT '尺寸',
  `museum_id` int unsigned NOT NULL COMMENT '现藏博物馆ID',
  `location_id` int unsigned DEFAULT NULL COMMENT '博物馆所在地ID',
  `detail_url` varchar(500) DEFAULT NULL COMMENT '博物馆详情页URL',
  `image_url` varchar(500) DEFAULT NULL COMMENT '主图原图URL',
  `image_path` varchar(500) DEFAULT NULL COMMENT '本地存储相对路径',
  `credit_line` varchar(300) DEFAULT NULL COMMENT '版权/来源说明',
  `accession_number` varchar(100) DEFAULT NULL COMMENT '馆藏编号',
  `crawl_date` date DEFAULT NULL COMMENT '爬取日期',
  `image_validated` tinyint DEFAULT NULL COMMENT '图片有效性验证 0/1',
  `last_updated` timestamp NULL DEFAULT NULL COMMENT '最后更新时间',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `provenance` text COMMENT '文物流转或来源脉络',
  `current_status` varchar(100) DEFAULT NULL COMMENT '当前展出、保存或数字开放状态',
  PRIMARY KEY (`id`),
  KEY `idx_museum_object` (`museum_id`,`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `artists`
--

DROP TABLE IF EXISTS `artists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artists` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name_zh` varchar(100) DEFAULT NULL COMMENT '中文名',
  `name_en` varchar(200) DEFAULT NULL COMMENT '英文名',
  `birth_year` int DEFAULT NULL COMMENT '生年',
  `death_year` int DEFAULT NULL COMMENT '卒年',
  `dynasty_id` int unsigned DEFAULT NULL COMMENT '主要活跃朝代ID',
  `biography` text COMMENT '生平介绍',
  `baidu_url` varchar(255) DEFAULT NULL COMMENT '百度百科链接',
  `wiki_url` varchar(255) DEFAULT NULL COMMENT '维基百科链接',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='艺术家（书画家等）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_records`
--

DROP TABLE IF EXISTS `audit_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` varchar(100) DEFAULT NULL COMMENT '内容ID（关联的具体内容）',
  `content_type` varchar(50) NOT NULL COMMENT '内容类型：comment/post/upload等',
  `source_type` varchar(50) DEFAULT NULL COMMENT '来源表名',
  `content` text NOT NULL COMMENT '待审核内容',
  `submitter_id` int unsigned NOT NULL COMMENT '提交者用户ID',
  `auto_audit_result` varchar(20) DEFAULT 'pending' COMMENT '自动审核结果：pending/approved/rejected',
  `manual_audit_result` varchar(20) DEFAULT 'pending' COMMENT '人工审核结果：pending/approved/rejected',
  `auditor_id` int unsigned DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_manual_result` (`manual_audit_result`),
  KEY `idx_submitter` (`submitter_id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_strategies`
--

DROP TABLE IF EXISTS `audit_strategies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_strategies` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_type` varchar(50) NOT NULL COMMENT '内容类型：post/comment/upload',
  `auto_mode` varchar(20) DEFAULT 'auto_review' COMMENT '自动审核模式：auto_pass/auto_review/auto_reject',
  `enable_sensitive_check` tinyint DEFAULT '1' COMMENT '启用敏感词检测：1-是 0-否',
  `enable_image_check` tinyint DEFAULT '0' COMMENT '启用图片审核：1-是 0-否',
  `risk_threshold` int DEFAULT '3' COMMENT '风险阈值，1-5，超过则转人工',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审核策略配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `backup_records`
--

DROP TABLE IF EXISTS `backup_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `backup_name` varchar(200) NOT NULL COMMENT '备份名称',
  `backup_type` varchar(20) NOT NULL COMMENT '备份类型：full/incremental/export',
  `scope` varchar(50) DEFAULT NULL COMMENT '备份范围',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_path` varchar(500) DEFAULT NULL COMMENT '存储路径',
  `operator_id` int unsigned DEFAULT NULL COMMENT '操作人ID',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-进行中 1-已完成 2-失败',
  `remark` text COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='备份记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `backup_strategies`
--

DROP TABLE IF EXISTS `backup_strategies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_strategies` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `auto_backup_enabled` tinyint DEFAULT '0' COMMENT '是否开启自动备份：1-是 0-否',
  `backup_cron` varchar(50) DEFAULT '0 0 2 * * ?' COMMENT 'Cron表达式，默认每日凌晨2点',
  `backup_type` varchar(20) DEFAULT 'full' COMMENT '默认备份类型：full/incremental/export',
  `retention_days` int DEFAULT '30' COMMENT '备份保留天数',
  `encrypt_enabled` tinyint DEFAULT '1' COMMENT '是否加密存储：1-是 0-否',
  `notify_on_failure` tinyint DEFAULT '1' COMMENT '失败是否通知：1-是 0-否',
  `storage_path` varchar(500) DEFAULT './backups' COMMENT '备份文件存储路径',
  `storage_warning_threshold` bigint DEFAULT '10737418240' COMMENT '存储告警阈值（字节），默认10GB',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='备份策略配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment_likes`
--

DROP TABLE IF EXISTS `comment_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_likes` (
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `comment_id` int unsigned NOT NULL COMMENT '评论ID',
  `liked_at` datetime DEFAULT NULL COMMENT '点赞时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crawl_task_logs`
--

DROP TABLE IF EXISTS `crawl_task_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crawl_task_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `task_id` int unsigned NOT NULL COMMENT '任务ID',
  `task_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务名称(冗余)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'running' COMMENT '执行状态: running, success, failed',
  `crawled_count` int NOT NULL DEFAULT '0' COMMENT '爬取数量',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '当前重试次数',
  `executor_id` int DEFAULT NULL COMMENT '执行者ID(0=系统)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  CONSTRAINT `fk_crawl_task_logs_task` FOREIGN KEY (`task_id`) REFERENCES `crawl_tasks` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爬取任务执行日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crawl_tasks`
--

DROP TABLE IF EXISTS `crawl_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crawl_tasks` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `task_name` varchar(100) NOT NULL DEFAULT '' COMMENT '任务名称',
  `task_code` varchar(50) NOT NULL DEFAULT '' COMMENT '任务编码',
  `source_url` varchar(500) NOT NULL DEFAULT '' COMMENT '数据源URL',
  `source_type` varchar(30) NOT NULL DEFAULT 'web' COMMENT '数据源类型',
  `crawl_rule` text COMMENT '爬取规则JSON',
  `priority` int NOT NULL DEFAULT '1' COMMENT '优先级',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT '定时表达式',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '最大重试次数',
  `retry_delay` int NOT NULL DEFAULT '60' COMMENT '重试间隔(秒)',
  `timeout_seconds` int NOT NULL DEFAULT '300' COMMENT '超时时间(秒)',
  `last_run_time` datetime DEFAULT NULL COMMENT '最后执行时间',
  `next_run_time` datetime DEFAULT NULL COMMENT '下次执行时间',
  `total_runs` int NOT NULL DEFAULT '0' COMMENT '总执行次数',
  `success_runs` int NOT NULL DEFAULT '0' COMMENT '成功次数',
  `fail_runs` int NOT NULL DEFAULT '0' COMMENT '失败次数',
  `description` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_by` int DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `museum_id` int unsigned NOT NULL COMMENT '博物馆ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'idle' COMMENT '状态: idle-空闲, running-运行中, paused-已暂停, completed-已完成, failed-失败',
  `items_crawled` int DEFAULT NULL COMMENT '爬取总数',
  `items_new` int DEFAULT NULL COMMENT '新增数量',
  `items_updated` int DEFAULT NULL COMMENT '更新数量',
  `error_message` text COMMENT '错误信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_task_code` (`task_code`),
  KEY `idx_status` (`status`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_next_run_time` (`next_run_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='爬取任务记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dynasties`
--

DROP TABLE IF EXISTS `dynasties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dynasties` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name_zh` varchar(50) NOT NULL COMMENT '中文朝代名称',
  `name_en` varchar(100) DEFAULT NULL COMMENT '英文名称',
  `start_year` int DEFAULT NULL COMMENT '起始年份（公元前为负）',
  `end_year` int DEFAULT NULL COMMENT '结束年份',
  `description` text COMMENT '朝代简介',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历史朝代';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locations` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name_zh` varchar(100) NOT NULL COMMENT '中文名称',
  `name_en` varchar(200) DEFAULT NULL COMMENT '英文名称',
  `parent_id` int unsigned DEFAULT NULL COMMENT '上级地点ID',
  `type` varchar(20) DEFAULT NULL COMMENT '类型：country/province/city/site',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地理地点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `museums`
--

DROP TABLE IF EXISTS `museums`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `museums` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '博物馆官方完整名称',
  `short_name` varchar(100) DEFAULT NULL COMMENT '简称',
  `country` varchar(100) NOT NULL COMMENT '国家',
  `city` varchar(100) DEFAULT NULL COMMENT '城市',
  `website` varchar(255) DEFAULT NULL COMMENT '官网URL',
  `collection_url` varchar(255) DEFAULT NULL COMMENT '藏品搜索URL',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '博物馆纬度',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '博物馆经度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='博物馆信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '接收用户ID',
  `type` varchar(50) NOT NULL COMMENT '通知类型：audit_result/new_follower等',
  `title` varchar(200) DEFAULT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `is_read` tinyint(1) DEFAULT NULL COMMENT '是否已读',
  `extra_data` text COMMENT '附加数据(JSON)',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通知';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `operation_logs`
--

DROP TABLE IF EXISTS `operation_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_logs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '操作用户ID',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型：INSERT/UPDATE/DELETE等',
  `target_type` varchar(50) DEFAULT NULL COMMENT '操作目标类型（表名/模块）',
  `target_id` varchar(100) DEFAULT NULL COMMENT '操作对象ID',
  `old_value` text COMMENT '变更前数据(JSON)',
  `new_value` text COMMENT '变更后数据(JSON)',
  `ip` varchar(45) DEFAULT NULL COMMENT '操作IP',
  `user_agent` varchar(300) DEFAULT NULL COMMENT '用户代理',
  `created_at` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员操作日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `penalty_records`
--

DROP TABLE IF EXISTS `penalty_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `penalty_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '被处罚用户ID',
  `penalty_type` varchar(50) NOT NULL COMMENT '处罚类型：warning/temp_ban/permanent_ban等',
  `reason` text NOT NULL COMMENT '处罚原因',
  `operator_id` int unsigned NOT NULL COMMENT '操作人ID',
  `penalty_time` datetime NOT NULL COMMENT '处罚时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时封禁）',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-生效 0-已解除',
  `remark` text COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_penalty_time` (`penalty_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处罚记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '权限标识（artifact:edit）',
  `display_name` varchar(100) DEFAULT NULL COMMENT '显示名称',
  `module` varchar(50) DEFAULT NULL COMMENT '所属模块',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `post_likes`
--

DROP TABLE IF EXISTS `post_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_likes` (
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `post_id` int unsigned NOT NULL COMMENT '动态ID',
  `liked_at` datetime DEFAULT NULL COMMENT '点赞时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态点赞';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_failed_question`
--

DROP TABLE IF EXISTS `qa_failed_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_failed_question` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '失败问题记录主键',
  `qa_log_id` bigint unsigned DEFAULT NULL COMMENT '问答日志 ID，关联 qa_log.id，日志写入失败时可为空',
  `session_id` varchar(100) DEFAULT NULL COMMENT '会话 ID，便于追溯上下文',
  `user_id` int unsigned DEFAULT NULL COMMENT '用户 ID，关联 users.id，未登录可为空',
  `question` text NOT NULL COMMENT '用户原始问题',
  `normalized_question` varchar(1000) DEFAULT NULL COMMENT '规范化后的问题文本',
  `question_hash` char(64) DEFAULT NULL COMMENT '问题哈希，建议 SHA-256，用于统计重复失败问题',
  `intent` varchar(80) DEFAULT NULL COMMENT '识别到的意图编码',
  `failure_type` varchar(40) NOT NULL COMMENT '失败类型：no_data/need_clarification/unsupported/retrieval_error/generation_error，对应 API status 映射见设计文档',
  `artifact_id` int unsigned DEFAULT NULL COMMENT '关联文物内部 ID，关联 artifacts.id',
  `object_id` varchar(100) DEFAULT NULL COMMENT '关联文物 object_id',
  `error_detail` text COMMENT '错误详情或无数据原因',
  `status` varchar(30) NOT NULL DEFAULT 'open' COMMENT '处理状态：open/ignored/resolved',
  `handled_by` int unsigned DEFAULT NULL COMMENT '处理人 ID，关联 users.id',
  `handled_at` datetime DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_qa_failed_log` (`qa_log_id`),
  KEY `idx_qa_failed_session` (`session_id`),
  KEY `idx_qa_failed_user` (`user_id`),
  KEY `idx_qa_failed_hash` (`question_hash`),
  KEY `idx_qa_failed_intent` (`intent`),
  KEY `idx_qa_failed_type_created` (`failure_type`,`created_at`),
  KEY `idx_qa_failed_object` (`object_id`),
  KEY `idx_qa_failed_status` (`status`),
  KEY `idx_qa_failed_artifact` (`artifact_id`),
  KEY `idx_qa_failed_handled_by` (`handled_by`),
  CONSTRAINT `fk_qa_failed_artifact` FOREIGN KEY (`artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_failed_handled_by` FOREIGN KEY (`handled_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_failed_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_failed_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_failed_status` CHECK ((`status` in (_utf8mb4'open',_utf8mb4'ignored',_utf8mb4'resolved'))),
  CONSTRAINT `chk_qa_failed_type` CHECK ((`failure_type` in (_utf8mb4'no_data',_utf8mb4'need_clarification',_utf8mb4'unsupported',_utf8mb4'retrieval_error',_utf8mb4'generation_error')))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答失败问题记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_feedback`
--

DROP TABLE IF EXISTS `qa_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_feedback` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户反馈记录主键',
  `qa_log_id` bigint unsigned NOT NULL COMMENT '问答日志 ID，关联 qa_log.id',
  `user_id` int unsigned DEFAULT NULL COMMENT '反馈用户 ID，关联 users.id，未登录可为空',
  `feedback_type` varchar(30) NOT NULL COMMENT '反馈类型：helpful/inaccurate',
  `comment` text COMMENT '用户补充说明',
  `source_client` varchar(50) DEFAULT NULL COMMENT '调用来源，如 web、app、demo',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qa_feedback_log_user_type` (`qa_log_id`,`user_id`,`feedback_type`),
  KEY `idx_qa_feedback_log` (`qa_log_id`),
  KEY `idx_qa_feedback_user` (`user_id`),
  KEY `idx_qa_feedback_type_created` (`feedback_type`,`created_at`),
  CONSTRAINT `fk_qa_feedback_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_feedback_type` CHECK ((`feedback_type` in (_utf8mb4'helpful',_utf8mb4'inaccurate')))
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答用户反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_intent_template`
--

DROP TABLE IF EXISTS `qa_intent_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_intent_template` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '意图模板主键',
  `intent_code` varchar(80) NOT NULL COMMENT '意图编码，如 artifact_material',
  `intent_name` varchar(100) NOT NULL COMMENT '意图中文名称，如文物材质',
  `description` text COMMENT '意图说明',
  `data_source` varchar(50) NOT NULL COMMENT '主要数据源：mysql/neo4j/mixed/template',
  `question_patterns_json` json DEFAULT NULL COMMENT '常见问法模板 JSON',
  `mysql_query_template` text COMMENT 'MySQL 查询模板，仅作配置参考，不直接动态执行',
  `neo4j_query_template` text COMMENT 'Neo4j 查询模板，仅作配置参考，不直接动态执行',
  `answer_template` text COMMENT '回答模板',
  `no_data_template` text COMMENT '暂无数据兜底模板',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：1 启用，0 停用',
  `version` varchar(30) NOT NULL DEFAULT 'v1' COMMENT '模板版本',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qa_intent_code` (`intent_code`),
  KEY `idx_qa_intent_enabled` (`enabled`),
  KEY `idx_qa_intent_source` (`data_source`),
  CONSTRAINT `chk_qa_intent_enabled` CHECK ((`enabled` in (0,1))),
  CONSTRAINT `chk_qa_intent_source` CHECK ((`data_source` in (_utf8mb4'mysql',_utf8mb4'neo4j',_utf8mb4'mixed',_utf8mb4'template')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答意图与回答模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_log`
--

DROP TABLE IF EXISTS `qa_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '问答日志内部主键',
  `qa_log_uuid` char(36) NOT NULL COMMENT '对外问答日志 ID，对应 API qaLogId',
  `session_pk` bigint unsigned DEFAULT NULL COMMENT '会话表内部主键，关联 qa_session.id',
  `session_id` varchar(100) DEFAULT NULL COMMENT '接口层会话 ID，冗余保存便于查询',
  `conversation_id` varchar(100) DEFAULT NULL COMMENT '客户端对话 ID',
  `user_id` int unsigned DEFAULT NULL COMMENT '提问用户 ID，关联 users.id，未登录可为空',
  `request_object_id` varchar(100) DEFAULT NULL COMMENT '请求体或 URL 原始传入的 objectId',
  `question` text NOT NULL COMMENT '用户原始问题',
  `normalized_question` varchar(1000) DEFAULT NULL COMMENT '规范化后的问题文本',
  `intent` varchar(80) DEFAULT NULL COMMENT '识别到的意图编码',
  `intent_confidence` decimal(5,4) DEFAULT NULL COMMENT '意图识别置信度，建议 0 到 1',
  `intent_detail_json` json DEFAULT NULL COMMENT '意图识别和实体抽取调试信息，如 matchedKeywords、entities、confidence',
  `status` varchar(30) NOT NULL COMMENT '回答状态：answered/no_data/need_clarification/unsupported/error',
  `answer` text COMMENT '返回给用户的自然语言答案',
  `fact_content` text COMMENT '事实内容，来自 MySQL、Neo4j 或确认数据源',
  `supplemental_content` text COMMENT '模板或大模型生成的补充性描述',
  `artifact_id` int unsigned DEFAULT NULL COMMENT '关联文物内部 ID，关联 artifacts.id',
  `object_id` varchar(100) DEFAULT NULL COMMENT '关联文物 object_id，对应 API objectId',
  `resolve_source` varchar(50) DEFAULT NULL COMMENT '文物解析来源，如 question_entity/request_object_id/session_context',
  `candidates_json` json DEFAULT NULL COMMENT '多候选文物列表，need_clarification 时使用',
  `source_client` varchar(50) DEFAULT NULL COMMENT '调用来源，如 web、app、demo',
  `retrieval_raw_json` json DEFAULT NULL COMMENT '检索原始调试数据摘要',
  `error_message` text COMMENT '错误信息，error 状态时使用',
  `latency_ms` int unsigned DEFAULT NULL COMMENT '本次问答处理耗时，单位毫秒',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '问答发生时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qa_log_uuid` (`qa_log_uuid`),
  KEY `idx_qa_log_session_pk` (`session_pk`),
  KEY `idx_qa_log_session` (`session_id`),
  KEY `idx_qa_log_user_created` (`user_id`,`created_at`),
  KEY `idx_qa_log_request_object` (`request_object_id`),
  KEY `idx_qa_log_object` (`object_id`),
  KEY `idx_qa_log_intent_status` (`intent`,`status`),
  KEY `idx_qa_log_created` (`created_at`),
  KEY `fk_qa_log_artifact` (`artifact_id`),
  CONSTRAINT `fk_qa_log_artifact` FOREIGN KEY (`artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_log_session` FOREIGN KEY (`session_pk`) REFERENCES `qa_session` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_log_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_log_intent_confidence` CHECK (((`intent_confidence` is null) or ((`intent_confidence` >= 0) and (`intent_confidence` <= 1)))),
  CONSTRAINT `chk_qa_log_status` CHECK ((`status` in (_utf8mb4'answered',_utf8mb4'no_data',_utf8mb4'need_clarification',_utf8mb4'unsupported',_utf8mb4'error')))
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答主日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_review_task`
--

DROP TABLE IF EXISTS `qa_review_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_review_task` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '人工审核任务主键',
  `feedback_id` bigint unsigned NOT NULL COMMENT '反馈记录 ID，关联 qa_feedback.id',
  `qa_log_id` bigint unsigned NOT NULL COMMENT '问答日志 ID，关联 qa_log.id',
  `task_status` varchar(30) NOT NULL DEFAULT 'pending' COMMENT '任务状态：pending/processing/done/closed',
  `review_result` varchar(30) DEFAULT NULL COMMENT '审核结果：approved/rejected/fixed',
  `priority` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '优先级：1 低、2 中、3 高',
  `assigned_admin_id` int DEFAULT NULL COMMENT '分配给的管理员 ID，关联 admin_users.id',
  `reviewer_admin_id` int DEFAULT NULL COMMENT '实际审核管理员 ID，关联 admin_users.id',
  `review_comment` text COMMENT '审核意见',
  `corrected_answer` text COMMENT '修正后的答案',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qa_review_feedback` (`feedback_id`),
  KEY `idx_qa_review_log` (`qa_log_id`),
  KEY `idx_qa_review_status_created` (`task_status`,`created_at`),
  KEY `idx_qa_review_assigned_admin` (`assigned_admin_id`),
  KEY `idx_qa_review_reviewer_admin` (`reviewer_admin_id`),
  CONSTRAINT `fk_qa_review_assigned_admin` FOREIGN KEY (`assigned_admin_id`) REFERENCES `admin_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_review_feedback` FOREIGN KEY (`feedback_id`) REFERENCES `qa_feedback` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_review_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_review_reviewer_admin` FOREIGN KEY (`reviewer_admin_id`) REFERENCES `admin_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_review_priority` CHECK ((`priority` between 1 and 3)),
  CONSTRAINT `chk_qa_review_result` CHECK (((`review_result` is null) or (`review_result` in (_utf8mb4'approved',_utf8mb4'rejected',_utf8mb4'fixed')))),
  CONSTRAINT `chk_qa_review_status` CHECK ((`task_status` in (_utf8mb4'pending',_utf8mb4'processing',_utf8mb4'done',_utf8mb4'closed')))
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答不准确反馈人工审核任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_session`
--

DROP TABLE IF EXISTS `qa_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_session` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '会话记录内部主键',
  `session_id` varchar(100) NOT NULL COMMENT '接口层会话 ID，对应 API sessionId',
  `conversation_id` varchar(100) DEFAULT NULL COMMENT '客户端对话 ID，对应 API conversationId',
  `user_id` int unsigned DEFAULT NULL COMMENT '用户 ID，关联 users.id，未登录可为空',
  `current_artifact_id` int unsigned DEFAULT NULL COMMENT '当前上下文文物内部 ID，关联 artifacts.id',
  `current_object_id` varchar(100) DEFAULT NULL COMMENT '当前上下文文物 object_id',
  `last_intent` varchar(80) DEFAULT NULL COMMENT '最近一次识别到的意图编码',
  `recent_context_json` json DEFAULT NULL COMMENT '最近上下文摘要，建议保存最近 5 轮问答',
  `source_client` varchar(50) DEFAULT NULL COMMENT '调用来源，如 web、app、demo',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '会话状态：active/closed/expired',
  `last_active_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近活跃时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qa_session_session_id` (`session_id`),
  KEY `idx_qa_session_user` (`user_id`),
  KEY `idx_qa_session_object` (`current_object_id`),
  KEY `idx_qa_session_last_active` (`last_active_at`),
  KEY `fk_qa_session_artifact` (`current_artifact_id`),
  CONSTRAINT `fk_qa_session_artifact` FOREIGN KEY (`current_artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_session_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_session_status` CHECK ((`status` in (_utf8mb4'active',_utf8mb4'closed',_utf8mb4'expired')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答会话上下文表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qa_source_record`
--

DROP TABLE IF EXISTS `qa_source_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa_source_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '答案来源记录主键',
  `qa_log_id` bigint unsigned NOT NULL COMMENT '问答日志 ID，关联 qa_log.id',
  `source_type` varchar(30) NOT NULL COMMENT '来源类型：mysql/neo4j/llm/template',
  `source_name` varchar(200) NOT NULL COMMENT '来源名称，如博物馆名称、Neo4j 知识图谱、模板名称',
  `source_table` varchar(100) DEFAULT NULL COMMENT 'MySQL 来源表名，如 artifacts、artists',
  `source_record_id` varchar(100) DEFAULT NULL COMMENT '来源记录 ID，可保存 MySQL 主键或 Neo4j 节点 ID',
  `artifact_id` int unsigned DEFAULT NULL COMMENT '关联文物内部 ID，关联 artifacts.id',
  `object_id` varchar(100) DEFAULT NULL COMMENT '关联文物 object_id',
  `detail_url` varchar(500) DEFAULT NULL COMMENT '原始详情页链接',
  `fact_text` text COMMENT '来源事实文本，对应 API sources[].factText',
  `source_payload_json` json DEFAULT NULL COMMENT '来源原始摘要，如查询字段、节点关系等',
  `confidence` decimal(5,4) DEFAULT NULL COMMENT '来源置信度，建议 0 到 1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_qa_source_log` (`qa_log_id`),
  KEY `idx_qa_source_type` (`source_type`),
  KEY `idx_qa_source_object` (`object_id`),
  KEY `idx_qa_source_artifact` (`artifact_id`),
  CONSTRAINT `fk_qa_source_artifact` FOREIGN KEY (`artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_source_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_source_confidence` CHECK (((`confidence` is null) or ((`confidence` >= 0) and (`confidence` <= 1)))),
  CONSTRAINT `chk_qa_source_type` CHECK ((`source_type` in (_utf8mb4'mysql',_utf8mb4'neo4j',_utf8mb4'llm',_utf8mb4'template')))
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识问答答案来源记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `restore_records`
--

DROP TABLE IF EXISTS `restore_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restore_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `backup_id` bigint unsigned NOT NULL COMMENT '关联的备份记录ID',
  `backup_name` varchar(200) DEFAULT NULL COMMENT '备份名称快照',
  `operator_id` int unsigned NOT NULL COMMENT '操作人ID',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-进行中 1-成功 2-失败',
  `remark` text COMMENT '备注/失败原因',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_backup_id` (`backup_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据恢复记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_id` int unsigned NOT NULL COMMENT '角色ID',
  `permission_id` int unsigned NOT NULL COMMENT '权限ID',
  KEY `idx_role_perm` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '角色标识（super_admin等）',
  `display_name` varchar(50) NOT NULL COMMENT '显示名称',
  `description` text COMMENT '描述',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `security_logs`
--

DROP TABLE IF EXISTS `security_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `security_logs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned DEFAULT NULL COMMENT '相关用户ID',
  `event_type` varchar(50) NOT NULL COMMENT '事件类型：login_failed/permission_change等',
  `ip` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `detail` text COMMENT '详细信息',
  `created_at` datetime DEFAULT NULL COMMENT '发生时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='安全日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sensitive_words`
--

DROP TABLE IF EXISTS `sensitive_words`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensitive_words` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `word` varchar(100) NOT NULL COMMENT '敏感词',
  `category` varchar(50) DEFAULT NULL COMMENT '分类：political/pornographic/violence/advertising/other',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `level` varchar(20) DEFAULT NULL COMMENT '级别：high/medium/low',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_word` (`word`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='敏感词库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_alerts`
--

DROP TABLE IF EXISTS `system_alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_alerts` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `alert_type` varchar(50) NOT NULL COMMENT '告警类型: interface_timeout/db_failure/disk_full/high_memory/cpu_overload/service_error',
  `severity` varchar(20) NOT NULL DEFAULT 'warning' COMMENT '严重级别: info/warning/critical',
  `title` varchar(200) NOT NULL COMMENT '告警标题',
  `message` text COMMENT '告警详情',
  `source` varchar(100) DEFAULT NULL COMMENT '告警来源(服务名/模块名)',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-活跃, resolved-已解决, ignored-已忽略',
  `resolved_by` int DEFAULT NULL COMMENT '处理人ID',
  `resolved_at` datetime DEFAULT NULL COMMENT '处理时间',
  `resolve_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_alert_type` (`alert_type`),
  KEY `idx_severity` (`severity`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统告警记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_configs`
--

DROP TABLE IF EXISTS `system_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_configs` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_name` varchar(100) NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_type` varchar(30) NOT NULL DEFAULT 'string' COMMENT '值类型: string/number/boolean/json',
  `config_group` varchar(50) NOT NULL DEFAULT 'general' COMMENT '配置分组',
  `config_value` text NOT NULL COMMENT '配置值',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `editable` tinyint NOT NULL DEFAULT '1' COMMENT '是否可编辑',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `updated_by` int unsigned DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_config_group` (`config_group`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_logs`
--

DROP TABLE IF EXISTS `system_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_logs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `log_level` varchar(10) NOT NULL COMMENT '日志级别(DEBUG,INFO,WARN,ERROR)',
  `module` varchar(100) DEFAULT NULL COMMENT '模块名称',
  `message` text COMMENT '日志信息',
  `detail` text COMMENT '详细内容(JSON格式)',
  `created_at` datetime DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统运行日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL COMMENT '标签类别',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tags_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文物标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_behaviors`
--

DROP TABLE IF EXISTS `user_behaviors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_behaviors` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `behavior_type` varchar(50) NOT NULL COMMENT '行为类型：login/comment/publish/upload/like/favorite/browse/follow',
  `target_type` varchar(50) DEFAULT NULL COMMENT '目标类型：artifact/post/comment/user',
  `target_id` varchar(100) DEFAULT NULL COMMENT '目标对象ID',
  `target_desc` varchar(500) DEFAULT NULL COMMENT '目标描述',
  `ip` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `device` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `detail` text COMMENT '行为详情(JSON)',
  `created_at` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_behavior_type` (`behavior_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户行为记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_browse_history`
--

DROP TABLE IF EXISTS `user_browse_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_browse_history` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `browse_time` datetime DEFAULT NULL COMMENT '浏览时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户浏览历史';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_comments`
--

DROP TABLE IF EXISTS `user_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_comments` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `parent_id` int unsigned DEFAULT NULL COMMENT '父评论ID',
  `content` text NOT NULL COMMENT '评论内容',
  `like_count` int DEFAULT NULL COMMENT '点赞数',
  `status` varchar(20) DEFAULT NULL COMMENT '状态：pending/approved/rejected',
  `audit_by` int unsigned DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(200) DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户评论';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_favorites`
--

DROP TABLE IF EXISTS `user_favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorites` (
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `artifact_id` int unsigned NOT NULL COMMENT '文物ID',
  `group_name` varchar(50) DEFAULT NULL COMMENT '收藏分组名称',
  `created_at` datetime DEFAULT NULL COMMENT '收藏时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏文物';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_follows`
--

DROP TABLE IF EXISTS `user_follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_follows` (
  `follower_id` int unsigned NOT NULL COMMENT '关注者用户ID',
  `followee_id` int unsigned NOT NULL COMMENT '被关注者用户ID',
  `created_at` datetime DEFAULT NULL COMMENT '关注时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户关注';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_posts`
--

DROP TABLE IF EXISTS `user_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_posts` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `content` text NOT NULL COMMENT '动态内容',
  `artifact_id` int unsigned DEFAULT NULL COMMENT '关联文物ID',
  `museum_id` int unsigned DEFAULT NULL COMMENT '关联博物馆ID',
  `image_urls` text COMMENT '图片URL列表（JSON或逗号分隔）',
  `like_count` int DEFAULT NULL COMMENT '点赞数',
  `comment_count` int DEFAULT NULL COMMENT '评论数',
  `status` varchar(20) DEFAULT NULL COMMENT '审核状态：pending/approved/rejected',
  `audit_by` int unsigned DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(200) DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户动态';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `role_id` int unsigned NOT NULL COMMENT '角色ID',
  `granted_by` int unsigned DEFAULT NULL COMMENT '授权人用户ID',
  `granted_at` datetime DEFAULT NULL COMMENT '授权时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_uploads`
--

DROP TABLE IF EXISTS `user_uploads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_uploads` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `artifact_id` int unsigned DEFAULT NULL COMMENT '关联文物ID',
  `media_type` varchar(20) NOT NULL COMMENT '媒体类型：image/audio/video',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `file_path` varchar(500) DEFAULT NULL COMMENT '本地存储路径',
  `caption` varchar(500) DEFAULT NULL COMMENT '用户描述',
  `location_taken` varchar(200) DEFAULT NULL COMMENT '拍摄地点',
  `status` varchar(20) DEFAULT NULL COMMENT '审核状态：pending/approved/rejected',
  `audit_by` int unsigned DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(200) DEFAULT NULL COMMENT '拒绝原因',
  `like_count` int DEFAULT NULL COMMENT '点赞数',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户上传内容';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) NOT NULL COMMENT '密码哈希',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `status` varchar(20) DEFAULT NULL COMMENT '状态：active/disabled/banned',
  `ban_reason` text COMMENT '封禁原因',
  `user_type` varchar(20) DEFAULT NULL,
  `registered_at` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_ip` varchar(45) DEFAULT NULL COMMENT '最后登录IP',
  `source` varchar(20) DEFAULT NULL COMMENT '注册来源：web/app',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `comment_disabled` tinyint DEFAULT '0' COMMENT '是否禁止评论',
  `upload_disabled` tinyint DEFAULT '0' COMMENT '是否禁止上传',
  `institution` varchar(200) DEFAULT NULL COMMENT '用户所属机构',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `violation_types`
--

DROP TABLE IF EXISTS `violation_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `violation_types` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_code` varchar(50) NOT NULL COMMENT '违规类型编码',
  `type_name` varchar(100) NOT NULL COMMENT '违规类型名称',
  `severity_level` tinyint NOT NULL COMMENT '严重等级：1-轻微 2-一般 3-严重 4-特别严重',
  `default_penalty` varchar(50) DEFAULT NULL COMMENT '默认处罚：warning/temp_ban/permanent_ban',
  `description` text COMMENT '描述说明',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_code` (`type_code`),
  KEY `idx_severity` (`severity_level`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='违规类型配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'seitem'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-31 17:52:25
