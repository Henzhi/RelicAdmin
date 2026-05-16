import adminApi from './request'

export function getAuditStrategies() {
  return adminApi.get('/audit-strategy/list')
}

export function updateAuditStrategy(id, data) {
  return adminApi.put(`/audit-strategy/${id}`, data)
}