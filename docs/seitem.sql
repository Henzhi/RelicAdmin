/*
 Navicat Premium Dump SQL

 Source Server         : seitem
 Source Server Type    : MySQL
 Source Server Version : 80403 (8.4.3-SQLPub-0.0.1)
 Source Host           : mysql6.sqlpub.com:3311
 Source Schema         : seitem

 Target Server Type    : MySQL
 Target Server Version : 80403 (8.4.3-SQLPub-0.0.1)
 File Encoding         : 65001

 Date: 31/05/2026 17:54:24
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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user_role
-- ----------------------------
INSERT INTO `admin_user_role` VALUES (3, 2, 5, '2026-05-15 22:19:00');
INSERT INTO `admin_user_role` VALUES (4, 3, 4, '2026-05-17 13:22:00');
INSERT INTO `admin_user_role` VALUES (5, 4, 4, '2026-05-17 13:23:00');
INSERT INTO `admin_user_role` VALUES (6, 1, 1, '2026-05-22 19:43:00');
INSERT INTO `admin_user_role` VALUES (7, 5, 1, '2026-05-23 17:06:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员用户表（独立于前台用户表）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_users
-- ----------------------------
INSERT INTO `admin_users` VALUES (1, 'admin', '$2a$10$xyPc5FA9AhAEruAnbqHZ3eHdGJfrfExFr3x3RZELwe6nX5O.WxOWO', '666', 'mahh315@163.com', '13321126702', 'https://seitem.oss-cn-beijing.aliyuncs.com/avatar/1/3dc52a60-136b-4408-a0bd-fa22f9943a26.jpg', 'active', '2026-05-28 21:15:00', '', '2026-05-14 21:51:02', '2026-05-28 21:15:36');
INSERT INTO `admin_users` VALUES (2, 'admin1', '$2a$10$Wcihf23mOo0d4GkBgdgEyOyHBdV4oe0tem2DX39VzvM/jQSNYKuJ6', '数据管理员', NULL, NULL, NULL, 'active', NULL, NULL, '2026-05-15 22:19:53', '2026-05-15 22:19:53');
INSERT INTO `admin_users` VALUES (3, 'admin2', '$2a$10$xw7gY8M7PkdZWzIYqYIS2uu00MlgqmN578bLSy8wkm7wpkjEcfFYq', NULL, NULL, NULL, NULL, 'active', '2026-05-17 13:23:00', '', '2026-05-17 13:23:00', '2026-05-17 13:23:14');
INSERT INTO `admin_users` VALUES (4, 'admin3', '$2a$10$I.VcYnaG1fSFHwfjpb7aWuruv0A1ulvP5lox5MRigpQxVv7linwOi', '内容审核员', NULL, NULL, NULL, 'active', NULL, NULL, '2026-05-17 13:23:53', '2026-05-17 13:23:53');
INSERT INTO `admin_users` VALUES (5, 'adminlzl', '$2a$10$qoCwPiyCZSlHkGAMDPhnrenFelJYKK.paM.XWE0tD4j9lamuzhKoK', NULL, NULL, NULL, NULL, 'active', '2026-05-23 17:07:00', '', '2026-05-23 17:06:53', '2026-05-23 17:07:07');

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
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, 'test', '这是一条测试公告。。', 'global', 'all', '2026-05-06 00:00:00', '2026-05-30 00:00:00', 0, 1, 0, '2026-05-16 19:49:47', '2026-05-16 19:51:24');
INSERT INTO `announcements` VALUES (2, 'test', '这是第二条测试公告~', 'global', 'all', '2026-05-16 19:51:50', '2026-05-30 00:00:00', 1, 1, 0, '2026-05-16 19:51:53', NULL);
INSERT INTO `announcements` VALUES (3, 'test', 'test', 'global', 'all', '2026-05-22 15:06:32', '2026-05-23 00:00:00', 1, 1, 0, '2026-05-22 15:06:36', NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '申诉记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appeal_records
-- ----------------------------

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
-- Records of artifact_artist
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物多图片' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artifact_images
-- ----------------------------
INSERT INTO `artifact_images` VALUES (2, 1, 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/1/b3f95ab1-ecc2-4792-8946-5455d6c23604.jpg', '', 1, 0);
INSERT INTO `artifact_images` VALUES (3, 2, 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/2/93a46179-95bc-43f3-9d70-e0e50efc409b.jpg', NULL, 0, 0);
INSERT INTO `artifact_images` VALUES (6, 2, 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/2/04b01205-6d34-46cc-9b28-10105f636d0b.jpg', '', 0, 2);
INSERT INTO `artifact_images` VALUES (7, 2, 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/2/e05f8195-99b7-4ee4-911d-7530a9f3ae29.jpg', '', 1, 3);
INSERT INTO `artifact_images` VALUES (8, 3, 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/3/4b68e154-7a34-48d0-9e63-ee8914831348.jpg', '', 1, 0);
INSERT INTO `artifact_images` VALUES (9, 4, 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/4/ee565913-f8bc-46e0-9ec3-956c7ad90fff.png', '', 0, 1);

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
-- Records of artifact_likes
-- ----------------------------
INSERT INTO `artifact_likes` VALUES (9, 1, NULL);
INSERT INTO `artifact_likes` VALUES (9, 4, '2026-05-01 00:00:00');

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
-- Records of artifact_location
-- ----------------------------

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
-- Records of artifact_tags
-- ----------------------------

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
-- Records of artifact_types
-- ----------------------------
INSERT INTO `artifact_types` VALUES (1, 'Painting', '绘画作品，包括国画、油画、水彩等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (2, 'Ceramics', '陶瓷器物，包括瓷器、陶器等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (3, 'Bronze', '青铜器，包括礼器、兵器、工具等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (4, 'Calligraphy', '书法作品，包括碑帖、手卷等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (5, 'Sculpture', '雕塑作品，包括石雕、木雕、泥塑等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (6, 'Jade', '玉器，包括玉璧、玉佩、玉雕等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (7, 'Lacquer', '漆器', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (8, 'Textile', '纺织品，包括丝绸、刺绣等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (9, 'Metalwork', '金属工艺品，包括金银器等', '2026-05-11 22:00:37', '2026-05-11 22:00:37');
INSERT INTO `artifact_types` VALUES (10, 'Enamel', '珐琅器', '2026-05-11 22:00:37', '2026-05-11 22:00:37');

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artifacts
-- ----------------------------
INSERT INTO `artifacts` VALUES (1, 't', 'te', 'te', 't', 2, 'Ceramics', 'te', '', '', 3, NULL, '', '', NULL, 't', 'te', '2026-05-01', 0, '2026-05-11 22:09:09', '2026-05-11 20:50:59', NULL, NULL);
INSERT INTO `artifacts` VALUES (2, '01', 'test2', 'test', '2026', 2, 'Bronze', 'test', 'test', 'test', 3, 1, '', 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/2/e05f8195-99b7-4ee4-911d-7530a9f3ae29.jpg', NULL, 'test', 'test', '2026-05-01', 0, '2026-05-12 11:12:14', '2026-05-12 10:38:07', NULL, NULL);
INSERT INTO `artifacts` VALUES (3, '', 'teset', '', '', NULL, 'Painting', '', '', '', 3, NULL, '', 'https://seitem.oss-cn-beijing.aliyuncs.com/artifact/3/4b68e154-7a34-48d0-9e63-ee8914831348.jpg', NULL, '', '', '2026-05-12', 0, '2026-05-12 11:33:05', '2026-05-12 11:32:39', NULL, NULL);
INSERT INTO `artifacts` VALUES (4, 'test', 'test', 'tes', 'test', 2, 'Painting', 'tes', 'test', 'tes', 3, 4, 'test', '', NULL, 'tes', 'tes', '2026-05-09', 0, '2026-05-12 11:33:46', '2026-05-12 11:33:46', NULL, NULL);
INSERT INTO `artifacts` VALUES (5, '', '测试', '', '', NULL, 'Painting', '', '', '', 3, NULL, '', '', NULL, '', '', '2026-05-22', 0, '2026-05-22 17:53:43', '2026-05-22 17:53:43', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '艺术家（书画家等）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artists
-- ----------------------------
INSERT INTO `artists` VALUES (1, 'TES', 'SET', 1, 2, 2, 'TES', 'ESTT', 'SET', '2026-05-11 19:58:14', '2026-05-11 19:58:14');
INSERT INTO `artists` VALUES (2, 'AA', 'AA', NULL, NULL, 2, '', '', '', '2026-05-11 19:58:27', '2026-05-11 22:01:55');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_records
-- ----------------------------

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
-- Records of audit_strategies
-- ----------------------------
INSERT INTO `audit_strategies` VALUES (1, 'post', 'auto_pass', 1, 1, 2, 1, '2026-05-16 20:45:28', '2026-05-16 21:34:10');
INSERT INTO `audit_strategies` VALUES (2, 'comment', 'auto_pass', 1, 1, 1, 1, '2026-05-16 20:45:28', '2026-05-16 21:34:00');
INSERT INTO `audit_strategies` VALUES (3, 'upload', 'auto_review', 1, 1, 4, 1, '2026-05-16 20:45:28', '2026-05-16 20:46:25');

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
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '备份记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of backup_records
-- ----------------------------
INSERT INTO `backup_records` VALUES (11, 'test3', 'full', '全部数据', 0, './backups\\test3.sql', 1, 0, '备份进行中...', '2026-05-17 17:03:42');
INSERT INTO `backup_records` VALUES (12, 'test5', 'full', '全部数据', 0, './backups\\test5.sql', 1, 0, '备份进行中...', '2026-05-17 17:11:11');
INSERT INTO `backup_records` VALUES (13, 'test', 'full', '全部数据', 54994, './backups\\test.sql', 1, 1, '备份完成', '2026-05-17 17:18:33');
INSERT INTO `backup_records` VALUES (14, 'test7', 'full', '全部数据', 0, './backups\\test7.sql', 1, 0, '备份进行中...', '2026-05-17 17:20:02');
INSERT INTO `backup_records` VALUES (15, 'test8', 'full', '全部数据', 0, './backups\\test8.sql', 1, 0, '备份进行中...', '2026-05-17 17:21:32');
INSERT INTO `backup_records` VALUES (16, 'test4', 'full', '全部数据', 0, './backups\\test4.sql', 1, 0, '备份进行中...', '2026-05-17 17:27:47');
INSERT INTO `backup_records` VALUES (17, 'test2', 'full', '全部数据', 0, './backups\\test2.sql', 1, 0, '备份进行中...', '2026-05-17 17:34:08');
INSERT INTO `backup_records` VALUES (18, 'test1', 'full', '全部数据', 0, './backups\\test1.sql', 1, 0, '备份进行中...', '2026-05-17 17:38:35');

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
-- Records of backup_strategies
-- ----------------------------
INSERT INTO `backup_strategies` VALUES (1, 0, '0 0 2 * * ?', 'full', 30, 1, 1, './backups', 10737418240, 1, '2026-05-17 13:52:19', NULL);

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
-- Records of comment_likes
-- ----------------------------

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
-- Records of crawl_task_logs
-- ----------------------------

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
-- Records of crawl_tasks
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '历史朝代' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dynasties
-- ----------------------------
INSERT INTO `dynasties` VALUES (2, 'TEST', 'test', 1, 1, 'wdqwd', '2026-05-11 19:57:56');

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
-- Records of locations
-- ----------------------------
INSERT INTO `locations` VALUES (2, 'rw', 'rw', 1, 'province', '2026-05-11 20:48:36');
INSERT INTO `locations` VALUES (3, 'wrr', 'rww', 4, 'site', '2026-05-11 20:48:57');
INSERT INTO `locations` VALUES (4, 'tez', 'te', 2, 'city', '2026-05-11 22:19:41');
INSERT INTO `locations` VALUES (5, '美国', 'A', NULL, 'country', '2026-05-16 14:36:47');
INSERT INTO `locations` VALUES (6, '俄罗斯', 'a', NULL, 'country', '2026-05-16 14:37:32');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博物馆信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of museums
-- ----------------------------
INSERT INTO `museums` VALUES (2, 'tes', '', 'tes', '', '', '', '2026-05-11 19:56:19', '2026-05-11 19:56:19', NULL, NULL);
INSERT INTO `museums` VALUES (3, 'aa', 'wd', 'a', 'sdg', '', '', '2026-05-11 19:56:22', '2026-05-11 22:02:34', NULL, NULL);
INSERT INTO `museums` VALUES (4, '测试', 'cs', '测试', '测试', 'www.123.com', 'www.123.cn', '2026-05-22 17:54:30', '2026-05-22 17:54:30', NULL, NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notifications
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_logs
-- ----------------------------
INSERT INTO `operation_logs` VALUES (1, 1, 'INSERT', 'Announcement', '', NULL, 'AnnouncementCreateDTO(title=test, content=test, position=global, targetAudience=all, startTime=2026-05-22 15:06:32, endTime=2026-05-23 00:00:00, status=1)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '2026-05-22 15:06:36');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '处罚记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of penalty_records
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES (1, 'user:view', '查看用户', '用户管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (2, 'user:create', '创建用户', '用户管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (3, 'user:update', '修改用户', '用户管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (4, 'user:delete', '删除用户', '用户管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (5, 'role:view', '查看角色', '角色管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (6, 'role:create', '创建角色', '角色管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (7, 'role:update', '修改角色', '角色管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (8, 'role:delete', '删除角色', '角色管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (9, 'log:view', '查看日志', '日志管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (10, 'system:config', '系统配置', '系统管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (11, 'backup:manage', '备份管理', '系统管理', '2026-04-28 18:38:59');
INSERT INTO `permissions` VALUES (20, 'audit:view', '查看审核', '内容审核', '2026-04-29 09:45:43');
INSERT INTO `permissions` VALUES (21, 'audit:process', '处理审核', '内容审核', '2026-04-29 09:45:43');
INSERT INTO `permissions` VALUES (22, 'data:view', '查看数据', '数据管理', '2026-04-29 09:45:43');
INSERT INTO `permissions` VALUES (23, 'data:edit', '编辑数据', '数据管理', '2026-04-29 09:45:43');
INSERT INTO `permissions` VALUES (27, 'monitor:view', '查看监控', '系统监控', '2026-04-29 09:45:43');
INSERT INTO `permissions` VALUES (32, 'user:penalty', '用户处罚', '用户管理', '2026-04-29 16:52:29');
INSERT INTO `permissions` VALUES (38, 'audit:create', '创建审核', '内容审核', '2026-04-29 16:52:29');
INSERT INTO `permissions` VALUES (39, 'audit:update', '更新审核', '内容审核', '2026-04-29 16:52:29');
INSERT INTO `permissions` VALUES (40, 'audit:delete', '删除审核', '内容审核', '2026-04-29 16:52:29');
INSERT INTO `permissions` VALUES (43, 'data:backup', '数据备份', '数据管理', '2026-04-29 16:52:29');
INSERT INTO `permissions` VALUES (66, 'test', 'test', 'test', '2026-05-10 20:59:54');

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
-- Records of post_likes
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答失败问题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qa_failed_question
-- ----------------------------
INSERT INTO `qa_failed_question` VALUES (1, 25, 'smoke-live-db', NULL, '???????????', '???????????', 'b43878df519ded204282979315e505166699221526234b3d4986c22db9c1445b', 'unknown', 'unsupported', NULL, 'DEMO_001', 'demo_fact_missing', 'open', NULL, NULL, '2026-05-30 15:34:48');

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
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qa_feedback
-- ----------------------------
INSERT INTO `qa_feedback` VALUES (1, 1, NULL, 'helpful', NULL, 'web', '2026-05-25 21:22:00');
INSERT INTO `qa_feedback` VALUES (3, 3, NULL, 'helpful', NULL, 'web', '2026-05-25 21:22:42');
INSERT INTO `qa_feedback` VALUES (4, 4, 9, 'inaccurate', '答案不正确。', 'demo', '2026-05-25 21:22:42');
INSERT INTO `qa_feedback` VALUES (5, 5, NULL, 'helpful', NULL, 'web', '2026-05-25 21:37:25');
INSERT INTO `qa_feedback` VALUES (6, 6, 9, 'inaccurate', '答案不正确，博物馆名称错误。', 'demo', '2026-05-25 21:37:25');
INSERT INTO `qa_feedback` VALUES (7, 7, NULL, 'inaccurate', '这件文物的收藏博物馆实际为大英博物馆。', 'web', '2026-05-25 21:37:26');
INSERT INTO `qa_feedback` VALUES (8, 8, NULL, 'helpful', NULL, 'web', '2026-05-25 21:37:44');
INSERT INTO `qa_feedback` VALUES (9, 9, 9, 'inaccurate', '答案不正确，博物馆名称错误。', 'demo', '2026-05-25 21:37:44');
INSERT INTO `qa_feedback` VALUES (10, 10, NULL, 'inaccurate', '这件文物的收藏博物馆实际为大英博物馆。', 'web', '2026-05-25 21:37:44');
INSERT INTO `qa_feedback` VALUES (11, 11, NULL, 'helpful', NULL, 'web', '2026-05-25 21:56:42');
INSERT INTO `qa_feedback` VALUES (12, 12, 9, 'inaccurate', '答案不正确，博物馆名称错误。', 'demo', '2026-05-25 21:56:42');
INSERT INTO `qa_feedback` VALUES (13, 13, NULL, 'inaccurate', '这件文物的收藏博物馆实际为大英博物馆。', 'web', '2026-05-25 21:56:43');
INSERT INTO `qa_feedback` VALUES (14, 14, NULL, 'helpful', NULL, 'web', '2026-05-25 21:59:35');
INSERT INTO `qa_feedback` VALUES (15, 15, 9, 'inaccurate', '答案不正确，博物馆名称错误。', 'demo', '2026-05-25 21:59:36');
INSERT INTO `qa_feedback` VALUES (16, 16, NULL, 'inaccurate', '这件文物的收藏博物馆实际为大英博物馆。', 'web', '2026-05-25 21:59:36');
INSERT INTO `qa_feedback` VALUES (17, 17, NULL, 'helpful', NULL, NULL, '2026-05-25 21:59:56');
INSERT INTO `qa_feedback` VALUES (18, 18, 9, 'inaccurate', NULL, NULL, '2026-05-25 21:59:57');
INSERT INTO `qa_feedback` VALUES (19, 19, 9, 'helpful', NULL, 'web', '2026-05-26 09:59:09');
INSERT INTO `qa_feedback` VALUES (20, 20, 9, 'inaccurate', '答案不正确，博物馆名称错误。', 'demo', '2026-05-26 10:01:53');
INSERT INTO `qa_feedback` VALUES (28, 21, 9, 'helpful', NULL, 'web', '2026-05-26 10:23:55');
INSERT INTO `qa_feedback` VALUES (29, 22, NULL, 'helpful', NULL, 'web', '2026-05-26 10:25:59');
INSERT INTO `qa_feedback` VALUES (30, 23, 9, 'inaccurate', '答案不正确，博物馆名称错误。', 'demo', '2026-05-26 10:26:00');
INSERT INTO `qa_feedback` VALUES (31, 24, NULL, 'inaccurate', '这件文物的收藏博物馆实际为大英博物馆。', 'web', '2026-05-26 10:26:00');
INSERT INTO `qa_feedback` VALUES (32, 25, NULL, 'inaccurate', 'smoke test feedback', 'smoke-test', '2026-05-30 15:34:48');
INSERT INTO `qa_feedback` VALUES (33, 27, NULL, 'helpful', NULL, 'smoke-test', '2026-05-30 15:38:17');
INSERT INTO `qa_feedback` VALUES (34, 30, NULL, 'helpful', NULL, 'smoke-test', '2026-05-31 13:20:24');
INSERT INTO `qa_feedback` VALUES (35, 38, NULL, 'helpful', NULL, 'smoke-test', '2026-05-31 13:24:27');
INSERT INTO `qa_feedback` VALUES (36, 50, NULL, 'helpful', NULL, 'web', '2026-05-31 14:14:39');

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
-- Records of qa_intent_template
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答主日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qa_log
-- ----------------------------
INSERT INTO `qa_log` VALUES (1, '11111111-1111-1111-1111-111111111001', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:22:00');
INSERT INTO `qa_log` VALUES (2, '11111111-1111-1111-1111-111111111002', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:22:00');
INSERT INTO `qa_log` VALUES (3, '22222222-2222-2222-2222-222222222001', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:22:42');
INSERT INTO `qa_log` VALUES (4, '22222222-2222-2222-2222-222222222002', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:22:42');
INSERT INTO `qa_log` VALUES (5, 'f6871e57-6ed2-49e0-8fa3-4958d4abfeb1', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:37:25');
INSERT INTO `qa_log` VALUES (6, '70547c1d-8b92-4b40-9291-3ec4dc5b3c1e', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:37:25');
INSERT INTO `qa_log` VALUES (7, 'a9a8860d-f548-43d9-a05d-b431db49a4ad', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:37:26');
INSERT INTO `qa_log` VALUES (8, '7adec46c-002c-408a-bc05-ad27badd5e37', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:37:44');
INSERT INTO `qa_log` VALUES (9, '8959b1a2-71b1-4530-a458-57cc52171774', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:37:44');
INSERT INTO `qa_log` VALUES (10, 'bd937a37-50b6-449d-8c7a-e8ef9425c26d', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:37:44');
INSERT INTO `qa_log` VALUES (11, 'a7338014-7a63-4a86-935d-52a4989ae94a', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:56:42');
INSERT INTO `qa_log` VALUES (12, '98eea1f0-b86d-4fff-b109-12e989f390db', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:56:42');
INSERT INTO `qa_log` VALUES (13, 'a76f1f3c-e639-4d61-91a4-e0114fba88dc', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:56:43');
INSERT INTO `qa_log` VALUES (14, '08848df3-f0c5-47b6-beff-4a4f43da01f4', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:59:35');
INSERT INTO `qa_log` VALUES (15, '38911e95-6383-4ebf-8409-eb3eb8cebf49', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:59:35');
INSERT INTO `qa_log` VALUES (16, 'b5203aba-19b0-4c3a-a5e5-aa4c0b6c6d2c', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:59:36');
INSERT INTO `qa_log` VALUES (17, '99999999-9999-9999-9999-999999999001', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:59:56');
INSERT INTO `qa_log` VALUES (18, '99999999-9999-9999-9999-999999999002', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-25 21:59:57');
INSERT INTO `qa_log` VALUES (19, 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-26 09:59:09');
INSERT INTO `qa_log` VALUES (20, 'aaaaaaaa-bbbb-cccc-dddd-ffffffffffff', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-26 10:01:53');
INSERT INTO `qa_log` VALUES (21, 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeee01', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-26 10:23:55');
INSERT INTO `qa_log` VALUES (22, 'a4f72eb6-df0b-4ded-8338-f4128465bb42', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-26 10:25:59');
INSERT INTO `qa_log` VALUES (23, 'd2648bcd-d280-4cfa-b4c0-8ee684405e91', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-26 10:26:00');
INSERT INTO `qa_log` VALUES (24, '6c1f8fd9-193a-488f-8ed5-775f6c1fb933', NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'no_data', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-26 10:26:00');
INSERT INTO `qa_log` VALUES (25, 'ddb67e1c-09eb-442c-8f28-7a51584576e1', NULL, 'smoke-live-db', NULL, NULL, 'DEMO_001', '???????????', '???????????', 'unknown', 0.0000, '{\"entities\": {}, \"confidence\": 0.0, \"matchedKeywords\": []}', 'unsupported', '当前问题暂未匹配到已支持的文物问答类型。你可以询问文物收藏地、年代、材质、类型、介绍、作者、尺寸或相关文物。', NULL, NULL, NULL, 'DEMO_001', 'request_object_id', '[]', 'smoke-test', '{\"reason\": \"demo_fact_missing\"}', NULL, NULL, '2026-05-30 15:34:47');
INSERT INTO `qa_log` VALUES (26, '49c69bbe-e803-47e4-94a0-084221290071', NULL, 'smoke-live-db-unicode', NULL, NULL, 'DEMO_001', '演示文物的材质是什么？', '演示文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', '演示文物 DEMO_001 的材质为 porcelain。', '演示文物 DEMO_001 的材质为 porcelain。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'request_object_id', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-30 15:35:06');
INSERT INTO `qa_log` VALUES (27, 'c30eabdf-f50b-4c5b-bd21-34eaa6f3161b', NULL, 'smoke-session', NULL, NULL, 'DEMO_001', '演示文物的材质是什么？', '演示文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', '演示文物 DEMO_001 的材质为 porcelain。', '演示文物 DEMO_001 的材质为 porcelain。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'request_object_id', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-30 15:38:14');
INSERT INTO `qa_log` VALUES (28, 'dae8b1b1-4d71-4075-ab01-c24586c7ec03', NULL, 'smoke-session', NULL, NULL, NULL, '它的尺寸是多少？', '它的尺寸是多少？', 'artifact_dimensions', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"尺寸\"]}', 'answered', '演示文物 DEMO_001 的尺寸为 H. 30 cm x W. 20 cm。', '演示文物 DEMO_001 的尺寸为 H. 30 cm x W. 20 cm。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'session_context', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-30 15:38:15');
INSERT INTO `qa_log` VALUES (29, '32542a48-909a-4d9a-ba55-9be674b19ade', NULL, NULL, NULL, NULL, NULL, '大英博物馆收藏了多少件中国文物？', '大英博物馆收藏了多少件中国文物？', 'statistics_count', 0.9000, '{\"entities\": {\"museum\": \"大英博物馆\"}, \"confidence\": 0.9, \"matchedKeywords\": [\"收藏\", \"多少\"]}', 'answered', '博物馆 大英博物馆 收藏了 0 件文物', '博物馆 大英博物馆 收藏了 0 件文物', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, NULL, 'not_required_for_intent', '[]', 'smoke-test', '{}', NULL, NULL, '2026-05-30 15:38:16');
INSERT INTO `qa_log` VALUES (30, '3a90dc51-f187-41fa-8624-cbf384363fcd', NULL, 'smoke-session', NULL, NULL, 'DEMO_001', '演示文物的材质是什么？', '演示文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', '演示文物 DEMO_001 的材质为 porcelain。', '演示文物 DEMO_001 的材质为 porcelain。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'request_object_id', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-31 13:20:22');
INSERT INTO `qa_log` VALUES (31, '1e747d04-6d49-4600-986d-323804530dca', NULL, 'smoke-session', NULL, NULL, NULL, '它的尺寸是多少？', '它的尺寸是多少？', 'artifact_dimensions', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"尺寸\"]}', 'answered', '演示文物 DEMO_001 的尺寸为 H. 30 cm x W. 20 cm。', '演示文物 DEMO_001 的尺寸为 H. 30 cm x W. 20 cm。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'session_context', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-31 13:20:23');
INSERT INTO `qa_log` VALUES (32, '26602962-ca3b-4cfc-943b-2743b2617923', NULL, NULL, NULL, NULL, NULL, '大英博物馆收藏了多少件中国文物？', '大英博物馆收藏了多少件中国文物？', 'statistics_count', 0.9000, '{\"entities\": {\"museum\": \"大英博物馆\"}, \"confidence\": 0.9, \"matchedKeywords\": [\"收藏\", \"多少\"]}', 'answered', '博物馆 大英博物馆 收藏了 0 件文物', '博物馆 大英博物馆 收藏了 0 件文物', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, NULL, 'not_required_for_intent', '[]', 'smoke-test', '{}', NULL, NULL, '2026-05-31 13:20:23');
INSERT INTO `qa_log` VALUES (33, '793f3cc6-57b8-4e5d-811e-4cf6e31d7711', NULL, NULL, NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'final-check', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 13:20:45');
INSERT INTO `qa_log` VALUES (34, 'a12b6d3f-999a-4cb2-80d9-164ed715dfbd', NULL, 'final-real-mysql', NULL, NULL, '01', '它的尺寸是多少？', '它的尺寸是多少？', 'artifact_dimensions', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"尺寸\"]}', 'answered', 'test2的尺寸与规格为 test。', 'test2的尺寸与规格为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'final-check', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 13:20:46');
INSERT INTO `qa_log` VALUES (35, '5d87a086-b611-48c4-98e7-3cfced8106e2', NULL, NULL, NULL, NULL, '55495', '这件文物收藏在哪里？', '这件文物收藏在哪里？', 'artifact_museum', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"收藏在哪里\"]}', 'answered', '55495 收藏于 The Metropolitan Museum of Art', '55495 收藏于 The Metropolitan Museum of Art', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, '55495', 'request_object_id', '[]', 'final-check', '{}', NULL, NULL, '2026-05-31 13:20:47');
INSERT INTO `qa_log` VALUES (36, '9e4b9368-9d27-42cc-8294-8b4f7e40a9e6', NULL, NULL, NULL, NULL, '55495', '这件文物是什么朝代的？', '这件文物是什么朝代的？', 'artifact_period', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"朝代\"]}', 'answered', '55495 的年代为 清', '55495 的年代为 清', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, '55495', 'request_object_id', '[]', 'final-check', '{}', NULL, NULL, '2026-05-31 13:20:47');
INSERT INTO `qa_log` VALUES (37, '3e72cd8e-85bc-4de2-aa87-75e976e9701c', NULL, NULL, NULL, NULL, NULL, '大英博物馆收藏了多少件中国文物？', '大英博物馆收藏了多少件中国文物？', 'statistics_count', 0.9000, '{\"entities\": {\"museum\": \"大英博物馆\"}, \"confidence\": 0.9, \"matchedKeywords\": [\"收藏\", \"多少\"]}', 'answered', '博物馆 大英博物馆 收藏了 0 件文物', '博物馆 大英博物馆 收藏了 0 件文物', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, NULL, 'not_required_for_intent', '[]', 'final-check', '{}', NULL, NULL, '2026-05-31 13:20:48');
INSERT INTO `qa_log` VALUES (38, 'e4dbc228-f680-41a6-8a0e-3f9d9c30294c', NULL, 'smoke-session', NULL, NULL, 'DEMO_001', '演示文物的材质是什么？', '演示文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', '演示文物 DEMO_001 的材质为 porcelain。', '演示文物 DEMO_001 的材质为 porcelain。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'request_object_id', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-31 13:24:25');
INSERT INTO `qa_log` VALUES (39, 'f30e830c-9313-4a65-b304-c1613f2056ab', NULL, 'smoke-session', NULL, NULL, NULL, '它的尺寸是多少？', '它的尺寸是多少？', 'artifact_dimensions', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"尺寸\"]}', 'answered', '演示文物 DEMO_001 的尺寸为 H. 30 cm x W. 20 cm。', '演示文物 DEMO_001 的尺寸为 H. 30 cm x W. 20 cm。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, 'DEMO_001', 'session_context', '[]', 'smoke-test', '{\"dataset\": \"demo\"}', NULL, NULL, '2026-05-31 13:24:26');
INSERT INTO `qa_log` VALUES (40, '363b3d09-e334-4ca6-aa27-a1153be2a52e', NULL, NULL, NULL, NULL, NULL, '大英博物馆收藏了多少件中国文物？', '大英博物馆收藏了多少件中国文物？', 'statistics_count', 0.9000, '{\"entities\": {\"museum\": \"大英博物馆\"}, \"confidence\": 0.9, \"matchedKeywords\": [\"收藏\", \"多少\"]}', 'answered', '博物馆 大英博物馆 收藏了 0 件文物', '博物馆 大英博物馆 收藏了 0 件文物', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, NULL, 'not_required_for_intent', '[]', 'smoke-test', '{}', NULL, NULL, '2026-05-31 13:24:27');
INSERT INTO `qa_log` VALUES (41, 'c734384b-bc4b-4d04-8681-b3e156b053b9', NULL, 'manual-test-01', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 14:10:14');
INSERT INTO `qa_log` VALUES (42, '71ce75a4-8361-4bcd-a8d0-1499e077a6b8', NULL, 'manual-test-01', NULL, NULL, '01', '这件文物的类型是什么？', '这件文物的类型是什么？', 'artifact_type', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"类型\"]}', 'answered', 'test2的类型为 Bronze。', 'test2的类型为 Bronze。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 14:10:16');
INSERT INTO `qa_log` VALUES (43, 'ff0b02c9-86d6-4c70-bdb2-73668ae933b5', NULL, 'manual-test-01', NULL, NULL, '01', '这件文物的尺寸是多少？', '这件文物的尺寸是多少？', 'artifact_dimensions', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"尺寸\"]}', 'answered', 'test2的尺寸与规格为 test。', 'test2的尺寸与规格为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 14:10:17');
INSERT INTO `qa_log` VALUES (44, 'c08ef793-5255-4ebe-b0f5-533768db310c', NULL, 'manual-test-01', NULL, NULL, '01', '这件文物的介绍是什么？', '这件文物的介绍是什么？', 'artifact_description', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"介绍\"]}', 'answered', 'test', 'test', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 14:10:18');
INSERT INTO `qa_log` VALUES (45, '88b23337-4072-4cfb-8ff8-1cc9a3721248', NULL, 'manual-test-neo4j', NULL, NULL, '55495', '这件文物收藏在哪里？', '这件文物收藏在哪里？', 'artifact_museum', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"收藏在哪里\"]}', 'answered', '55495 收藏于 The Metropolitan Museum of Art', '55495 收藏于 The Metropolitan Museum of Art', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, '55495', 'request_object_id', '[]', 'web', '{}', NULL, NULL, '2026-05-31 14:10:19');
INSERT INTO `qa_log` VALUES (46, 'a7234648-8c68-41e3-ada8-2e9c8b4a82fc', NULL, 'manual-test-neo4j', NULL, NULL, '55495', '这件文物是什么年代？', '这件文物是什么年代？', 'artifact_period', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"年代\"]}', 'answered', '55495 的年代为 清', '55495 的年代为 清', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, '55495', 'request_object_id', '[]', 'web', '{}', NULL, NULL, '2026-05-31 14:10:20');
INSERT INTO `qa_log` VALUES (47, '9a56b578-0bf0-4dfa-aec4-ef5acc50f6a8', NULL, 'manual-test-stat', NULL, NULL, NULL, '大英博物馆收藏了多少件中国文物？', '大英博物馆收藏了多少件中国文物？', 'statistics_count', 0.9000, '{\"entities\": {\"museum\": \"大英博物馆\"}, \"confidence\": 0.9, \"matchedKeywords\": [\"收藏\", \"多少\"]}', 'answered', '博物馆 大英博物馆 收藏了 0 件文物', '博物馆 大英博物馆 收藏了 0 件文物', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, NULL, 'not_required_for_intent', '[]', 'web', '{}', NULL, NULL, '2026-05-31 14:10:20');
INSERT INTO `qa_log` VALUES (48, '2a434d66-4d0b-49c4-9dd8-d3b3a59fad05', NULL, 'qa-web-1780207671526-f8bd35', NULL, NULL, '01', '这件文物的材质是什么', '这件文物的材质是什么', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 14:12:17');
INSERT INTO `qa_log` VALUES (49, 'cc8bc1b6-337b-422c-9963-7344ef29e7cd', NULL, 'qa-web-1780207671526-f8bd35', NULL, NULL, '01', '它的类型是什么', '它的类型是什么', 'artifact_type', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"类型\"]}', 'answered', 'test2的类型为 Bronze。', 'test2的类型为 Bronze。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 14:13:30');
INSERT INTO `qa_log` VALUES (50, '83ee47d5-cf69-4b65-bf0c-0264e3e0f3b0', NULL, 'qa-web-1780207671526-f8bd35', NULL, NULL, '01', '大英博物馆收藏了多少件中国文物？', '大英博物馆收藏了多少件中国文物？', 'statistics_count', 0.9000, '{\"entities\": {\"museum\": \"大英博物馆\"}, \"confidence\": 0.9, \"matchedKeywords\": [\"收藏\", \"多少\"]}', 'answered', '博物馆 大英博物馆 收藏了 0 件文物', '博物馆 大英博物馆 收藏了 0 件文物', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；如后续接入大语言模型，补充性描述会在此字段中单独标注。', NULL, NULL, 'not_required_for_intent', '[]', 'web', '{}', NULL, NULL, '2026-05-31 14:14:33');
INSERT INTO `qa_log` VALUES (51, 'de5785c6-b927-4c1a-b6e4-60f2702b33e5', NULL, 'qa-web-1780207671526-f8bd35', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；当前未启用大语言模型补充生成，已使用模板兜底。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 16:36:59');
INSERT INTO `qa_log` VALUES (52, 'b734bad8-9df1-4685-ac08-b0992d18f819', NULL, 'qa-web-1780207671526-f8bd35', NULL, NULL, '01', '这件文物的材质是什么', '这件文物的材质是什么', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；当前未启用大语言模型补充生成，已使用模板兜底。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 16:56:33');
INSERT INTO `qa_log` VALUES (53, '8a42dbc8-25a8-4ef1-b186-2abd3c8651c0', NULL, 'llm-debug', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；当前未启用大语言模型补充生成，已使用模板兜底。', 2, '01', 'request_object_id', '[]', 'debug', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 16:57:39');
INSERT INTO `qa_log` VALUES (54, '7aa3f0c9-327a-495e-9520-18101e0c7128', NULL, 'qa-web-1780207671526-f8bd35', NULL, NULL, '01', '这件材质是什么', '这件材质是什么', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；当前未启用大语言模型补充生成，已使用模板兜底。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 16:57:42');
INSERT INTO `qa_log` VALUES (55, '91b6a483-f437-4a5b-94b1-4cb5763b8779', NULL, 'llm-debug-after-restart', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '该回答由系统根据 MySQL 或 Neo4j 中的已确认事实生成；当前未启用大语言模型补充生成，已使用模板兜底。', 2, '01', 'request_object_id', '[]', 'debug', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 16:58:19');
INSERT INTO `qa_log` VALUES (56, 'd6ad01bf-ffe8-49e1-83db-9d769bdfdfdc', NULL, 'llm-debug-8001', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '这件文物的材质为test。', 2, '01', 'request_object_id', '[]', 'debug', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 17:01:04');
INSERT INTO `qa_log` VALUES (57, 'd79f95b6-ea62-46a5-9d32-edb0bcb68b45', NULL, 'frontend-proxy-llm', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '这件文物的材质为test。', 2, '01', 'request_object_id', '[]', 'debug', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 17:02:25');
INSERT INTO `qa_log` VALUES (58, 'c23ac0b1-e5f5-43b1-9142-a48b8e5e211c', NULL, 'qa-web-1780218177181-90140c', NULL, NULL, '01', '这件文物的材质是什么？', '这件文物的材质是什么？', 'artifact_material', 0.7800, '{\"entities\": {}, \"confidence\": 0.78, \"matchedKeywords\": [\"材质\"]}', 'answered', 'test2的材质为 test。', 'test2的材质为 test。', '这件文物的材质为test。', 2, '01', 'request_object_id', '[]', 'web', '{\"dataset\": \"mysql\", \"objectId\": \"01\", \"artifactId\": 2}', NULL, NULL, '2026-05-31 17:03:15');

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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答不准确反馈人工审核任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qa_review_task
-- ----------------------------
INSERT INTO `qa_review_task` VALUES (1, 4, 4, 'done', 'fixed', 1, NULL, 1, '已修正。', NULL, '2026-05-25 21:22:42', '2026-05-25 21:56:50', '2026-05-25 21:56:50');
INSERT INTO `qa_review_task` VALUES (2, 6, 6, 'done', 'rejected', 1, NULL, 1, '反馈与数据一致，无需修改。', NULL, '2026-05-25 21:37:25', '2026-05-25 21:37:44', '2026-05-25 21:37:44');
INSERT INTO `qa_review_task` VALUES (3, 7, 7, 'done', 'approved', 1, NULL, 1, '已确认反馈有效。', NULL, '2026-05-25 21:37:26', '2026-05-25 21:37:43', '2026-05-25 21:37:43');
INSERT INTO `qa_review_task` VALUES (4, 9, 9, 'done', 'rejected', 1, NULL, 1, '反馈与数据一致，无需修改。', NULL, '2026-05-25 21:37:44', '2026-05-25 21:56:42', '2026-05-25 21:56:42');
INSERT INTO `qa_review_task` VALUES (5, 10, 10, 'done', 'approved', 1, NULL, 1, '已确认反馈有效。', NULL, '2026-05-25 21:37:44', '2026-05-25 21:56:42', '2026-05-25 21:56:42');
INSERT INTO `qa_review_task` VALUES (6, 12, 12, 'done', 'rejected', 1, NULL, 1, '反馈与数据一致，无需修改。', NULL, '2026-05-25 21:56:43', '2026-05-25 21:59:35', '2026-05-25 21:59:35');
INSERT INTO `qa_review_task` VALUES (7, 13, 13, 'done', 'approved', 1, NULL, 1, '已确认反馈有效。', NULL, '2026-05-25 21:56:43', '2026-05-25 21:59:35', '2026-05-25 21:59:35');
INSERT INTO `qa_review_task` VALUES (8, 15, 15, 'pending', NULL, 1, NULL, NULL, NULL, NULL, '2026-05-25 21:59:36', '2026-05-25 21:59:36', NULL);
INSERT INTO `qa_review_task` VALUES (9, 16, 16, 'done', 'rejected', 1, NULL, 1, '反馈与数据一致，无需修改。', NULL, '2026-05-25 21:59:36', '2026-05-26 10:25:59', '2026-05-26 10:25:59');
INSERT INTO `qa_review_task` VALUES (10, 18, 18, 'done', 'approved', 1, NULL, 1, '已确认反馈有效。', NULL, '2026-05-25 21:59:57', '2026-05-26 10:25:59', '2026-05-26 10:25:59');
INSERT INTO `qa_review_task` VALUES (11, 20, 20, 'done', 'approved', 1, NULL, 1, '已确认反馈有效。', NULL, '2026-05-26 10:01:53', '2026-05-26 10:13:21', '2026-05-26 10:13:21');
INSERT INTO `qa_review_task` VALUES (12, 30, 23, 'pending', NULL, 1, NULL, NULL, NULL, NULL, '2026-05-26 10:26:00', '2026-05-26 10:26:00', NULL);
INSERT INTO `qa_review_task` VALUES (13, 31, 24, 'pending', NULL, 1, NULL, NULL, NULL, NULL, '2026-05-26 10:26:00', '2026-05-26 10:26:00', NULL);
INSERT INTO `qa_review_task` VALUES (14, 32, 25, 'pending', NULL, 1, NULL, NULL, NULL, NULL, '2026-05-30 15:34:49', '2026-05-30 15:34:49', NULL);

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
-- Records of qa_session
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识问答答案来源记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qa_source_record
-- ----------------------------
INSERT INTO `qa_source_record` VALUES (1, 25, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-30 15:34:47');
INSERT INTO `qa_source_record` VALUES (2, 26, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-30 15:35:06');
INSERT INTO `qa_source_record` VALUES (3, 27, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-30 15:38:15');
INSERT INTO `qa_source_record` VALUES (4, 28, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-30 15:38:15');
INSERT INTO `qa_source_record` VALUES (5, 29, 'neo4j', '知识图谱·统计', NULL, NULL, NULL, NULL, NULL, '博物馆 大英博物馆 收藏了 0 件文物', '{\"factText\": \"博物馆 大英博物馆 收藏了 0 件文物\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"知识图谱·统计\", \"sourceType\": \"neo4j\"}', 0.9000, '2026-05-30 15:38:16');
INSERT INTO `qa_source_record` VALUES (6, 30, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-31 13:20:22');
INSERT INTO `qa_source_record` VALUES (7, 31, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-31 13:20:23');
INSERT INTO `qa_source_record` VALUES (8, 32, 'neo4j', '知识图谱·统计', NULL, NULL, NULL, NULL, NULL, '博物馆 大英博物馆 收藏了 0 件文物', '{\"factText\": \"博物馆 大英博物馆 收藏了 0 件文物\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"知识图谱·统计\", \"sourceType\": \"neo4j\"}', 0.9000, '2026-05-31 13:20:24');
INSERT INTO `qa_source_record` VALUES (9, 33, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 13:20:45');
INSERT INTO `qa_source_record` VALUES (10, 34, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的尺寸与规格为 test。', '{\"factText\": \"test2的尺寸与规格为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 13:20:46');
INSERT INTO `qa_source_record` VALUES (11, 35, 'neo4j', '知识图谱·收藏关系', NULL, NULL, NULL, NULL, NULL, '55495 收藏于 The Metropolitan Museum of Art', '{\"factText\": \"55495 收藏于 The Metropolitan Museum of Art\", \"detailUrl\": null, \"confidence\": 0.95, \"sourceName\": \"知识图谱·收藏关系\", \"sourceType\": \"neo4j\"}', 0.9500, '2026-05-31 13:20:47');
INSERT INTO `qa_source_record` VALUES (12, 36, 'neo4j', '知识图谱·年代关系', NULL, NULL, NULL, NULL, NULL, '55495 的年代为 清', '{\"factText\": \"55495 的年代为 清\", \"detailUrl\": null, \"confidence\": 0.95, \"sourceName\": \"知识图谱·年代关系\", \"sourceType\": \"neo4j\"}', 0.9500, '2026-05-31 13:20:48');
INSERT INTO `qa_source_record` VALUES (13, 37, 'neo4j', '知识图谱·统计', NULL, NULL, NULL, NULL, NULL, '博物馆 大英博物馆 收藏了 0 件文物', '{\"factText\": \"博物馆 大英博物馆 收藏了 0 件文物\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"知识图谱·统计\", \"sourceType\": \"neo4j\"}', 0.9000, '2026-05-31 13:20:48');
INSERT INTO `qa_source_record` VALUES (14, 38, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-31 13:24:26');
INSERT INTO `qa_source_record` VALUES (15, 39, 'template', 'QA Demo Dataset', NULL, NULL, NULL, NULL, 'https://www.clevelandart.org/art/collection/search', '演示数据用于验证知识问答子系统主流程。', '{\"factText\": \"演示数据用于验证知识问答子系统主流程。\", \"detailUrl\": \"https://www.clevelandart.org/art/collection/search\", \"confidence\": 0.8, \"sourceName\": \"QA Demo Dataset\", \"sourceType\": \"template\"}', 0.8000, '2026-05-31 13:24:26');
INSERT INTO `qa_source_record` VALUES (16, 40, 'neo4j', '知识图谱·统计', NULL, NULL, NULL, NULL, NULL, '博物馆 大英博物馆 收藏了 0 件文物', '{\"factText\": \"博物馆 大英博物馆 收藏了 0 件文物\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"知识图谱·统计\", \"sourceType\": \"neo4j\"}', 0.9000, '2026-05-31 13:24:27');
INSERT INTO `qa_source_record` VALUES (17, 41, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 14:10:15');
INSERT INTO `qa_source_record` VALUES (18, 42, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的类型为 Bronze。', '{\"factText\": \"test2的类型为 Bronze。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 14:10:16');
INSERT INTO `qa_source_record` VALUES (19, 43, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的尺寸与规格为 test。', '{\"factText\": \"test2的尺寸与规格为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 14:10:17');
INSERT INTO `qa_source_record` VALUES (20, 44, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test', '{\"factText\": \"test\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 14:10:18');
INSERT INTO `qa_source_record` VALUES (21, 45, 'neo4j', '知识图谱·收藏关系', NULL, NULL, NULL, NULL, NULL, '55495 收藏于 The Metropolitan Museum of Art', '{\"factText\": \"55495 收藏于 The Metropolitan Museum of Art\", \"detailUrl\": null, \"confidence\": 0.95, \"sourceName\": \"知识图谱·收藏关系\", \"sourceType\": \"neo4j\"}', 0.9500, '2026-05-31 14:10:19');
INSERT INTO `qa_source_record` VALUES (22, 46, 'neo4j', '知识图谱·年代关系', NULL, NULL, NULL, NULL, NULL, '55495 的年代为 清', '{\"factText\": \"55495 的年代为 清\", \"detailUrl\": null, \"confidence\": 0.95, \"sourceName\": \"知识图谱·年代关系\", \"sourceType\": \"neo4j\"}', 0.9500, '2026-05-31 14:10:20');
INSERT INTO `qa_source_record` VALUES (23, 47, 'neo4j', '知识图谱·统计', NULL, NULL, NULL, NULL, NULL, '博物馆 大英博物馆 收藏了 0 件文物', '{\"factText\": \"博物馆 大英博物馆 收藏了 0 件文物\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"知识图谱·统计\", \"sourceType\": \"neo4j\"}', 0.9000, '2026-05-31 14:10:20');
INSERT INTO `qa_source_record` VALUES (24, 48, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 14:12:17');
INSERT INTO `qa_source_record` VALUES (25, 49, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的类型为 Bronze。', '{\"factText\": \"test2的类型为 Bronze。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 14:13:30');
INSERT INTO `qa_source_record` VALUES (26, 50, 'neo4j', '知识图谱·统计', NULL, NULL, NULL, NULL, NULL, '博物馆 大英博物馆 收藏了 0 件文物', '{\"factText\": \"博物馆 大英博物馆 收藏了 0 件文物\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"知识图谱·统计\", \"sourceType\": \"neo4j\"}', 0.9000, '2026-05-31 14:14:33');
INSERT INTO `qa_source_record` VALUES (27, 51, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 16:36:59');
INSERT INTO `qa_source_record` VALUES (28, 52, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 16:56:34');
INSERT INTO `qa_source_record` VALUES (29, 53, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 16:57:39');
INSERT INTO `qa_source_record` VALUES (30, 54, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 16:57:42');
INSERT INTO `qa_source_record` VALUES (31, 55, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 16:58:19');
INSERT INTO `qa_source_record` VALUES (32, 56, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 17:01:04');
INSERT INTO `qa_source_record` VALUES (33, 56, 'llm', '大语言模型补充生成：gpt-4o-mini', NULL, NULL, NULL, NULL, NULL, '这件文物的材质为test。', '{\"factText\": \"这件文物的材质为test。\", \"detailUrl\": null, \"confidence\": 0.6, \"sourceName\": \"大语言模型补充生成：gpt-4o-mini\", \"sourceType\": \"llm\"}', 0.6000, '2026-05-31 17:01:04');
INSERT INTO `qa_source_record` VALUES (34, 57, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 17:02:25');
INSERT INTO `qa_source_record` VALUES (35, 57, 'llm', '大语言模型补充生成：gpt-4o-mini', NULL, NULL, NULL, NULL, NULL, '这件文物的材质为test。', '{\"factText\": \"这件文物的材质为test。\", \"detailUrl\": null, \"confidence\": 0.6, \"sourceName\": \"大语言模型补充生成：gpt-4o-mini\", \"sourceType\": \"llm\"}', 0.6000, '2026-05-31 17:02:26');
INSERT INTO `qa_source_record` VALUES (36, 58, 'mysql', '公共 MySQL 文物基础表', NULL, NULL, NULL, NULL, NULL, 'test2的材质为 test。', '{\"factText\": \"test2的材质为 test。\", \"detailUrl\": null, \"confidence\": 0.9, \"sourceName\": \"公共 MySQL 文物基础表\", \"sourceType\": \"mysql\"}', 0.9000, '2026-05-31 17:03:15');
INSERT INTO `qa_source_record` VALUES (37, 58, 'llm', '大语言模型补充生成：gpt-4o-mini', NULL, NULL, NULL, NULL, NULL, '这件文物的材质为test。', '{\"factText\": \"这件文物的材质为test。\", \"detailUrl\": null, \"confidence\": 0.6, \"sourceName\": \"大语言模型补充生成：gpt-4o-mini\", \"sourceType\": \"llm\"}', 0.6000, '2026-05-31 17:03:15');

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
-- Records of restore_records
-- ----------------------------
INSERT INTO `restore_records` VALUES (1, 13, 'test', 1, 2, '恢复失败: 备份文件不存在: ./backups\\test.sql', '2026-05-17 17:19:19');
INSERT INTO `restore_records` VALUES (2, 13, 'test', 1, 2, '恢复失败: 备份文件不存在: ./backups\\test.sql', '2026-05-17 17:19:30');
INSERT INTO `restore_records` VALUES (3, 18, 'test1', 1, 1, '恢复成功，共处理 117 行数据。应急备份: ./backups\\test1.sql.before_restore.sql', '2026-05-17 17:39:25');

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
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES (1, 1);
INSERT INTO `role_permissions` VALUES (1, 2);
INSERT INTO `role_permissions` VALUES (1, 3);
INSERT INTO `role_permissions` VALUES (1, 4);
INSERT INTO `role_permissions` VALUES (1, 5);
INSERT INTO `role_permissions` VALUES (1, 6);
INSERT INTO `role_permissions` VALUES (1, 7);
INSERT INTO `role_permissions` VALUES (1, 8);
INSERT INTO `role_permissions` VALUES (1, 9);
INSERT INTO `role_permissions` VALUES (1, 10);
INSERT INTO `role_permissions` VALUES (1, 11);
INSERT INTO `role_permissions` VALUES (1, 20);
INSERT INTO `role_permissions` VALUES (1, 21);
INSERT INTO `role_permissions` VALUES (1, 22);
INSERT INTO `role_permissions` VALUES (1, 23);
INSERT INTO `role_permissions` VALUES (1, 27);
INSERT INTO `role_permissions` VALUES (1, 32);
INSERT INTO `role_permissions` VALUES (1, 38);
INSERT INTO `role_permissions` VALUES (1, 39);
INSERT INTO `role_permissions` VALUES (1, 40);
INSERT INTO `role_permissions` VALUES (1, 43);
INSERT INTO `role_permissions` VALUES (4, 20);
INSERT INTO `role_permissions` VALUES (4, 21);
INSERT INTO `role_permissions` VALUES (4, 38);
INSERT INTO `role_permissions` VALUES (4, 39);
INSERT INTO `role_permissions` VALUES (4, 40);
INSERT INTO `role_permissions` VALUES (5, 9);
INSERT INTO `role_permissions` VALUES (5, 22);
INSERT INTO `role_permissions` VALUES (5, 23);
INSERT INTO `role_permissions` VALUES (5, 43);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'super_admin', '超级管理员', '拥有所有权限', '2026-04-28 18:38:59', '2026-04-29 17:23:56');
INSERT INTO `roles` VALUES (4, 'content_auditor', '内容审核员', '仅拥有内容审核相关权限', '2026-04-29 09:45:34', '2026-05-06 11:24:28');
INSERT INTO `roles` VALUES (5, 'data_manager', '数据管理员', '拥有文物数据的增删改查权限', '2026-04-29 09:45:37', '2026-05-06 11:24:51');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '安全日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of security_logs
-- ----------------------------

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
-- Records of sensitive_words
-- ----------------------------
INSERT INTO `sensitive_words` VALUES (1, '暴力', 'violence', 1, 'high', '2026-04-28 18:39:00', '2026-05-16 20:25:23');
INSERT INTO `sensitive_words` VALUES (2, '色情', 'pornographic', 1, 'high', '2026-04-28 18:39:00', '2026-05-16 20:25:36');
INSERT INTO `sensitive_words` VALUES (3, '赌博', 'other', 1, 'medium', '2026-04-28 18:39:00', '2026-05-16 20:25:48');
INSERT INTO `sensitive_words` VALUES (4, '诈骗', 'other', 1, 'high', '2026-04-28 18:39:00', '2026-05-16 20:25:56');
INSERT INTO `sensitive_words` VALUES (5, 'test1', 'other', 1, NULL, '2026-05-16 20:27:07', NULL);
INSERT INTO `sensitive_words` VALUES (6, 'test2', 'other', 1, NULL, '2026-05-16 20:27:07', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统告警记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_alerts
-- ----------------------------
INSERT INTO `system_alerts` VALUES (1, 'service_error', 'info', '系统监控已启用', '系统监控看板V7已部署，告警系统正常运行中', 'monitoring', 'resolved', NULL, NULL, NULL, '2026-05-22 16:12:42');
INSERT INTO `system_alerts` VALUES (2, 'disk_full', 'warning', '磁盘空间不足', '磁盘 F:\\ 使用率已达 93.6%，剩余空间 12.77 GB', 'system', 'active', NULL, NULL, NULL, '2026-05-23 16:50:49');
INSERT INTO `system_alerts` VALUES (3, 'db_failure', 'critical', '数据库连接失败', '数据库连接异常: HikariPool-1 - Connection is not available, request timed out after 30006ms.', 'database', 'active', NULL, NULL, NULL, '2026-05-28 21:14:22');

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
-- Records of system_configs
-- ----------------------------
INSERT INTO `system_configs` VALUES (1, 'feat_user_registration', '开放用户注册', 'boolean', 'feature_toggle', 'true', '控制前台用户注册功能的开启/关闭', 1, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (2, 'feat_user_comment', '用户评论功能', 'boolean', 'feature_toggle', 'true', '控制用户发表评论功能的开启/关闭', 2, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (3, 'feat_user_upload', '用户上传功能', 'boolean', 'feature_toggle', 'true', '控制在Web/App端使用上传功能', 3, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (4, 'feat_user_post', '用户动态发布', 'boolean', 'feature_toggle', 'true', '控制用户发布动态功能的开启/关闭', 4, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (5, 'feat_user_browse', '文物浏览功能', 'boolean', 'feature_toggle', 'true', '控制前台文物浏览功能的开启/关闭', 5, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (6, 'feat_auto_audit', '自动审核功能', 'boolean', 'feature_toggle', 'true', '控制自动审核功能的开启/关闭', 6, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (7, 'feat_notify_push', '消息推送功能', 'boolean', 'feature_toggle', 'true', '控制系统通知推送功能的开启/关闭', 7, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (8, 'feat_search_recommend', '搜索推荐功能', 'boolean', 'feature_toggle', 'true', '控制搜索与推荐功能的开启/关闭', 8, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (9, 'feat_maintenance_mode', '维护模式', 'boolean', 'feature_toggle', 'false', '开启后前台显示维护页面，管理员仍可正常访问后台', 9, 1, 1, '2026-05-22 13:32:47', NULL, NULL);
INSERT INTO `system_configs` VALUES (10, 'site_name', '站点名称', 'string', 'general', 'RelicAdmin文物管理系统', '系统站点显示名称', 1, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (11, 'site_description', '站点描述', 'string', 'general', '综合性文物管理和数据采集平台', '系统站点描述', 2, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (12, 'page_size', '默认分页大小', 'number', 'general', '10', '列表查询默认分页条数', 3, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (13, 'max_upload_size', '最大上传大小(MB)', 'number', 'file', '10', '文件上传大小限制', 10, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (14, 'allowed_file_types', '允许上传的文件类型', 'json', 'file', '[\"jpg\",\"jpeg\",\"png\",\"gif\",\"webp\"]', '允许上传的图片格式', 11, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (15, 'log_level', '日志级别', 'string', 'general', 'INFO', '系统日志输出级别: DEBUG, INFO, WARN, ERROR', 4, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (16, 'enable_registration', '开放注册', 'boolean', 'security', 'true', '是否允许新用户注册', 20, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (17, 'enable_notification', '启用通知', 'boolean', 'notification', 'true', '是否启用系统通知功能', 30, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (18, 'crawler_global_timeout', '爬取全局超时(秒)', 'number', 'crawler', '300', '爬取任务全局默认超时', 40, 1, 1, '2026-05-22 13:33:06', NULL, NULL);
INSERT INTO `system_configs` VALUES (19, 'crawler_user_agent', '爬取User-Agent', 'string', 'crawler', 'Mozilla/5.0 (compatible; RelicCrawler/1.0)', '爬虫请求头User-Agent', 41, 1, 1, '2026-05-22 13:33:06', NULL, NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统运行日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文物标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tags
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_behaviors
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户浏览历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_browse_history
-- ----------------------------
INSERT INTO `user_browse_history` VALUES (1, 9, 1, '2026-05-22 16:07:42');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户评论' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_comments
-- ----------------------------
INSERT INTO `user_comments` VALUES (1, 9, 1, NULL, '很好', NULL, NULL, NULL, NULL, NULL, NULL);

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
-- Records of user_favorites
-- ----------------------------
INSERT INTO `user_favorites` VALUES (1, 1, 'test', NULL);
INSERT INTO `user_favorites` VALUES (9, 4, 'test', '2026-05-08 00:00:00');

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
-- Records of user_follows
-- ----------------------------
INSERT INTO `user_follows` VALUES (9, 10, NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户动态' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_posts
-- ----------------------------

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
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (9, 4, 1, '2026-05-16 15:29:00');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户上传内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_uploads
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '统一用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (9, 'test', '$2a$10$tUpTvCjHS3NOjgq3JXgFD.WSkQPizrzTQrusixR9ulTx.gvJpyP8q', NULL, NULL, NULL, 'test', 'active', 'test', 'knowledge', '2026-05-15 22:06:34', NULL, NULL, 'admin', '2026-05-15 22:06:34', '2026-05-15 22:06:52', 0, 0, NULL);
INSERT INTO `users` VALUES (10, 'test2', '$2a$10$mtiSdl2TTWHvKt8a/CO4x.vZd.Ck50gbm.HYz4g1xqNzyBnRm1UES', NULL, NULL, NULL, 'test2', 'active', NULL, 'knowledge', '2026-05-16 16:16:59', NULL, NULL, 'admin', '2026-05-16 16:16:59', '2026-05-16 16:16:59', 0, 0, NULL);
INSERT INTO `users` VALUES (11, 'test1', '$2a$10$ECVnFMZeU5w4tZUL11LPQOO/b9Pbo1GI3/xYURMmG0KaKYdb9Jcn6', NULL, NULL, NULL, 'test1', 'active', NULL, 'knowledge', '2026-05-22 11:58:49', NULL, NULL, 'web', '2026-05-22 11:58:49', '2026-05-22 11:58:49', 0, 0, NULL);
INSERT INTO `users` VALUES (12, 'test3', '$2a$10$QcvSFzLx3gojinzOT4B4Bu7CM1On/xiVmSCZzgtPe.j6HCOgjVEo6', NULL, NULL, NULL, 'test3', 'active', NULL, 'knowledge', '2026-05-22 15:05:35', NULL, NULL, 'web', '2026-05-22 15:05:35', '2026-05-22 15:05:35', 0, 0, NULL);
INSERT INTO `users` VALUES (13, 'test4', '$2a$10$27po1w.mHEvmFYA5M4Pmz.MpQhx2P8DX97omh8IZ6gPS4SYex67tK', NULL, NULL, NULL, 'test4', 'active', NULL, 'knowledge', '2026-05-22 17:49:13', NULL, NULL, 'web', '2026-05-22 17:49:13', '2026-05-22 17:49:13', 0, 0, NULL);

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

-- ----------------------------
-- Records of violation_types
-- ----------------------------
INSERT INTO `violation_types` VALUES (1, 'spam_comment', '垃圾评论', 3, 'warning', '发布无关内容、广告等', 1, '2026-05-06 16:19:08', '2026-05-16 20:46:51');
INSERT INTO `violation_types` VALUES (2, 'inappropriate_content', '不当内容', 2, 'temp_ban', '发布色情、暴力、侮辱性内容', 1, '2026-05-06 16:19:08', '2026-05-16 20:47:23');
INSERT INTO `violation_types` VALUES (3, 'fake_identity', '虚假身份', 2, 'temp_ban', '使用虚假身份或冒用他人身份', 1, '2026-05-06 16:19:08', NULL);
INSERT INTO `violation_types` VALUES (4, 'copyright_violation', '版权侵权', 3, 'permanent_ban', '上传未经授权的内容', 1, '2026-05-06 16:19:08', NULL);
INSERT INTO `violation_types` VALUES (5, 'malicious_attack', '恶意攻击', 4, 'permanent_ban', '恶意攻击系统或其他用户', 1, '2026-05-06 16:19:08', NULL);
INSERT INTO `violation_types` VALUES (6, 'frequent_operation', '频繁操作', 1, 'warning', '短时间内频繁操作，疑似机器行为', 1, '2026-05-06 16:19:08', '2026-05-16 20:47:58');
INSERT INTO `violation_types` VALUES (7, 'illegal_upload', '违规上传', 2, 'temp_ban', '上传不符合规范的文件', 1, '2026-05-06 16:19:08', NULL);

SET FOREIGN_KEY_CHECKS = 1;
