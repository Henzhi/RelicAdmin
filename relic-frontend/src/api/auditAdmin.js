import adminApi from './request'

export function getAuditPage(params) {
  return adminApi.get('/audit/page', { params })
}

export function auditRecord(id, data) {
  return adminApi.put(`/audit/${id}`, data)
}