import adminApi from './request'

export function getDynastyPage(params) {
  return adminApi.get('/dynasty/page', { params })
}

export function getDynastyList() {
  return adminApi.get('/dynasty/list')
}

export function createDynasty(data) {
  return adminApi.post('/dynasty', data)
}

export function updateDynasty(id, data) {
  return adminApi.put('/dynasty/' + id, data)
}

export function deleteDynasty(id) {
  return adminApi.delete('/dynasty/' + id)
}