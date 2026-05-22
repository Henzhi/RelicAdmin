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

export function getDatasourcePage(params) {
  return adminApi.get('/system-config/datasource/page', { params })
}

export function getDatasourceById(id) {
  return adminApi.get(`/system-config/datasource/${id}`)
}

export function createDatasource(data) {
  return adminApi.post('/system-config/datasource', data)
}

export function updateDatasource(id, data) {
  return adminApi.put(`/system-config/datasource/${id}`, data)
}

export function testDatasourceConnection(id) {
  return adminApi.post(`/system-config/datasource/${id}/test`)
}

export function deleteDatasource(id) {
  return adminApi.delete(`/system-config/datasource/${id}`)
}

export function getFeatureToggles() {
  return adminApi.get('/system-config/feature-toggles')
}

export function toggleFeature(id, configValue) {
  return adminApi.put(`/system-config/feature-toggles/${id}/toggle`, { configValue })
}