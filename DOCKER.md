# RelicAdmin 容器化部署指南

本文档说明如何使用 Docker 部署 RelicAdmin 的前后端服务。项目使用远程 MySQL 与 Redis，**不在容器内启动数据库**。

## 架构概览

```
浏览器
  │
  ▼
relic-frontend (Nginx :8081)
  ├─ 静态资源（Vue 构建产物）
  └─ /admin/*、/user/* 反向代理
         │
         ▼
relic-server (Spring Boot :8080，仅容器内网访问)
  ├─ 远程 MySQL
  └─ 远程 Redis
```

| 服务 | 镜像 | 对外端口 | 说明 |
|------|------|----------|------|
| `relic-frontend` | `relic-frontend:latest` | `8081` | Nginx 托管前端并代理 API |
| `relic-server` | `relic-server:latest` | 无（内部 8080） | Spring Boot 后端 |

## 环境要求

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)（Windows / macOS）或 Docker Engine + Compose（Linux）
- 能访问远程 MySQL、Redis 的网络环境
- 首次构建需下载 Maven / Node 基础镜像，建议预留 **10～20 分钟**

> **Windows 注意**：执行 `docker compose` 前须先启动 Docker Desktop，托盘图标显示 Running 后再操作。

## 快速开始

### 1. 配置环境变量

复制模板并填入真实连接信息：

```bash
# Linux / macOS
cp .env.example .env

# Windows
copy .env.example .env
```

编辑 `.env`，至少配置以下项：

| 变量 | 说明 |
|------|------|
| `RELIC_DATASOURCE_HOST` | MySQL 主机地址 |
| `RELIC_DATASOURCE_PORT` | MySQL 端口 |
| `RELIC_DATASOURCE_DATABASE` | 数据库名 |
| `RELIC_DATASOURCE_USERNAME` | 用户名 |
| `RELIC_DATASOURCE_PASSWORD` | 密码 |
| `RELIC_REDIS_HOST` | Redis 主机地址 |
| `RELIC_REDIS_PORT` | Redis 端口 |
| `RELIC_REDIS_PASSWORD` | Redis 密码 |
| `RELIC_REDIS_DATABASE` | Redis 库编号 |
| `SPRING_DATA_REDIS_SSL_ENABLED` | 云 Redis（如 Upstash）通常设为 `true` |

`.env` 已被 `.gitignore` 忽略，**请勿提交到 Git**。仓库中仅保留 `.env.example` 作为模板。

### 2. 构建并启动

在项目根目录执行：

```bash
docker compose up -d --build
```

### 3. 访问

- 管理后台：**http://localhost:8081**

## 常用命令

```bash
# 查看容器状态
docker compose ps

# 查看后端日志
docker logs relic-server -f

# 查看前端日志
docker logs relic-frontend -f

# 停止服务
docker compose down

# 重新构建并启动（代码更新后）
docker compose up -d --build

# 仅重建某一服务
docker compose up -d --build relic-server
docker compose up -d --build relic-frontend
```

## 单独构建镜像

不通过 Compose 时，可分别构建：

```bash
# 后端（在项目根目录）
docker build -t relic-server:latest .

# 前端
docker build -t relic-frontend:latest ./relic-frontend
```

## 相关文件说明

| 文件 | 作用 |
|------|------|
| `docker-compose.yml` | 编排前后端服务，从 `.env` 注入配置 |
| `Dockerfile` | 后端多阶段构建（Maven 编译 → JRE 21 运行） |
| `relic-frontend/Dockerfile` | 前端多阶段构建（Node 构建 → Nginx 运行） |
| `relic-frontend/nginx.conf` | 静态资源与 API 反向代理规则 |
| `.env.example` | 环境变量模板（可提交 Git） |
| `.env` | 本地真实配置（**不提交 Git**） |
| `.dockerignore` | 后端构建上下文排除规则 |

## 配置说明

### 后端数据库 / Redis

`docker-compose.yml` 通过 `env_file: .env` 将变量注入 `relic-server` 容器。这些变量对应 `application.yml` 中的占位符：

- `RELIC_DATASOURCE_*` → `relic.datasource.*`
- `RELIC_REDIS_*` → `relic.redis.*`

Spring Boot 会自动将环境变量中的 `_` 映射为配置项中的 `.`（例如 `RELIC_DATASOURCE_HOST` → `relic.datasource.host`）。

### 前端 API 代理

生产环境下，Nginx 将 `/admin/`、`/user/` 请求转发至容器内 `relic-server:8080`，行为与本地开发时 `vite.config.js` 中的 dev proxy 一致，前端代码无需修改。

### 修改对外端口

默认前端映射为 `8081:80`。如需改为 80 端口，编辑 `docker-compose.yml`：

```yaml
relic-frontend:
  ports:
    - "80:80"
```

## 常见问题

### 1. 无法连接 Docker API

```
failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine
```

**原因**：Docker Desktop 未启动。  
**处理**：打开 Docker Desktop，等待状态变为 Running 后重试。

### 2. 容器名称冲突

```
The container name "/relic-server" is already in use
```

**处理**：

```bash
docker compose down
docker compose up -d
```

### 3. Redis 连接失败（Upstash 等）

- 确认 `SPRING_DATA_REDIS_SSL_ENABLED=true`
- 密码格式为 `default:xxx` 时，可尝试增加：
  ```env
  SPRING_DATA_REDIS_USERNAME=default
  ```
  并将 `RELIC_REDIS_PASSWORD` 改为冒号后的 token 部分

### 4. 首次构建较慢

后端需在镜像内执行 `mvn package` 并下载依赖，属正常现象。后续构建会利用 Docker 层缓存加快速度。

## 安全建议

1. **不要**将 `.env`、`application-dev.yml` 提交到公开仓库
2. 若密码曾误提交到 Git，请**立即轮换** MySQL、Redis、OSS 等密钥
3. 生产环境建议通过服务器环境变量或密钥管理服务注入配置，而非明文文件

## 本地非 Docker 开发

本地开发仍可使用原有方式：

- 后端：IDE 或 `mvn spring-boot:run`（需配置 `application-dev.yml`）
- 前端：`cd relic-frontend && npm run dev`

本地开发配置文件请参考 `relic-server/src/main/resources/application-dev.yml.example` 自行复制为 `application-dev.yml`。
