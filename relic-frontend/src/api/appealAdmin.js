import adminApi from './request'

export function getAppealPage(params) {
  return adminApi.get('/appeal/page', { params })
}

export function handleAppeal(id, data) {
  return adminApi.put(`/appeal/${id}`, data)
}