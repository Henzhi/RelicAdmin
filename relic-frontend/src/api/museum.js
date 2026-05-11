import adminApi from './request'

export function getMuseumPage(params) {
  return adminApi.get('/museum/page', { params })
}

export function getMuseumById(id) {
  return adminApi.get('/museum/' + id)
}

export function createMuseum(data) {
  return adminApi.post('/museum', data)
}

export function updateMuseum(id, data) {
  return adminApi.put('/museum/' + id, data)
}

export function deleteMuseum(id) {
  return adminApi.delete('/museum/' + id)
}