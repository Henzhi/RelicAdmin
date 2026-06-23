/*
 Navicat Premium Data Transfer

 Source Server         : seitem
 Source Server Type    : MySQL
 Source Server Version : 80403 (8.4.3-SQLPub-0.0.1)
 Source Host           : mysql6.sqlpub.com:3311
 Source Schema         : seitem

 Target Server Type    : MySQL
 Target Server Version : 80403 (8.4.3-SQLPub-0.0.1)
 File Encoding         : 65001

 Date: 05/06/2026 13:52:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_user_role`;
CREATE TABLE `admin_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_user_id` int NOT NULL,
  `role_id` int UNSIGNED NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_role`(`admin_user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `admin_user_role_ibfk_1` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `admin_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for admin_users
-- ----------------------------
DROP TABLE IF EXISTS `admin_users`;
CREATE TABLE `admin_users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态: active-启用, banned-禁用',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员用户表（独立于前台用户表）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'global' COMMENT '展示位置：global/home/user_center',
  `target_audience` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'all' COMMENT '目标受众：all/admin/users',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-发布 0-下线',
  `created_by` int UNSIGNED NULL DEFAULT NULL COMMENT '创建人ID',
  `view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_position`(`position` ASC) USING BTREE,
  INDEX `idx_time_range`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for appeal_records
-- ----------------------------
DROP TABLE IF EXISTS `appeal_records`;
CREATE TABLE `appeal_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `penalty_id` bigint UNSIGNED NOT NULL COMMENT '关联的处罚记录ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '申诉用户ID',
  `appeal_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申诉理由',
  `evidence` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '证据（JSON或文本）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected',
  `review_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '复审结果',
  `reviewer_id` int UNSIGNED NULL DEFAULT NULL COMMENT '复审人ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '复审时间',
  `review_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '复审备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_penalty_id`(`penalty_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '申诉记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact_artist
-- ----------------------------
DROP TABLE IF EXISTS `artifact_artist`;
CREATE TABLE `artifact_artist`  (
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `artist_id` int UNSIGNED NOT NULL COMMENT '艺术家ID',
  `relationship_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关系类型（creator/collector等）',
  INDEX `idx_artifact_artist`(`artifact_id` ASC, `artist_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物与艺术家关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact_images
-- ----------------------------
DROP TABLE IF EXISTS `artifact_images`;
CREATE TABLE `artifact_images`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `image_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '本地存储路径',
  `is_primary` tinyint NULL DEFAULT NULL COMMENT '是否主图 0/1',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6275 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物多图片' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact_likes
-- ----------------------------
DROP TABLE IF EXISTS `artifact_likes`;
CREATE TABLE `artifact_likes`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `liked_at` datetime NULL DEFAULT NULL COMMENT '点赞时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物点赞' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact_location
-- ----------------------------
DROP TABLE IF EXISTS `artifact_location`;
CREATE TABLE `artifact_location`  (
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `location_id` int UNSIGNED NOT NULL COMMENT '地点ID',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色（出土地/制作地等）',
  INDEX `idx_artifact_location`(`artifact_id` ASC, `location_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物与地点关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact_tags
-- ----------------------------
DROP TABLE IF EXISTS `artifact_tags`;
CREATE TABLE `artifact_tags`  (
  `artifact_id` int UNSIGNED NOT NULL,
  `tag_id` int UNSIGNED NOT NULL,
  PRIMARY KEY (`artifact_id`, `tag_id`) USING BTREE,
  INDEX `idx_artifact_tags_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物与标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact_types
-- ----------------------------
DROP TABLE IF EXISTS `artifact_types`;
CREATE TABLE `artifact_types`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称（如：绘画、瓷器、青铜器、书法、雕塑、玉器等）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '类型描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物类型管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifacts
-- ----------------------------
DROP TABLE IF EXISTS `artifacts`;
CREATE TABLE `artifacts`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `object_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '博物馆原始ID或系统生成唯一标识',
  `title_zh` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名称',
  `title_en` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文原始名称',
  `time_period` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年代描述（原始）',
  `dynasty_id` int UNSIGNED NULL DEFAULT NULL COMMENT '所属朝代ID',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文物类型（如 Painting, Ceramics）',
  `material` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '材质',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文物介绍文本',
  `dimensions` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '尺寸',
  `museum_id` int UNSIGNED NOT NULL COMMENT '现藏博物馆ID',
  `location_id` int UNSIGNED NULL DEFAULT NULL COMMENT '博物馆所在地ID',
  `detail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '博物馆详情页URL',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主图原图URL',
  `image_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '本地存储相对路径',
  `credit_line` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版权/来源说明',
  `accession_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '馆藏编号',
  `crawl_date` date NULL DEFAULT NULL COMMENT '爬取日期',
  `image_validated` tinyint NULL DEFAULT NULL COMMENT '图片有效性验证 0/1',
  `last_updated` timestamp NULL DEFAULT NULL COMMENT '最后更新时间',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `provenance` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文物流转或来源脉络',
  `current_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前展出、保存或数字开放状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_museum_object`(`museum_id` ASC, `object_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6275 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artists
-- ----------------------------
DROP TABLE IF EXISTS `artists`;
CREATE TABLE `artists`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name_zh` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名',
  `name_en` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名',
  `birth_year` int NULL DEFAULT NULL COMMENT '生年',
  `death_year` int NULL DEFAULT NULL COMMENT '卒年',
  `dynasty_id` int UNSIGNED NULL DEFAULT NULL COMMENT '主要活跃朝代ID',
  `biography` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '生平介绍',
  `baidu_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '百度百科链接',
  `wiki_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维基百科链接',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 343 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '艺术家（书画家等）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for audit_records
-- ----------------------------
DROP TABLE IF EXISTS `audit_records`;
CREATE TABLE `audit_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容ID（关联的具体内容）',
  `content_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容类型：comment/post/upload等',
  `source_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源表名',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '待审核内容',
  `submitter_id` int UNSIGNED NOT NULL COMMENT '提交者用户ID',
  `auto_audit_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '自动审核结果：pending/approved/rejected',
  `manual_audit_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '人工审核结果：pending/approved/rejected',
  `auditor_id` int UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_content_type`(`content_type` ASC) USING BTREE,
  INDEX `idx_manual_result`(`manual_audit_result` ASC) USING BTREE,
  INDEX `idx_submitter`(`submitter_id` ASC) USING BTREE,
  INDEX `idx_created`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for audit_strategies
-- ----------------------------
DROP TABLE IF EXISTS `audit_strategies`;
CREATE TABLE `audit_strategies`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容类型：post/comment/upload',
  `auto_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'auto_review' COMMENT '自动审核模式：auto_pass/auto_review/auto_reject',
  `enable_sensitive_check` tinyint NULL DEFAULT 1 COMMENT '启用敏感词检测：1-是 0-否',
  `enable_image_check` tinyint NULL DEFAULT 0 COMMENT '启用图片审核：1-是 0-否',
  `risk_threshold` int NULL DEFAULT 3 COMMENT '风险阈值，1-5，超过则转人工',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核策略配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for backup_records
-- ----------------------------
DROP TABLE IF EXISTS `backup_records`;
CREATE TABLE `backup_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `backup_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '备份名称',
  `backup_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '备份类型：full/incremental/export',
  `scope` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备份范围',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存储路径',
  `operator_id` int UNSIGNED NULL DEFAULT NULL COMMENT '操作人ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-进行中 1-已完成 2-失败',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '备份记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for backup_strategies
-- ----------------------------
DROP TABLE IF EXISTS `backup_strategies`;
CREATE TABLE `backup_strategies`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `auto_backup_enabled` tinyint NULL DEFAULT 0 COMMENT '是否开启自动备份：1-是 0-否',
  `backup_cron` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0 0 2 * * ?' COMMENT 'Cron表达式，默认每日凌晨2点',
  `backup_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'full' COMMENT '默认备份类型：full/incremental/export',
  `retention_days` int NULL DEFAULT 30 COMMENT '备份保留天数',
  `encrypt_enabled` tinyint NULL DEFAULT 1 COMMENT '是否加密存储：1-是 0-否',
  `notify_on_failure` tinyint NULL DEFAULT 1 COMMENT '失败是否通知：1-是 0-否',
  `storage_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT './backups' COMMENT '备份文件存储路径',
  `storage_warning_threshold` bigint NULL DEFAULT 10737418240 COMMENT '存储告警阈值（字节），默认10GB',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '备份策略配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment_likes
-- ----------------------------
DROP TABLE IF EXISTS `comment_likes`;
CREATE TABLE `comment_likes`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `comment_id` int UNSIGNED NOT NULL COMMENT '评论ID',
  `liked_at` datetime NULL DEFAULT NULL COMMENT '点赞时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for crawl_task_logs
-- ----------------------------
DROP TABLE IF EXISTS `crawl_task_logs`;
CREATE TABLE `crawl_task_logs`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `task_id` int UNSIGNED NOT NULL COMMENT '任务ID',
  `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务名称(冗余)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'running' COMMENT '执行状态: running, success, failed',
  `crawled_count` int NOT NULL DEFAULT 0 COMMENT '爬取数量',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '当前重试次数',
  `executor_id` int NULL DEFAULT NULL COMMENT '执行者ID(0=系统)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `fk_crawl_task_logs_task` FOREIGN KEY (`task_id`) REFERENCES `crawl_tasks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '爬取任务执行日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for crawl_tasks
-- ----------------------------
DROP TABLE IF EXISTS `crawl_tasks`;
CREATE TABLE `crawl_tasks`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `task_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务编码',
  `source_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '数据源URL',
  `source_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'web' COMMENT '数据源类型',
  `crawl_rule` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '爬取规则JSON',
  `priority` int NOT NULL DEFAULT 1 COMMENT '优先级',
  `cron_expression` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '定时表达式',
  `max_retry` int NOT NULL DEFAULT 3 COMMENT '最大重试次数',
  `retry_delay` int NOT NULL DEFAULT 60 COMMENT '重试间隔(秒)',
  `timeout_seconds` int NOT NULL DEFAULT 300 COMMENT '超时时间(秒)',
  `last_run_time` datetime NULL DEFAULT NULL COMMENT '最后执行时间',
  `next_run_time` datetime NULL DEFAULT NULL COMMENT '下次执行时间',
  `total_runs` int NOT NULL DEFAULT 0 COMMENT '总执行次数',
  `success_runs` int NOT NULL DEFAULT 0 COMMENT '成功次数',
  `fail_runs` int NOT NULL DEFAULT 0 COMMENT '失败次数',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务描述',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_by` int NULL DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `museum_id` int UNSIGNED NOT NULL COMMENT '博物馆ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'idle' COMMENT '状态: idle-空闲, running-运行中, paused-已暂停, completed-已完成, failed-失败',
  `items_crawled` int NULL DEFAULT NULL COMMENT '爬取总数',
  `items_new` int NULL DEFAULT NULL COMMENT '新增数量',
  `items_updated` int NULL DEFAULT NULL COMMENT '更新数量',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_task_code`(`task_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_enabled`(`enabled` ASC) USING BTREE,
  INDEX `idx_next_run_time`(`next_run_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '爬取任务记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dynasties
-- ----------------------------
DROP TABLE IF EXISTS `dynasties`;
CREATE TABLE `dynasties`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name_zh` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文朝代名称',
  `name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名称',
  `start_year` int NULL DEFAULT NULL COMMENT '起始年份（公元前为负）',
  `end_year` int NULL DEFAULT NULL COMMENT '结束年份',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '朝代简介',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '历史朝代' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name_zh` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文名称',
  `name_en` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名称',
  `parent_id` int UNSIGNED NULL DEFAULT NULL COMMENT '上级地点ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型：country/province/city/site',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地理地点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for museums
-- ----------------------------
DROP TABLE IF EXISTS `museums`;
CREATE TABLE `museums`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '博物馆官方完整名称',
  `short_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简称',
  `country` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '国家',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '官网URL',
  `collection_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '藏品搜索URL',
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '博物馆纬度',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '博物馆经度',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博物馆信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '接收用户ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型：audit_result/new_follower等',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `is_read` tinyint(1) NULL DEFAULT NULL COMMENT '是否已读',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附加数据(JSON)',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '操作用户ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型：INSERT/UPDATE/DELETE等',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作目标类型（表名/模块）',
  `target_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作对象ID',
  `old_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更前数据(JSON)',
  `new_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更后数据(JSON)',
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP',
  `user_agent` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `created_at` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for penalty_records
-- ----------------------------
DROP TABLE IF EXISTS `penalty_records`;
CREATE TABLE `penalty_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '被处罚用户ID',
  `penalty_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '处罚类型：warning/temp_ban/permanent_ban等',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '处罚原因',
  `operator_id` int UNSIGNED NOT NULL COMMENT '操作人ID',
  `penalty_time` datetime NOT NULL COMMENT '处罚时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间（临时封禁）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-生效 0-已解除',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_penalty_time`(`penalty_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '处罚记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识（artifact:edit）',
  `display_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示名称',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属模块',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_likes
-- ----------------------------
DROP TABLE IF EXISTS `post_likes`;
CREATE TABLE `post_likes`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `post_id` int UNSIGNED NOT NULL COMMENT '动态ID',
  `liked_at` datetime NULL DEFAULT NULL COMMENT '点赞时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '动态点赞' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_failed_question
-- ----------------------------
DROP TABLE IF EXISTS `qa_failed_question`;
CREATE TABLE `qa_failed_question`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '失败问题记录主键',
  `qa_log_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '问答日志 ID，关联 qa_log.id，日志写入失败时可为空',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话 ID，便于追溯上下文',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户 ID，关联 users.id，未登录可为空',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户原始问题',
  `normalized_question` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规范化后的问题文本',
  `question_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问题哈希，建议 SHA-256，用于统计重复失败问题',
  `intent` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '识别到的意图编码',
  `failure_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '失败类型：no_data/need_clarification/unsupported/retrieval_error/generation_error，对应 API status 映射见设计文档',
  `artifact_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联文物内部 ID，关联 artifacts.id',
  `object_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联文物 object_id',
  `error_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误详情或无数据原因',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'open' COMMENT '处理状态：open/ignored/resolved',
  `handled_by` int UNSIGNED NULL DEFAULT NULL COMMENT '处理人 ID，关联 users.id',
  `handled_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_qa_failed_log`(`qa_log_id` ASC) USING BTREE,
  INDEX `idx_qa_failed_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_qa_failed_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_qa_failed_hash`(`question_hash` ASC) USING BTREE,
  INDEX `idx_qa_failed_intent`(`intent` ASC) USING BTREE,
  INDEX `idx_qa_failed_type_created`(`failure_type` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_qa_failed_object`(`object_id` ASC) USING BTREE,
  INDEX `idx_qa_failed_status`(`status` ASC) USING BTREE,
  INDEX `idx_qa_failed_artifact`(`artifact_id` ASC) USING BTREE,
  INDEX `idx_qa_failed_handled_by`(`handled_by` ASC) USING BTREE,
  CONSTRAINT `fk_qa_failed_artifact` FOREIGN KEY (`artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_failed_handled_by` FOREIGN KEY (`handled_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_failed_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_failed_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_failed_status` CHECK (`status` in (_utf8mb4'open',_utf8mb4'ignored',_utf8mb4'resolved')),
  CONSTRAINT `chk_qa_failed_type` CHECK (`failure_type` in (_utf8mb4'no_data',_utf8mb4'need_clarification',_utf8mb4'unsupported',_utf8mb4'retrieval_error',_utf8mb4'generation_error'))
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答失败问题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_feedback
-- ----------------------------
DROP TABLE IF EXISTS `qa_feedback`;
CREATE TABLE `qa_feedback`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户反馈记录主键',
  `qa_log_id` bigint UNSIGNED NOT NULL COMMENT '问答日志 ID，关联 qa_log.id',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '反馈用户 ID，关联 users.id，未登录可为空',
  `feedback_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈类型：helpful/inaccurate',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户补充说明',
  `source_client` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调用来源，如 web、app、demo',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qa_feedback_log_user_type`(`qa_log_id` ASC, `user_id` ASC, `feedback_type` ASC) USING BTREE,
  INDEX `idx_qa_feedback_log`(`qa_log_id` ASC) USING BTREE,
  INDEX `idx_qa_feedback_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_qa_feedback_type_created`(`feedback_type` ASC, `created_at` ASC) USING BTREE,
  CONSTRAINT `fk_qa_feedback_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_feedback_type` CHECK (`feedback_type` in (_utf8mb4'helpful',_utf8mb4'inaccurate'))
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_intent_template
-- ----------------------------
DROP TABLE IF EXISTS `qa_intent_template`;
CREATE TABLE `qa_intent_template`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '意图模板主键',
  `intent_code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '意图编码，如 artifact_material',
  `intent_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '意图中文名称，如文物材质',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '意图说明',
  `data_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主要数据源：mysql/neo4j/mixed/template',
  `question_patterns_json` json NULL COMMENT '常见问法模板 JSON',
  `mysql_query_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'MySQL 查询模板，仅作配置参考，不直接动态执行',
  `neo4j_query_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Neo4j 查询模板，仅作配置参考，不直接动态执行',
  `answer_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回答模板',
  `no_data_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '暂无数据兜底模板',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1 启用，0 停用',
  `version` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'v1' COMMENT '模板版本',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qa_intent_code`(`intent_code` ASC) USING BTREE,
  INDEX `idx_qa_intent_enabled`(`enabled` ASC) USING BTREE,
  INDEX `idx_qa_intent_source`(`data_source` ASC) USING BTREE,
  CONSTRAINT `chk_qa_intent_enabled` CHECK (`enabled` in (0,1)),
  CONSTRAINT `chk_qa_intent_source` CHECK (`data_source` in (_utf8mb4'mysql',_utf8mb4'neo4j',_utf8mb4'mixed',_utf8mb4'template'))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答意图与回答模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_log
-- ----------------------------
DROP TABLE IF EXISTS `qa_log`;
CREATE TABLE `qa_log`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '问答日志内部主键',
  `qa_log_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对外问答日志 ID，对应 API qaLogId',
  `session_pk` bigint UNSIGNED NULL DEFAULT NULL COMMENT '会话表内部主键，关联 qa_session.id',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口层会话 ID，冗余保存便于查询',
  `conversation_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端对话 ID',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '提问用户 ID，关联 users.id，未登录可为空',
  `request_object_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求体或 URL 原始传入的 objectId',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户原始问题',
  `normalized_question` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规范化后的问题文本',
  `intent` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '识别到的意图编码',
  `intent_confidence` decimal(5, 4) NULL DEFAULT NULL COMMENT '意图识别置信度，建议 0 到 1',
  `intent_detail_json` json NULL COMMENT '意图识别和实体抽取调试信息，如 matchedKeywords、entities、confidence',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回答状态：answered/no_data/need_clarification/unsupported/error',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回给用户的自然语言答案',
  `fact_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '事实内容，来自 MySQL、Neo4j 或确认数据源',
  `supplemental_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板或大模型生成的补充性描述',
  `artifact_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联文物内部 ID，关联 artifacts.id',
  `object_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联文物 object_id，对应 API objectId',
  `resolve_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文物解析来源，如 question_entity/request_object_id/session_context',
  `candidates_json` json NULL COMMENT '多候选文物列表，need_clarification 时使用',
  `source_client` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调用来源，如 web、app、demo',
  `retrieval_raw_json` json NULL COMMENT '检索原始调试数据摘要',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息，error 状态时使用',
  `latency_ms` int UNSIGNED NULL DEFAULT NULL COMMENT '本次问答处理耗时，单位毫秒',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '问答发生时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qa_log_uuid`(`qa_log_uuid` ASC) USING BTREE,
  INDEX `idx_qa_log_session_pk`(`session_pk` ASC) USING BTREE,
  INDEX `idx_qa_log_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_qa_log_user_created`(`user_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_qa_log_request_object`(`request_object_id` ASC) USING BTREE,
  INDEX `idx_qa_log_object`(`object_id` ASC) USING BTREE,
  INDEX `idx_qa_log_intent_status`(`intent` ASC, `status` ASC) USING BTREE,
  INDEX `idx_qa_log_created`(`created_at` ASC) USING BTREE,
  INDEX `fk_qa_log_artifact`(`artifact_id` ASC) USING BTREE,
  CONSTRAINT `fk_qa_log_artifact` FOREIGN KEY (`artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_log_session` FOREIGN KEY (`session_pk`) REFERENCES `qa_session` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_log_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_log_intent_confidence` CHECK ((`intent_confidence` is null) or ((`intent_confidence` >= 0) and (`intent_confidence` <= 1))),
  CONSTRAINT `chk_qa_log_status` CHECK (`status` in (_utf8mb4'answered',_utf8mb4'no_data',_utf8mb4'need_clarification',_utf8mb4'unsupported',_utf8mb4'error'))
) ENGINE = InnoDB AUTO_INCREMENT = 336 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答主日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_review_task
-- ----------------------------
DROP TABLE IF EXISTS `qa_review_task`;
CREATE TABLE `qa_review_task`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '人工审核任务主键',
  `feedback_id` bigint UNSIGNED NOT NULL COMMENT '反馈记录 ID，关联 qa_feedback.id',
  `qa_log_id` bigint UNSIGNED NOT NULL COMMENT '问答日志 ID，关联 qa_log.id',
  `task_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '任务状态：pending/processing/done/closed',
  `review_result` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核结果：approved/rejected/fixed',
  `priority` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '优先级：1 低、2 中、3 高',
  `assigned_admin_id` int NULL DEFAULT NULL COMMENT '分配给的管理员 ID，关联 admin_users.id',
  `reviewer_admin_id` int NULL DEFAULT NULL COMMENT '实际审核管理员 ID，关联 admin_users.id',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核意见',
  `corrected_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '修正后的答案',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qa_review_feedback`(`feedback_id` ASC) USING BTREE,
  INDEX `idx_qa_review_log`(`qa_log_id` ASC) USING BTREE,
  INDEX `idx_qa_review_status_created`(`task_status` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_qa_review_assigned_admin`(`assigned_admin_id` ASC) USING BTREE,
  INDEX `idx_qa_review_reviewer_admin`(`reviewer_admin_id` ASC) USING BTREE,
  CONSTRAINT `fk_qa_review_assigned_admin` FOREIGN KEY (`assigned_admin_id`) REFERENCES `admin_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_review_feedback` FOREIGN KEY (`feedback_id`) REFERENCES `qa_feedback` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_review_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_review_reviewer_admin` FOREIGN KEY (`reviewer_admin_id`) REFERENCES `admin_users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_review_priority` CHECK (`priority` between 1 and 3),
  CONSTRAINT `chk_qa_review_result` CHECK ((`review_result` is null) or (`review_result` in (_utf8mb4'approved',_utf8mb4'rejected',_utf8mb4'fixed'))),
  CONSTRAINT `chk_qa_review_status` CHECK (`task_status` in (_utf8mb4'pending',_utf8mb4'processing',_utf8mb4'done',_utf8mb4'closed'))
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答不准确反馈人工审核任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_session
-- ----------------------------
DROP TABLE IF EXISTS `qa_session`;
CREATE TABLE `qa_session`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话记录内部主键',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口层会话 ID，对应 API sessionId',
  `conversation_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端对话 ID，对应 API conversationId',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户 ID，关联 users.id，未登录可为空',
  `current_artifact_id` int UNSIGNED NULL DEFAULT NULL COMMENT '当前上下文文物内部 ID，关联 artifacts.id',
  `current_object_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前上下文文物 object_id',
  `last_intent` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最近一次识别到的意图编码',
  `recent_context_json` json NULL COMMENT '最近上下文摘要，建议保存最近 5 轮问答',
  `source_client` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调用来源，如 web、app、demo',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '会话状态：active/closed/expired',
  `last_active_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近活跃时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qa_session_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_qa_session_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_qa_session_object`(`current_object_id` ASC) USING BTREE,
  INDEX `idx_qa_session_last_active`(`last_active_at` ASC) USING BTREE,
  INDEX `fk_qa_session_artifact`(`current_artifact_id` ASC) USING BTREE,
  CONSTRAINT `fk_qa_session_artifact` FOREIGN KEY (`current_artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_session_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_session_status` CHECK (`status` in (_utf8mb4'active',_utf8mb4'closed',_utf8mb4'expired'))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答会话上下文表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qa_source_record
-- ----------------------------
DROP TABLE IF EXISTS `qa_source_record`;
CREATE TABLE `qa_source_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '答案来源记录主键',
  `qa_log_id` bigint UNSIGNED NOT NULL COMMENT '问答日志 ID，关联 qa_log.id',
  `source_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源类型：mysql/neo4j/llm/template',
  `source_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源名称，如博物馆名称、Neo4j 知识图谱、模板名称',
  `source_table` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MySQL 来源表名，如 artifacts、artists',
  `source_record_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源记录 ID，可保存 MySQL 主键或 Neo4j 节点 ID',
  `artifact_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联文物内部 ID，关联 artifacts.id',
  `object_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联文物 object_id',
  `detail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始详情页链接',
  `fact_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '来源事实文本，对应 API sources[].factText',
  `source_payload_json` json NULL COMMENT '来源原始摘要，如查询字段、节点关系等',
  `confidence` decimal(5, 4) NULL DEFAULT NULL COMMENT '来源置信度，建议 0 到 1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_qa_source_log`(`qa_log_id` ASC) USING BTREE,
  INDEX `idx_qa_source_type`(`source_type` ASC) USING BTREE,
  INDEX `idx_qa_source_object`(`object_id` ASC) USING BTREE,
  INDEX `idx_qa_source_artifact`(`artifact_id` ASC) USING BTREE,
  CONSTRAINT `fk_qa_source_artifact` FOREIGN KEY (`artifact_id`) REFERENCES `artifacts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_qa_source_log` FOREIGN KEY (`qa_log_id`) REFERENCES `qa_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_qa_source_confidence` CHECK ((`confidence` is null) or ((`confidence` >= 0) and (`confidence` <= 1))),
  CONSTRAINT `chk_qa_source_type` CHECK (`source_type` in (_utf8mb4'mysql',_utf8mb4'neo4j',_utf8mb4'llm',_utf8mb4'template'))
) ENGINE = InnoDB AUTO_INCREMENT = 418 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答答案来源记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for restore_records
-- ----------------------------
DROP TABLE IF EXISTS `restore_records`;
CREATE TABLE `restore_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `backup_id` bigint UNSIGNED NOT NULL COMMENT '关联的备份记录ID',
  `backup_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备份名称快照',
  `operator_id` int UNSIGNED NOT NULL COMMENT '操作人ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-进行中 1-成功 2-失败',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注/失败原因',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_backup_id`(`backup_id` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据恢复记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions`  (
  `role_id` int UNSIGNED NOT NULL COMMENT '角色ID',
  `permission_id` int UNSIGNED NOT NULL COMMENT '权限ID',
  INDEX `idx_role_perm`(`role_id` ASC, `permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识（super_admin等）',
  `display_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '显示名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for security_logs
-- ----------------------------
DROP TABLE IF EXISTS `security_logs`;
CREATE TABLE `security_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '相关用户ID',
  `event_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事件类型：login_failed/permission_change等',
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细信息',
  `created_at` datetime NULL DEFAULT NULL COMMENT '发生时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '安全日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensitive_words
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_words`;
CREATE TABLE `sensitive_words`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '敏感词',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类：political/pornographic/violence/advertising/other',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '级别：high/medium/low',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_word`(`word` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '敏感词库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for system_alerts
-- ----------------------------
DROP TABLE IF EXISTS `system_alerts`;
CREATE TABLE `system_alerts`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `alert_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警类型: interface_timeout/db_failure/disk_full/high_memory/cpu_overload/service_error',
  `severity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'warning' COMMENT '严重级别: info/warning/critical',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警标题',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '告警详情',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '告警来源(服务名/模块名)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态: active-活跃, resolved-已解决, ignored-已忽略',
  `resolved_by` int NULL DEFAULT NULL COMMENT '处理人ID',
  `resolved_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `resolve_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_alert_type`(`alert_type` ASC) USING BTREE,
  INDEX `idx_severity`(`severity` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统告警记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'string' COMMENT '值类型: string/number/boolean/json',
  `config_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'general' COMMENT '配置分组',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `editable` tinyint NOT NULL DEFAULT 1 COMMENT '是否可编辑',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `updated_by` int UNSIGNED NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_group`(`config_group` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `log_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日志级别(DEBUG,INFO,WARN,ERROR)',
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块名称',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日志信息',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细内容(JSON格式)',
  `created_at` datetime NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统运行日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签类别',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tags_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_behaviors
-- ----------------------------
DROP TABLE IF EXISTS `user_behaviors`;
CREATE TABLE `user_behaviors`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `behavior_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行为类型：login/comment/publish/upload/like/favorite/browse/follow',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标类型：artifact/post/comment/user',
  `target_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标对象ID',
  `target_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标描述',
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `device` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '行为详情(JSON)',
  `created_at` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_behavior_type`(`behavior_type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_browse_history
-- ----------------------------
DROP TABLE IF EXISTS `user_browse_history`;
CREATE TABLE `user_browse_history`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `browse_time` datetime NULL DEFAULT NULL COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户浏览历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_comments
-- ----------------------------
DROP TABLE IF EXISTS `user_comments`;
CREATE TABLE `user_comments`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `parent_id` int UNSIGNED NULL DEFAULT NULL COMMENT '父评论ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `like_count` int NULL DEFAULT NULL COMMENT '点赞数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态：pending/approved/rejected',
  `audit_by` int UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户评论' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `user_favorites`;
CREATE TABLE `user_favorites`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `artifact_id` int UNSIGNED NOT NULL COMMENT '文物ID',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收藏分组名称',
  `created_at` datetime NULL DEFAULT NULL COMMENT '收藏时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收藏文物' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_follows
-- ----------------------------
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows`  (
  `follower_id` int UNSIGNED NOT NULL COMMENT '关注者用户ID',
  `followee_id` int UNSIGNED NOT NULL COMMENT '被关注者用户ID',
  `created_at` datetime NULL DEFAULT NULL COMMENT '关注时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_posts
-- ----------------------------
DROP TABLE IF EXISTS `user_posts`;
CREATE TABLE `user_posts`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动态内容',
  `artifact_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联文物ID',
  `museum_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联博物馆ID',
  `image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图片URL列表（JSON或逗号分隔）',
  `like_count` int NULL DEFAULT NULL COMMENT '点赞数',
  `comment_count` int NULL DEFAULT NULL COMMENT '评论数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核状态：pending/approved/rejected',
  `audit_by` int UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户动态' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` int UNSIGNED NOT NULL COMMENT '角色ID',
  `granted_by` int UNSIGNED NULL DEFAULT NULL COMMENT '授权人用户ID',
  `granted_at` datetime NULL DEFAULT NULL COMMENT '授权时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_uploads
-- ----------------------------
DROP TABLE IF EXISTS `user_uploads`;
CREATE TABLE `user_uploads`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `artifact_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联文物ID',
  `media_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '媒体类型：image/audio/video',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '本地存储路径',
  `caption` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户描述',
  `location_taken` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拍摄地点',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核状态：pending/approved/rejected',
  `audit_by` int UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `like_count` int NULL DEFAULT NULL COMMENT '点赞数',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户上传内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态：active/disabled/banned',
  `ban_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '封禁原因',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `registered_at` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注册来源：web/app',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `comment_disabled` tinyint NULL DEFAULT 0 COMMENT '是否禁止评论',
  `upload_disabled` tinyint NULL DEFAULT 0 COMMENT '是否禁止上传',
  `institution` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户所属机构',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '统一用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for violation_types
-- ----------------------------
DROP TABLE IF EXISTS `violation_types`;
CREATE TABLE `violation_types`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '违规类型编码',
  `type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '违规类型名称',
  `severity_level` tinyint NOT NULL COMMENT '严重等级：1-轻微 2-一般 3-严重 4-特别严重',
  `default_penalty` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '默认处罚：warning/temp_ban/permanent_ban',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述说明',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `type_code`(`type_code` ASC) USING BTREE,
  INDEX `idx_severity`(`severity_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '违规类型配置表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
