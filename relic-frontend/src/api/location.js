import adminApi from './request'

export function getLocationTree() {
  return adminApi.get('/location/tree')
}

export function getLocationList(params) {
  return adminApi.get('/location/list', { params })
}

export function createLocation(data) {
  return adminApi.post('/location', data)
}

export function updateLocation(id, data) {
  return adminApi.put('/location/' + id, data)
}

export function deleteLocation(id) {
  return adminApi.delete('/location/' + id)
}