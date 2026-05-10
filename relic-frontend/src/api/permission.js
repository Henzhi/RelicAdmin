import adminApi from './request'

export function getPermissionPage(params) {
  return adminApi.get('/permission/page', { params })
}

export function getPermissionList() {
  return adminApi.get('/permission/list')
}

export function createPermission(data) {
  return adminApi.post('/permission', data)
}

export function updatePermission(id, data) {
  return adminApi.put(`/permission/${id}`, data)
}

export function deletePermission(id) {
  return adminApi.delete(`/permission/${id}`)
}