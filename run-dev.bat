@echo off
rem ============================================================
rem  RelicAdmin 后端开发启动脚本（Windows）
rem  用法：双击运行 或 命令行执行 run-dev.bat
rem  说明：根 pom 为聚合模块，spring-boot:run 必须针对 relic-server
rem        先 install 依赖模块（relic-common/relic-pojo），确保新增类
rem        （如 PageQuery）被安装到本地仓库，避免运行时 ClassNotFoundException
rem ============================================================
cd /d "%~dp0"
echo [1/3] 检查项目专属 Redis 容器...
docker start relic-redis >nul 2>&1 && echo       relic-redis 已启动 (localhost:6380) || echo       未找到 relic-redis 容器，请先创建

echo [2/3] 构建并安装依赖模块 (relic-common/relic-pojo)...
call mvn install -pl relic-common,relic-pojo -DskipTests

echo [3/3] 启动后端服务 (http://localhost:8080)...
call mvn -f relic-server/pom.xml spring-boot:run
pause
