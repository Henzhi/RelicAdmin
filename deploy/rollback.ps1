# =====================================================
# RelicAdmin 快速回滚脚本 (Windows)
# 用法: .\rollback.ps1 -JarPath <上一版本jar绝对路径>
# 说明: 保留 releases/ 下最近 N 个版本，回滚时只需指定要恢复的 jar
# =====================================================
param(
    [Parameter(Mandatory = $true)]
    [string]$JarPath,
    [string]$Port = "8080",
    [string]$AppDir = "."
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $JarPath)) {
    Write-Host "[ERROR] jar 不存在: $JarPath" -ForegroundColor Red
    exit 1
}

Write-Host "=== 1. 停止当前应用 ===" -ForegroundColor Cyan
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*java*" } | ForEach-Object {
    # 仅停止监听目标端口的 java 进程（保守策略，不误杀其他 java）
    $conn = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($conn -and $conn.OwningProcess -eq $_.Id) {
        Write-Host "停止 PID $($_.Id) (端口 $Port)"
        Stop-Process -Id $_.Id -Force
    }
}
Start-Sleep -Seconds 2

Write-Host "=== 2. 启动上一版本 ===" -ForegroundColor Cyan
$env:SPRING_PROFILES_ACTIVE = if ($env:SPRING_PROFILES_ACTIVE) { $env:SPRING_PROFILES_ACTIVE } else { "prod" }
# 传递端口参数，防止误用默认 8080 导致端口冲突
Start-Process -FilePath "java" -ArgumentList "-jar", (Resolve-Path $JarPath), "--server.port=$Port" `
    -RedirectStandardOutput "rollback-out.log" -RedirectStandardError "rollback-err.log" -NoNewWindow

Write-Host "=== 3. 健康检查 (等待 $($env:HEALTH_TIMEOUT)) ===" -ForegroundColor Cyan
Start-Sleep -Seconds 15
try {
    $health = Invoke-WebRequest -Uri "http://localhost:$Port/v1/actuator/health" -UseBasicParsing -TimeoutSec 5
    if ($health.StatusCode -eq 200) {
        Write-Host "[OK] 回滚后健康检查通过" -ForegroundColor Green
    } else {
        Write-Host "[WARN] 健康检查 HTTP $($health.StatusCode)，请人工确认" -ForegroundColor Yellow
    }
} catch {
    Write-Host "[WARN] 健康检查失败: $($_.Exception.Message)（应用可能在启动中，请人工确认日志）" -ForegroundColor Yellow
}
Write-Host "=== 回滚流程完成 ===" -ForegroundColor Green
