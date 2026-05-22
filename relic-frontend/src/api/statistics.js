import request from './request'

export function getDashboardOverview() {
  return request.get('/statistics/dashboard')
}

export function getUserTrend(days = 30) {
  return request.get('/statistics/user-trend', { params: { days } })
}

export function getVisitTrend(days = 30) {
  return request.get('/statistics/visit-trend', { params: { days } })
}

export function getDataGrowth(days = 30) {
  return request.get('/statistics/data-growth', { params: { days } })
}

export function getArtifactByMuseum() {
  return request.get('/statistics/artifact-by-museum')
}

export function getArtifactByType() {
  return request.get('/statistics/artifact-by-type')
}

export function getArtifactByDynasty() {
  return request.get('/statistics/artifact-by-dynasty')
}

export function getAlertPage(params) {
  return request.get('/statistics/alerts/page', { params })
}

export function resolveAlert(id, remark) {
  return request.put(`/statistics/alerts/${id}/resolve`, null, { params: { remark } })
}