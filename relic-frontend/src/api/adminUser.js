import adminApi from './request'

export function getAdminUserPage(params) {
  return adminApi.get('/admin-user/page', { params })
}

export function getAdminUserById(id) {
  return adminApi.get(`/admin-user/${id}`)
}

export function getCurrentAdmin() {
  return adminApi.get('/admin-user/current')
}

export function createAdminUser(data) {
  return adminApi.post('/admin-user', data)
}

export function updateAdminUser(id, data) {
  return adminApi.put(`/admin-user/${id}`, data)
}

export function deleteAdminUser(id) {
  return adminApi.delete(`/admin-user/${id}`)
}

export function batchDeleteAdminUser(ids) {
  return adminApi.delete('/admin-user/batch', { data: { ids } })
}

export function updateAdminUserStatus(id, status) {
  return adminApi.put(`/admin-user/${id}/status`, null, { params: { status } })
}

export function assignAdminRoles(adminUserId, data) {
  return adminApi.put(`/admin-user/${adminUserId}/roles`, data)
}

export function updateAdminPassword(id, data) {
  return adminApi.put(`/admin-user/${id}/password`, data)
}

export function resetAdminPassword(id, newPassword) {
  return adminApi.put(`/admin-user/${id}/password/reset`, { newPassword })
}

export function updateCurrentProfile(data) {
  return adminApi.put('/admin-user/current/profile', data)
}

export function updateCurrentPassword(data) {
  return adminApi.put('/admin-user/current/password', data)
}

export function uploadCurrentAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return adminApi.post('/admin-user/current/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
