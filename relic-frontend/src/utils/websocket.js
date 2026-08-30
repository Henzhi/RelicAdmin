import { ElNotification } from 'element-plus'

/**
 * 管理端 WebSocket 客户端。
 *
 * - 连接地址：ws(s)://{host}/v1/ws/{userId}?token={admin_token}（服务端校验 JWT 且要求 sid 与 token 声明一致）
 * - 心跳：每 30s 发送 "ping"，服务端回 "pong"；服务端 90s 无报文会回收连接
 * - 重连：断开后按 1s→2s→4s…30s 指数退避自动重连；鉴权失败（关闭码 1008）不重连
 * - 事件：服务端推送 {type, data, timestamp} JSON，type=backup/audit 时弹出通知
 */

let ws = null
let heartbeatTimer = null
let reconnectTimer = null
let reconnectDelay = 1000
let manuallyClosed = false
let currentUser = null

const HEARTBEAT_INTERVAL = 30000
const MAX_RECONNECT_DELAY = 30000

function startHeartbeat() {
  stopHeartbeat()
  heartbeatTimer = setInterval(() => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send('ping')
    }
  }, HEARTBEAT_INTERVAL)
}

function stopHeartbeat() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer)
    heartbeatTimer = null
  }
}

function scheduleReconnect() {
  if (manuallyClosed || reconnectTimer) return
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null
    reconnectDelay = Math.min(reconnectDelay * 2, MAX_RECONNECT_DELAY)
    connect(currentUser?.id)
  }, reconnectDelay)
}

function handleMessage(raw) {
  let msg
  try {
    msg = JSON.parse(raw)
  } catch {
    return
  }
  if (!msg || !msg.type) return
  if (msg.type === 'backup') {
    const ok = msg.data?.result === 'success'
    ElNotification({
      title: ok ? '备份完成' : '备份失败',
      message: `${msg.data?.backupName || '备份任务'} ${ok ? '已成功完成' : '执行失败，请查看备份日志'}`,
      type: ok ? 'success' : 'error',
      duration: 6000
    })
  } else if (msg.type === 'audit') {
    ElNotification({
      title: '审核任务更新',
      message: msg.data?.batch
        ? `批量审核完成：${msg.data.count} 条记录判定为 ${msg.data.result}`
        : `审核记录 ${msg.data?.auditRecordId} 已判定为 ${msg.data?.result}`,
      type: 'info',
      duration: 6000
    })
  }
}

/**
 * 建立（或确保）WebSocket 连接。重复调用时若已在连接中则忽略。
 * @param {number} userId 当前管理员ID（须与 token 中的 user_id 一致）
 */
export function connect(userId) {
  if (!userId || (ws && ws.readyState <= WebSocket.OPEN)) return
  const token = localStorage.getItem('admin_token')
  if (!token) return
  manuallyClosed = false
  currentUser = { id: userId }

  const proto = window.location.protocol === 'https:' ? 'wss' : 'ws'
  const url = `${proto}://${window.location.host}/v1/ws/${userId}?token=${encodeURIComponent(token)}`
  ws = new WebSocket(url)

  ws.onopen = () => {
    reconnectDelay = 1000
    startHeartbeat()
  }
  ws.onmessage = (event) => {
    if (event.data === 'pong') return
    handleMessage(event.data)
  }
  ws.onclose = (event) => {
    stopHeartbeat()
    ws = null
    // 1008 = 服务端鉴权拒绝，重连无意义
    if (event.code !== 1008) {
      scheduleReconnect()
    }
  }
  ws.onerror = () => {
    try {
      ws?.close()
    } catch {
      /* ignore */
    }
  }
}

/** 主动断开并停止重连（登出/页面卸载时调用） */
export function closeWebSocket() {
  manuallyClosed = true
  stopHeartbeat()
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  if (ws) {
    try {
      ws.close()
    } catch {
      /* ignore */
    }
    ws = null
  }
  currentUser = null
}
