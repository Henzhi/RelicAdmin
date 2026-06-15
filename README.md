# RelicAdmin - 文物管理系统后台

RelicAdmin 是一个面向博物馆和文物管理机构的全栈后台管理系统，提供文物信息管理、用户行为监控、内容审核、知识问答对接等核心功能，支持多端（管理端/博物馆端/知识问答端/用户端）JWT 鉴权与 RBAC 权限控制。

## 目录

- [项目架构](#项目架构)
- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [项目结构](#项目结构)
- [贡献指南](#贡献指南)
- [许可证](#许可证)
- [联系方式](#联系方式)

## 项目架构

```
┌─────────────────────────────────────────────────────────┐
│                    RelicAdmin 前端                        │
│         Vue 3 + Element Plus + Vite 5                    │
├─────────────────────────────────────────────────────────┤
│                    RelicAdmin 后端                        │
│         Spring Boot 3.2 + MyBatis + Redis                │
├──────────┬──────────┬──────────┬────────────────────────┤
│  管理端   │ 博物馆端  │ 问答端   │      用户端            │
│ /admin/* │/museum/* │/knowledge│     /user/*            │
├──────────┴──────────┴──────────┴────────────────────────┤
│              MySQL  │  Redis  │  阿里云 OSS              │
└─────────────────────────────────────────────────────────┘
```

系统采用多模块 Maven 项目结构，前后端分离部署：

- **后端**：Spring Boot 3.2 提供 RESTful API，通过 JWT 实现四端独立鉴权
- **前端**：Vue 3 SPA 应用，Vite 开发服务器通过代理转发 API 请求
- **数据库**：MySQL 存储业务数据，Redis 缓存热点数据（敏感词等）
- **对象存储**：阿里云 OSS 存储文物图片等文件资源

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 运行环境 |
| Spring Boot | 3.2.12 | 应用框架 |
| MyBatis | 3.0.4 | ORM 框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis (Lettuce) | - | 缓存中间件 |
| JWT (jjwt) | 0.11.5 | 多端令牌鉴权 |
| PageHelper | 2.1.0 | 分页插件 |
| Druid | 1.2.23 | 数据库连接池 |
| Knife4j | 4.5.0 | API 文档 |
| Apache POI | 5.2.5 | Excel 导入导出 |
| OpenCSV | 5.9 | CSV 解析 |
| 阿里云 OSS SDK | 3.18.3 | 文件存储 |
| Lombok | 1.18.36 | 代码简化 |
| Fastjson2 | 2.0.61 | JSON 处理 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4 | 前端框架 |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.14 | UI 组件库 |
| Pinia | 3.0 | 状态管理 |
| Vue Router | 4.6 | 路由管理 |
| Axios | 1.16 | HTTP 客户端 |
| ECharts | 6.1 | 数据可视化 |

## 功能模块

### 核心管理

| 模块 | 说明 |
|------|------|
| 仪表盘 | 系统概览与数据统计 |
| 用户管理 | 前台用户账号、来源筛选、注册时间查询、封禁管理 |
| 管理员管理 | 后台账号 CRUD、角色分配、创建时间搜索 |
| 角色管理 | 角色标识配置与权限分配 |
| 权限管理 | 系统功能权限项管理 |
| 个人中心 | 管理员个人信息与密码修改 |

### 内容管理

| 模块 | 说明 |
|------|------|
| 博物馆管理 | 博物馆信息 CRUD |
| 朝代管理 | 朝代数据维护 |
| 艺术家管理 | 艺术家信息管理 |
| 地点管理 | 出土地点/收藏地点管理 |
| 文物管理 | 文物信息 CRUD、图片管理 |
| 文物类型管理 | 文物分类维护 |
| 批量导入 | CSV/Excel 文件上传、字段映射、预览确认、错误报告 |

### 用户行为记录

| 模块 | 说明 |
|------|------|
| 用户收藏 | 收藏记录查看与管理 |
| 用户点赞 | 点赞记录查看 |
| 用户动态 | 用户发布的动态内容管理 |
| 评论模块 | 用户评论查看与管理 |
| 用户关注 | 关注关系管理 |
| 用户上传 | 上传内容管理 |
| 浏览历史 | 用户浏览记录查看 |
| 行为日志 | 自动采集的用户行为日志（定时扫描同步） |

### 审核与风控

| 模块 | 说明 |
|------|------|
| 审核管理 | 内容审核（动态/评论/上传）、批量审核、时间范围搜索 |
| 审核统计 | 审核数据可视化统计 |
| 审核策略 | 自动审核策略配置 |
| 敏感词库 | 敏感词 CRUD、Redis 缓存加速 |
| 违规类型 | 违规分类管理 |
| 违规处罚 | 处罚记录管理 |
| 申诉管理 | 用户申诉处理 |

### 知识问答对接

| 模块 | 说明 |
|------|------|
| 问答日志 | 问答记录查看、状态/意图/关键词/时间筛选 |
| 用户反馈 | 反馈记录查看与筛选 |
| 失败问题 | 失败问题追踪与分类 |
| 审核任务 | 问答审核任务处理（通过/驳回/修复） |
| 问答统计 | 失败类型与不准确类型统计 |

### 系统运维

| 模块 | 说明 |
|------|------|
| 公告管理 | 系统公告发布与管理 |
| 备份管理 | 数据备份与恢复 |
| 备份策略 | 自动备份策略配置 |
| 恢复日志 | 数据恢复操作记录 |
| 爬取任务 | 数据爬取任务管理 |
| 系统配置 | 系统参数配置 |
| 日志管理 | 操作日志查看 |

## 快速开始

### 前置条件

- **JDK 21+**
- **Node.js 18+**（推荐 20+）
- **Maven 3.8+**
- **MySQL 8.0+**
- **Redis 6.0+**

### 1. 克隆项目

```bash
git clone <repository-url>
cd RelicAdmin
```

### 2. 配置数据库

创建 MySQL 数据库并执行初始化脚本，然后修改配置文件：

```yaml
# relic-server/src/main/resources/application-dev.yml
relic:
  datasource:
    host: your-mysql-host
    port: 3306
    database: your-database
    username: your-username
    password: your-password
```

### 3. 配置 Redis

```yaml
# relic-server/src/main/resources/application-dev.yml
relic:
  redis:
    host: your-redis-host
    port: 6379
    password: your-password
    database: 0
```

### 4. 启动后端

```bash
# 在项目根目录执行
mvn clean install -DskipTests
mvn spring-boot:run -pl relic-server
```

后端启动后访问：
- 应用地址：`http://localhost:8080`
- API 文档：`http://localhost:8080/doc.html`

### 5. 启动前端

```bash
cd relic-frontend
npm install
npm run dev
```

前端启动后访问：`http://localhost:5173`

### 6. 登录系统

使用默认管理员账号登录后台管理系统。

## 配置说明

### 后端配置

配置文件位于 `relic-server/src/main/resources/`，采用 Spring Profile 机制：

| 文件 | 说明 |
|------|------|
| `application.yml` | 主配置（端口、数据源引用、MyBatis、JWT、Knife4j） |
| `application-dev.yml` | 开发环境配置（数据库、Redis、OSS、QA 子系统地址） |

#### 关键配置项

```yaml
# 服务端口
server:
  port: 8080

# JWT 四端独立密钥
relic:
  jwt:
    admin-secret-key: <base64-key>      # 管理端
    knowledge-secret-key: <base64-key>  # 知识问答端
    museum-secret-key: <base64-key>     # 博物馆端
    user-secret-key: <base64-key>       # 用户端

# 知识问答子系统地址
relic:
  qa:
    base-url: http://127.0.0.1:8000

# 敏感词缓存策略
relic:
  cache:
    sensitive-word:
      key-prefix: "relic:sensitive:words"
      ttl: 30m          # 缓存过期时间
      enabled: true      # 是否启用 Redis 缓存

# 文件上传限制
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 20MB
```

### 前端配置

配置文件为 `relic-frontend/vite.config.js`：

```javascript
// API 代理配置
server: {
  proxy: {
    '^/admin/': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '^/user/': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## API 文档

项目集成 Knife4j（基于 OpenAPI 3），启动后端后访问：

```
http://localhost:8080/doc.html
```

### API 路由结构

| 前缀 | 端点 | 说明 |
|------|------|------|
| `/admin/**` | 管理端 | 后台管理接口，JWT admin token 鉴权 |
| `/museum/**` | 博物馆端 | 博物馆管理接口，JWT museum token 鉴权 |
| `/knowledge/**` | 知识问答端 | 问答子系统接口，JWT knowledge token 鉴权 |
| `/user/**` | 用户端 | 前台用户接口，JWT user token 鉴权 |

## 项目结构

```
RelicAdmin/
├── relic-common/                  # 公共模块
│   └── src/main/java/com/relic/
│       ├── constant/              # 常量定义
│       ├── context/               # 上下文工具
│       ├── enumeration/           # 枚举类型
│       ├── exception/             # 自定义异常
│       ├── json/                  # JSON 序列化配置
│       ├── properties/            # 配置属性绑定
│       ├── result/                # 统一响应封装
│       └── utils/                 # 工具类（JWT、OSS、HTTP）
│
├── relic-pojo/                    # 实体模块
│   └── src/main/java/com/relic/
│       ├── dto/                   # 数据传输对象
│       ├── entity/                # 数据库实体
│       └── vo/                    # 视图对象
│
├── relic-server/                  # 服务模块
│   └── src/main/java/com/relic/
│       ├── annotation/            # 自定义注解（自动填充、操作日志）
│       ├── aspect/                # AOP 切面
│       ├── config/                # 配置类（Redis、OSS、Security、WebSocket）
│       ├── controller/            # 控制器
│       │   ├── admin/             # 管理端接口
│       │   ├── knowledge/         # 知识问答端接口
│       │   ├── museum/            # 博物馆端接口
│       │   └── user/              # 用户端接口
│       ├── converter/             # 对象转换器
│       ├── handler/               # 全局异常处理
│       ├── interceptor/           # JWT 拦截器（四端独立）
│       ├── mapper/                # MyBatis Mapper 接口
│       ├── service/               # 业务接口与实现
│       └── task/                  # 定时任务（行为同步、审核同步）
│   └── src/main/resources/
│       ├── application.yml        # 主配置
│       ├── application-dev.yml    # 开发环境配置
│       └── mapper/                # MyBatis XML 映射文件
│
├── relic-frontend/                # 前端模块
│   └── src/
│       ├── api/                   # API 请求封装
│       ├── components/            # 公共组件
│       ├── layout/                # 布局组件
│       ├── router/                # 路由配置
│       ├── stores/                # Pinia 状态管理
│       ├── styles/                # 全局样式
│       ├── utils/                 # 工具函数
│       └── views/                 # 页面组件
│
└── pom.xml                        # 父 POM
```

## 贡献指南

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feature/your-feature`
3. 提交变更：`git commit -m "Add your feature"`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

### 开发规范

- 后端遵循 RESTful API 设计风格
- 数据库字段使用下划线命名，Java 代码使用驼峰命名（MyBatis 自动映射）
- 统一使用 `Result<T>` 封装 API 响应
- 操作日志通过 `@OperationLog` 注解 + AOP 自动记录
- 前端组件采用 Vue 3 Composition API（`<script setup>`）
- API 请求统一通过 `src/api/` 目录封装，使用 Axios 拦截器处理鉴权与错误

## 许可证

本项目仅供学习和教学使用。

## 联系方式

| 成员 | 角色 |
|------|------|
| 马虎虎 | 项目负责人 / 后端开发 |
| 叶叶新 | 后端开发 |
| 李泽林 | 前端开发 |
| 李昱昂 | 前端开发 |
| 李兴冉 | 测试 |
