import adminApi from './request'

export function getSystemConfigPage(params) {
  return adminApi.get('/system-config/page', { params })
}

export function getSystemConfigByGroup(group) {
  return adminApi.get(`/system-config/group/${group}`)
}

export function getSystemConfigById(id) {
  return adminApi.get(`/system-config/${id}`)
}

export function createSystemConfig(data) {
  return adminApi.post('/system-config', data)
}

export function updateSystemConfig(id, data) {
  return adminApi.put(`/system-config/${id}`, data)
}

export function updateSystemConfigValue(id, configValue) {
  return adminApi.put(`/system-config/${id}/value`, { configValue })
}

export function deleteSystemConfig(id) {
  return adminApi.delete(`/system-config/${id}`)
}

export function getFeatureToggles() {
  return adminApi.get('/system-config/feature-toggles')
}

export function toggleFeature(id, configValue) {
  return adminApi.put(`/system-config/feature-toggles/${id}/toggle`, { configValue })
}
