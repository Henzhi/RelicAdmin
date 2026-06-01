# RelicAdmin — 文物管理系统

> 基于 Spring Boot + Vue 3 的全栈文物数字化管理平台，面向博物馆及文化机构提供文物资产管理、用户内容安全管理、多角色权限控制等一站式解决方案。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 目录

- [项目概述](#项目概述)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [数据库设计](#数据库设计)
- [API 文档](#api-文档)
- [使用指南](#使用指南)
- [部署说明](#部署说明)
- [贡献指南](#贡献指南)

---

## 项目概述

RelicAdmin 是一个面向文化机构和博物馆的全栈文物数字化管理平台。系统支持多角色（超级管理员、内容审核员、数据管理员、知识服务用户、掌上博物馆用户）协同工作，涵盖文物信息管理、用户生成内容（UGC）审核与安全管控、数据备份恢复、网络爬取任务调度等核心业务场景。

**设计目标**：替代传统 Excel / 纸质管理模式，实现文物资产的集中化、标准化、安全化管理。

**适用场景**：
- 博物馆日常文物信息录入与维护
- 多管理员协同审核用户上传的文物图片、评论、动态
- 敏感词自动过滤与违规内容闭环处置
- 文物相关数据（朝代、艺术家、地点）的统一标准化管理
- 定时数据备份与一键恢复

---

## 核心功能

### 🏛️ 文物资产管理
- 文物 CRUD（名称、描述、年代、材质、尺寸等元数据）
- 多图片上传与管理（阿里云 OSS 存储）
- 关联博物馆、朝代、艺术家、地点多维度分类
- 文物类型自定义配置
- 用户收藏、点赞互动

### 👥 多角色权限管理（RBAC）
- 管理员用户 ↔ 角色 ↔ 权限 三级权限模型
- 默认角色：超级管理员、内容审核员、数据管理员、知识服务用户、掌上博物馆用户
- JWT 多端统一鉴权（admin / museum / knowledge / user）
- 细粒度权限控制到接口级别

### 🛡️ 内容安全审核
- 敏感词库动态配置与管理
- 审核策略按内容类型（post / comment / upload）独立配置
- 支持自动审核模式：auto_pass（自动通过）/ auto_review（人工审核）/ auto_reject（自动驳回）
- 违规处罚闭环：检测违规 → 自动/人工处罚 → 用户申诉 → 管理员仲裁
- 违规类型自定义（禁言、封号、内容下架等）

### 📊 用户行为追踪
- 收藏、点赞、评论、关注、动态发布、文件上传全链路记录
- 浏览历史追踪
- 行为日志实时查询

### 💾 数据备份恢复
- 全量/增量/导出三种备份模式
- 定时自动备份（Cron 表达式配置）
- 备份加密存储
- 一键恢复，恢复日志可追溯

### 🌐 网络爬取任务
- 数据源配置（网页 / API / RSS）
- 定时爬取（Cron 表达式）
- 爬取规则 JSON 配置
- 失败重试机制与执行日志

### ⚙️ 系统管理
- 系统公告发布
- 系统配置参数动态调整
- 操作日志自动记录（AOP + 自定义注解）
- 仪表盘数据概览
- 管理员个人信息管理（头像上传、密码修改）

---

## 技术栈

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.12 | 应用框架 |
| MyBatis | 3.0.4 | ORM 持久层 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.x (Upstash) | 缓存与分布式锁 |
| Druid | 1.2.23 | 数据库连接池 |
| Spring Security Crypto | — | BCrypt 密码加密 |
| JJWT | 0.11.5 | JWT 令牌签发与验证 |
| PageHelper | 2.1.0 | MyBatis 分页插件 |
| Knife4j | 4.5.0 | API 文档自动生成（Swagger） |
| Aliyun OSS SDK | 3.18.3 | 对象存储（头像/图片） |
| Apache POI | 5.2.5 | Excel 导入导出 |
| AspectJ | — | AOP 操作日志 |
| WebSocket | — | 实时消息推送 |

### 前端
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4 | 渐进式前端框架 |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.14 | UI 组件库 |
| Pinia | 3.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.x | HTTP 请求库 |
| ECharts | 6.x | 数据可视化图表 |

### 开发工具
| 工具 | 用途 |
|------|------|
| Maven 3.x | Java 项目构建与依赖管理 |
| npm / pnpm | 前端依赖管理 |
| IntelliJ IDEA / VS Code | 推荐开发 IDE |

---

## 环境要求

| 环境 | 最低版本 | 推荐版本 |
|------|----------|----------|
| JDK | 17 | **21** |
| Maven | 3.6+ | 3.9+ |
| Node.js | 16+ | 18 LTS / 20 LTS |
| MySQL | 8.0+ | 8.0 |
| Redis | 6.x | 7.x |

### 必需的外部服务
- **阿里云 OSS** — 用于头像和图片文件存储
- **MySQL 数据库** — 持久化存储（需提前创建数据库）
- **Redis** — 缓存服务

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Henzhi/RelicAdmin.git
cd RelicAdmin
```

### 2. 数据库初始化

#### 2.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS seitem DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2.2 执行迁移脚本

执行根目录下的 `seitem.sql` 脚本：



> **注意**：V1 脚本会创建默认超级管理员账号：
> - 用户名：`admin`
> - 密码：`admin123`

### 3. 配置后端

编辑 `relic-server/src/main/resources/application-dev.yml`，根据你的环境修改以下配置：

```yaml
relic:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost          # 数据库地址
    port: 3306               # 数据库端口
    database: seitem    # 数据库名
    username: root           # 数据库用户名
    password: your_password  # 数据库密码

  alioss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: your_aliyun_access_key_id
    access-key-secret: your_aliyun_access_key_secret
    bucket-name: your_bucket_name

  redis:
    host: localhost          # Redis 地址
    port: 6379               # Redis 端口
    password:                # Redis 密码（无密码则留空）
    database: 0
```

### 4. 启动后端

```bash
# 在项目根目录执行
mvn clean compile -pl relic-server -am

# 启动应用（开发模式）
mvn spring-boot:run -pl relic-server
```

启动成功后访问：
- API 文档（Knife4j）：`http://localhost:8080/doc.html`

### 5. 启动前端

```bash
cd relic-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

启动成功后访问：`http://localhost:5173`

> 前端开发服务器已配置代理，`/admin` 和 `/user` 开头的请求会自动转发到 `http://localhost:8080`。

### 6. 登录系统

1. 浏览器打开 `http://localhost:5173`
2. 使用默认管理员账号登录：
   - 用户名：`admin`
   - 密码：`admin123`
3. 建议首次登录后通过「个人中心」修改密码

---

## 项目结构

```
RelicAdmin/
├── relic-common/                    # 公共模块（工具类、常量、异常、JWT）
│   └── src/main/java/com/relic/
│       ├── constant/                # 常量类（JWT配置、消息、状态等）
│       ├── context/                 # 线程上下文（BaseContext）
│       ├── enumeration/             # 枚举类
│       ├── exception/               # 自定义异常类
│       ├── json/                    # JSON 序列化配置
│       ├── properties/              # 配置属性类（OSS、JWT）
│       ├── result/                  # 统一响应体（Result、PageResult）
│       └── utils/                   # 工具类（AliOssUtil、JwtUtil、HttpClient）
│
├── relic-pojo/                      # POJO 模块（实体、DTO、VO）
│   └── src/main/java/com/relic/
│       ├── dto/                     # 数据传输对象（40+ 个）
│       ├── entity/                  # 数据库实体类（60+ 个）
│       └── vo/                      # 视图对象（20+ 个）
│
├── relic-server/                    # 主服务模块（控制器、业务逻辑、数据访问）
│   └── src/main/
│       ├── java/com/relic/
│       │   ├── RelicApplication.java       # Spring Boot 启动类
│       │   ├── annotation/                 # 自定义注解（@AutoFill、@OperationLog）
│       │   ├── aspect/                     # AOP 切面（自动填充、操作日志）
│       │   ├── config/                     # 配置类（OSS、Redis、Security、WebMvc、WebSocket）
│       │   ├── controller/                 # 控制器层
│       │   │   ├── admin/                  # 管理端接口（24 个 Controller）
│       │   │   ├── knowledge/              # 知识服务端接口
│       │   │   ├── museum/                 # 掌上博物馆端接口
│       │   │   └── user/                   # 用户端接口
│       │   ├── converter/                  # VO 转换器
│       │   ├── handler/                    # 全局异常处理器
│       │   ├── interceptor/                # JWT 拦截器（4 端各自独立）
│       │   ├── mapper/                     # MyBatis Mapper 接口（50+ 个）
│       │   └── service/                    # 业务逻辑层
│       │       └── impl/                   # 服务实现类（40+ 个）
│       └── resources/
│           ├── application.yml             # 主配置文件
│           ├── application-dev.yml         # 开发环境配置
│           ├── mapper/                     # MyBatis XML 映射文件（50+ 个）
│           └── db/
│               ├── migration/              # 数据库迁移脚本（V1 - V7）
│               └── location-test-data.sql  # 测试数据
│
├── relic-frontend/                  # 前端项目（Vue 3 + Vite）
│   └── src/
│       ├── api/                     # API 请求模块（30+ 个）
│       ├── layout/                  # 布局组件（MainLayout）
│       ├── router/                  # 路由配置
│       ├── stores/                  # Pinia 状态管理
│       └── views/                   # 页面组件（40+ 个）
│
├── API-Documentation.md             # 详细 API 接口文档
├── pom.xml                          # Maven 父 POM（依赖版本管理）
└── README.md                        # 本文件
└── seitem.sql                       数据库迁移脚本
```

---

## 数据库设计

### 核心数据表

| 表名 | 说明 | 所属模块 |
|------|------|----------|
| `admin_users` | 管理员用户表（独立于前台用户） | 权限管理 |
| `admin_user_role` | 管理员-角色关联表 | 权限管理 |
| `roles` | 角色表 | 权限管理 |
| `role_permission` | 角色-权限关联表 | 权限管理 |
| `permissions` | 权限表 | 权限管理 |
| `users` | 前台用户表 | 用户管理 |
| `artifacts` | 文物主表 | 文物管理 |
| `artifact_images` | 文物图片表 | 文物管理 |
| `artifact_types` | 文物类型表 | 文物管理 |
| `artists` | 艺术家表 | 文物管理 |
| `dynasties` | 朝代表 | 文物管理 |
| `museums` | 博物馆表 | 文物管理 |
| `locations` | 地点表 | 文物管理 |
| `audit_strategies` | 审核策略配置表 | 内容安全 |
| `audit_records` | 审核记录表 | 内容安全 |
| `sensitive_words` | 敏感词库表 | 内容安全 |
| `penalty_records` | 违规处罚记录表 | 内容安全 |
| `violation_types` | 违规类型表 | 内容安全 |
| `appeal_records` | 申诉记录表 | 内容安全 |
| `user_behaviors` | 用户行为日志表 | 行为追踪 |
| `backup_strategies` | 备份策略表 | 备份管理 |
| `backup_records` | 备份记录表 | 备份管理 |
| `restore_records` | 恢复记录表 | 备份管理 |
| `crawl_tasks` | 爬取任务表 | 爬取管理 |
| `system_configs` | 系统配置表 | 系统管理 |

### ER 关系概览

```
[admin_users] ──多对多── [roles] ──多对多── [permissions]
[artifacts] ──多对一── [museums]
[artifacts] ──多对一── [dynasties]
[artifacts] ──多对一── [artists]
[artifacts] ──一对多── [artifact_images]
[users] ──一对多── [audit_records]
[audit_records] ──一对一── [penalty_records]
[penalty_records] ──一对一── [appeal_records]
```

---

## API 文档

### 在线文档

启动后端后，访问 Knife4j 在线 API 文档：

```
http://localhost:8080/doc.html
```

Swagger JSON 接口定义：

```
http://localhost:8080/v3/api-docs
```

### 统一响应格式

所有 API 统一返回以下 JSON 结构：

```json
{
  "code": 200,
  "msg": "success",
  "data": { }
}
```

分页查询响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 100,
    "records": [],
    "page": 1,
    "pageSize": 10
  }
}
```

### 认证方式

所有管理端接口需在请求头中携带 JWT Token：

```
token: <admin_token>
```

Token 通过登录接口获取：

```
POST /admin/employee/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### 主要接口分类

| 前缀 | 说明 | Controller 数量 |
|------|------|-----------------|
| `/admin/admin-user/*` | 管理员账号管理 | 1 |
| `/admin/artifact*` | 文物管理 | 6 |
| `/admin/audit*` | 审核管理 | 3 |
| `/admin/penalty*` | 违规处罚 | 1 |
| `/admin/appeal*` | 申诉管理 | 1 |
| `/admin/backup*` | 备份管理 | 2 |
| `/admin/crawl*` | 爬取任务 | 1 |
| `/admin/log*` | 日志管理 | 1 |
| `/admin/statistics*` | 数据统计 | 1 |
| `/user/*` | 用户端接口 | 6 |

> 完整接口文档请参考项目根目录下的 [API-Documentation.md](API-Documentation.md)。

---

## 使用指南

### 角色与权限

| 角色 | 编码 | 典型权限 |
|------|------|----------|
| 超级管理员 | `ROLE_SUPER_ADMIN` | 全部功能 |
| 内容审核员 | `ROLE_CONTENT_AUDITOR` | 审核管理、违规处罚、申诉处理 |
| 数据管理员 | `ROLE_DATA_ADMIN` | 文物 CRUD、博物馆/艺术家/朝代管理等 |
| 知识服务用户 | `ROLE_KNOWLEDGE_USER` | 知识图谱子系统 |
| 掌上博物馆用户 | `ROLE_MUSEUM_USER` | 掌上博物馆子系统 |

### 创建新管理员

1. 超级管理员登录 → 侧边栏「管理员管理」
2. 点击「新增」→ 填写用户名、密码、真实姓名、邮箱、手机号
3. 选择角色（可多选）
4. 提交后新管理员即可登录

### 添加文物

1. 进入「内容管理」→「文物管理」
2. 点击「新增文物」
3. 填写文物名称、描述、选择关联博物馆、朝代、艺术家、地点
4. 上传文物图片（可多张，存储至阿里云 OSS）
5. 提交，文物信息入库

### 配置审核策略

1. 进入「内容安全」→「审核策略」
2. 选择内容类型（动态 / 评论 / 上传）
3. 设置审核模式：
   - **自动通过**：无敏感词直接发布
   - **人工审核**：所有内容需人工审核
   - **自动驳回**：有敏感词则自动驳回
4. 设置风险阈值（1-5，超过阈值的内容转人工复审）
5. 保存策略

### 内容安全闭环操作流程

```
用户发布内容
    ↓
敏感词检测 + 审核策略匹配
    ↓
┌──自动通过──→ 内容上架
│
├──人工审核──→ 审核员审核 ──→ 通过上架 / 驳回
│
└──自动驳回──→ 记录违规 ──→ 自动处罚
                              ↓
                        用户可申诉
                              ↓
                        管理员仲裁（维持/撤销）
```

### 数据备份

1. 进入「系统管理」→「备份管理」
2. 点击「立即备份」，选择备份类型（全量/增量/导出）
3. 备份完成后可下载备份文件
4. 在「备份策略」中配置自动备份（Cron 表达式 + 保留天数）

### 个人中心

1. 点击右上角用户名下拉 →「个人中心」
2. 可编辑：真实姓名、邮箱、手机号
3. 可上传头像（JPG/PNG/GIF/WebP，最大 10MB）
4. 可修改登录密码（需验证原密码）

---

## 部署说明

### 后端打包

```bash
# 在项目根目录执行
mvn clean package -pl relic-server -am -DskipTests

# 生成的 JAR 包位于
# relic-server/target/relic-server-1.0-SNAPSHOT.jar

# 运行
java -jar relic-server/target/relic-server-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 前端打包

```bash
cd relic-frontend
npm run build

# 生成的静态文件位于 relic-frontend/dist/
# 可部署至 Nginx、Apache 或 CDN
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name relic-admin.example.com;

    # 前端静态资源
    location / {
        root /var/www/relic-admin/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /admin/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /user/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 生产环境 checklist

- [ ] 修改 `application-prod.yml` 中的数据库地址、用户名、密码
- [ ] 修改 JWT 签名密钥（`relic.jwt.*-secret-key`）为安全随机字符串
- [ ] 配置阿里云 OSS 密钥
- [ ] 配置 Redis 连接信息并设置密码
- [ ] 关闭 Knife4j 文档（生产环境不建议暴露 API 文档）
- [ ] 配置 HTTPS 证书
- [ ] 修改默认管理员密码

---

## 贡献指南

### 分支策略

- `main` — 稳定发布分支
- `xxx` — 功能完善分支

### 提交规范

提交信息请遵循 Conventional Commits 规范：

```
feat: 新增文物批量导入功能
fix: 修复备份策略 Cron 表达式校验异常
docs: 更新 API 文档中的分页格式说明
refactor: 重构审核策略匹配逻辑
style: 修复仪表盘图表样式
test: 补充 PenaltyService 单元测试
```

### 代码规范

- **Java**：遵循阿里巴巴 Java 开发手册
- **Vue**：遵循 Vue 3 官方风格指南 + Composition API
- **SQL**：表名/字段名使用蛇形命名（snake_case），索引命名前缀 `idx_`
- **API**：RESTful 风格，统一使用 `Result<T>` 响应体

### Pull Request 流程

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feature/your-feature`
3. 提交代码并推送
4. 创建 Pull Request 到 `develop` 分支
5. 确保 CI 通过（如配置了 CI）
6. 等待 Code Review

---


## 附录

### 常用命令速查

```bash
# --- 后端 ---
# 编译后端
mvn clean compile -pl relic-server -am

# 启动后端（开发模式）
mvn spring-boot:run -pl relic-server

# 打包后端
mvn clean package -pl relic-server -am -DskipTests

# 运行测试
mvn test -pl relic-server

# --- 前端 ---
# 安装依赖
cd relic-frontend && npm install

# 启动开发服务器
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview
```

### 默认账号信息

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | `admin` | `admin123` | 拥有系统全部权限 |

> ⚠️ **安全警告**：首次部署后请立即修改默认管理员密码。

### 技术架构图

```
┌──────────────────────────────────────────────────────┐
│                    Browser (浏览器)                    │
│          http://localhost:5173 (Vite Dev)             │
└──────────────────────┬───────────────────────────────┘
                       │ HTTP / WebSocket
          ┌────────────┴────────────┐
          │    Nginx / Vite Proxy   │
          └────────────┬────────────┘
                       │
┌──────────────────────┴───────────────────────────────┐
│          Spring Boot 3.2.12 (port 8080)               │
│                                                       │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │ Interceptor │  │  Controller  │  │    AOP      │  │
│  │  (JWT Auth) │──│  (REST API)  │──│ (@OpLog)    │  │
│  └─────────────┘  └──────┬───────┘  └─────────────┘  │
│                          │                            │
│  ┌───────────────────────┴──────────────────────────┐ │
│  │              Service Layer (业务逻辑)              │ │
│  └───────────────────────┬──────────────────────────┘ │
│                          │                            │
│  ┌───────────────────────┴──────────────────────────┐ │
│  │              Mapper Layer (数据访问)               │ │
│  └───────────┬───────────────────────┬──────────────┘ │
└──────────────┼───────────────────────┼────────────────┘
               │                       │
      ┌────────▼────────┐    ┌─────────▼─────────┐
      │   MySQL 8.0     │    │   Redis (Upstash)  │
      │  (持久化存储)    │    │  (缓存)            │
      └─────────────────┘    └───────────────────┘
               │
      ┌────────▼────────┐
      │  Aliyun OSS     │
      │  (图片/文件存储) │
      └─────────────────┘
```