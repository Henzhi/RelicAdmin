import adminApi, { longTimeoutApi } from './request'

export function getBackupPage(params) {
  return adminApi.get('/backup/page', { params })
}

export function createBackup(data) {
  return adminApi.post('/backup/create', data)
}

export function getBackupDetail(id) {
  return adminApi.get(`/backup/${id}`)
}

export function deleteBackup(id) {
  return adminApi.delete(`/backup/${id}`)
}

export function downloadBackup(id) {
  return adminApi.get(`/backup/${id}/download`, { responseType: 'blob' })
}

export function getBackupStrategy() {
  return adminApi.get('/backup/strategy')
}

export function updateBackupStrategy(id, data) {
  return adminApi.put(`/backup/strategy/${id}`, data)
}

export function getStorageInfo() {
  return adminApi.get('/backup/storage-info')
}

export function restoreFromBackup(data) {
  return longTimeoutApi.post('/backup/restore', data)
}

export function getRestorePage(params) {
  return adminApi.get('/backup/restore/page', { params })
}