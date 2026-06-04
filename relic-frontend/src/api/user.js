import adminApi from './request'

export function getUserPage(params) {
  return adminApi.get('/user/page', { params })
}

export function createUser(data) {
  return adminApi.post('/user', data)
}

export function deleteUser(id) {
  return adminApi.delete(`/user/${id}`)
}

export function banUser(userId, data) {
  return adminApi.put(`/user/ban/${userId}`, data)
}

// 弃用
// export function assignUserRoles(userId, data) {
//   return adminApi.put(`/user/${userId}/roles`, data)
// }

export function disableComment(userId, data) {
  return adminApi.put(`/user/comment-disable/${userId}`, data)
}

export function disableUpload(userId, data) {
  return adminApi.put(`/user/upload-disable/${userId}`, data)
}