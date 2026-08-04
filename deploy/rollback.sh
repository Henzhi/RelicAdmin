#!/usr/bin/env bash
# =====================================================
# RelicAdmin 快速回滚脚本 (Linux)
# 用法: ./rollback.sh <上一版本jar绝对路径> [端口] [应用目录]
# 说明: 保留 releases/ 下最近 N 个版本，回滚时指定要恢复的 jar
# =====================================================
set -euo pipefail

JAR_PATH="${1:?用法: ./rollback.sh <jar路径> [端口] [应用目录]}"
PORT="${2:-8080}"
APP_DIR="${3:-.}"
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-30}"

if [[ ! -f "$JAR_PATH" ]]; then
  echo "[ERROR] jar 不存在: $JAR_PATH" >&2
  exit 1
fi

echo "=== 1. 停止当前应用 (端口 $PORT) ==="
PID=$(lsof -ti tcp:"$PORT" 2>/dev/null || true)
if [[ -n "$PID" ]]; then
  echo "停止 PID: $PID"
  kill "$PID" 2>/dev/null || true
  sleep 2
fi

echo "=== 2. 启动上一版本 ==="
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
cd "$APP_DIR"
nohup java -jar "$JAR_PATH" > rollback-out.log 2> rollback-err.log &

echo "=== 3. 健康检查 (等待 ${HEALTH_TIMEOUT}s) ==="
for i in $(seq 1 "$HEALTH_TIMEOUT"); do
  if curl -fsS "http://localhost:${PORT}/v1/actuator/health" >/dev/null 2>&1; then
    echo "[OK] 回滚后健康检查通过"
    exit 0
  fi
  sleep 1
done
echo "[WARN] ${HEALTH_TIMEOUT}s 内健康检查未通过，请人工确认日志 rollback-out.log / rollback-err.log" >&2
exit 1
