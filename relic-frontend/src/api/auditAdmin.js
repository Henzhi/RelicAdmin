import adminApi from './request'

export function getAuditPage(params) {
  return adminApi.get('/audit/page', { params })
}

export function auditRecord(id, data) {
  return adminApi.put(`/audit/${id}`, data)
}

export function batchAudit(data) {
  return adminApi.put('/audit/batch', data)
}

export function getAuditStats(params) {
  return adminApi.get('/audit/stats', { params })
}

export function getAuditorStats(params) {
  return adminApi.get('/audit/auditor-stats', { params })
}

export const getContentTypeStats = (params) => adminApi.get('/audit/content-type-stats', { params })