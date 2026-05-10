import adminApi from './request'

export function getRolePage(params) {
  return adminApi.get('/role/page', { params })
}

export function getRoleList() {
  return adminApi.get('/role/list')
}

export function createRole(data) {
  return adminApi.post('/role', data)
}

export function updateRole(id, data) {
  return adminApi.put(`/role/${id}`, data)
}

export function deleteRole(id) {
  return adminApi.delete(`/role/${id}`)
}

export function getRolePermissions(roleId) {
  return adminApi.get(`/role/${roleId}/permissions`)
}

export function assignRolePermissions(roleId, data) {
  return adminApi.put(`/role/${roleId}/permissions`, data)
}