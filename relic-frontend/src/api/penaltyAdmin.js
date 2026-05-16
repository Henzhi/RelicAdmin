import adminApi from './request'

export function getViolationTypes() {
  return adminApi.get('/violation-type/list')
}

export function createPenalty(data) {
  return adminApi.post('/penalty', data)
}

export function getPenaltyPage(params) {
  return adminApi.get('/penalty/page', { params })
}

export function revokePenalty(id, data) {
  return adminApi.put(`/penalty/${id}/revoke`, data)
}