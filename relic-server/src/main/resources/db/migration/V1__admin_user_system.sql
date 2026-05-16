-- ============================================================
-- 独立管理员账户体系 - 数据库迁移脚本（简化版）
-- 管理员与普通用户表完全分离
-- ============================================================

-- Step 1: 为现有 users 表添加 user_type 字段（知识服务用户 / 掌上博物馆用户）
-- user_type 字段允许为空，不设置默认值
ALTER TABLE users ADD COLUMN user_type VARCHAR(20) AFTER ban_reason;

-- 创建索引
CREATE INDEX idx_users_user_type ON users(user_type);

-- Step 2: 创建独立的 admin_users 表（仅管理员账户使用，与 users 表完全隔离）
CREATE TABLE IF NOT EXISTS admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar_url VARCHAR(500) COMMENT '头像地址',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-启用, banned-禁用',
    last_login DATETIME COMMENT '最后登录时间',
    last_ip VARCHAR(50) COMMENT '最后登录IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表（独立于前台用户表）';

-- Step 3: 创建 admin_user_role 关联表（管理员与角色的多对多关联）
CREATE TABLE IF NOT EXISTS admin_user_role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id INT NOT NULL COMMENT '管理员ID',
    role_id INT UNSIGNED NOT NULL COMMENT '角色ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    FOREIGN KEY (admin_user_id) REFERENCES admin_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY uk_admin_role (admin_user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

-- Step 4: 插入角色种子数据
INSERT IGNORE INTO roles (name, display_name, description, created_at, updated_at) VALUES ('ROLE_SUPER_ADMIN', '超级管理员', '拥有系统全部权限', NOW(), NOW());
INSERT IGNORE INTO roles (name, display_name, description, created_at, updated_at) VALUES ('ROLE_CONTENT_AUDITOR', '内容审核员', '拥有内容审核相关权限', NOW(), NOW());
INSERT IGNORE INTO roles (name, display_name, description, created_at, updated_at) VALUES ('ROLE_DATA_ADMIN', '数据管理员', '拥有文物数据的增删改查权限', NOW(), NOW());
INSERT IGNORE INTO roles (name, display_name, description, created_at, updated_at) VALUES ('ROLE_KNOWLEDGE_USER', '知识服务用户', '知识服务子系统基础用户', NOW(), NOW());
INSERT IGNORE INTO roles (name, display_name, description, created_at, updated_at) VALUES ('ROLE_MUSEUM_USER', '掌上博物馆用户', '掌上博物馆子系统基础用户', NOW(), NOW());

-- Step 5: 插入默认超级管理员账号（用户名: admin, 密码: admin123）
INSERT IGNORE INTO admin_users (username, password_hash, real_name, status, created_at, updated_at) VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '系统管理员', 'active', NOW(), NOW());