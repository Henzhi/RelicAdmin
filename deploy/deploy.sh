#!/usr/bin/env bash
# =====================================================
# RelicAdmin 标准发布脚本 (Linux)
# 用法: ./deploy.sh <新jar路径> [端口]
# 说明: 保留 releases/ 下最近 KEEP_RELEASES 个版本，current 符号链接指向当前版本，
#       回滚时切回上一版并执行 ./rollback.sh
# =====================================================
set -euo pipefail

JAR_PATH="${1:?用法: ./deploy.sh <新jar路径> [端口]}"
PORT="${2:-8080}"
KEEP_RELEASES="${KEEP_RELEASES:-3}"
RELEASES_DIR="${RELEASES_DIR:-./releases}"

mkdir -p "$RELEASES_DIR"

echo "=== 1. 归档新版本 ==="
VERSION=$(basename "$JAR_PATH")
VERSION="${VERSION%.jar}"
mkdir -p "$RELEASES_DIR/$VERSION"
cp "$JAR_PATH" "$RELEASES_DIR/$VERSION/app.jar"
sha256sum "$JAR_PATH" > "$RELEASES_DIR/$VERSION/app.jar.sha256"
echo "归档完成: $RELEASES_DIR/$VERSION"

echo "=== 2. 停止当前应用 (端口 $PORT) ==="
PID=$(lsof -ti tcp:"$PORT" 2>/dev/null || true)
if [[ -n "$PID" ]]; then
  kill "$PID" 2>/dev/null || true
  sleep 2
fi

echo "=== 3. 切换 current 链接并启动 ==="
ln -sfn "$(pwd)/$RELEASES_DIR/$VERSION" "$(pwd)/current"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
nohup java -jar "current/app.jar" "--server.port=$PORT" > deploy-out.log 2> deploy-err.log &

echo "=== 4. 健康检查 ==="
sleep 15
if curl -fsS "http://localhost:${PORT}/v1/actuator/health" >/dev/null 2>&1; then
  echo "[OK] 发布成功，当前版本: $VERSION"
else
  echo "[WARN] 健康检查未通过，如需回滚: ./rollback.sh $RELEASES_DIR/$(ls -1 "$RELEASES_DIR" | sort | tail -2 | head -1)/app.jar $PORT" >&2
fi

echo "=== 5. 清理过期版本 (保留最近 $KEEP_RELEASES 个) ==="
ls -1dt "$RELEASES_DIR"/*/ | tail -n +$((KEEP_RELEASES + 1)) | xargs -r rm -rf
echo "完成。当前 releases:"
ls -1 "$RELEASES_DIR"
