import adminApi from './request'

export function getUserPage(params) {
  return adminApi.get('/user/page', { params })
}

export function banUser(userId, data) {
  return adminApi.put(`/user/ban/${userId}`, data)
}

export function assignUserRoles(userId, data) {
  return adminApi.put(`/user/${userId}/roles`, data)
}

export function disableComment(userId, data) {
  return adminApi.put(`/user/comment-disable/${userId}`, data)
}

export function disableUpload(userId, data) {
  return adminApi.put(`/user/upload-disable/${userId}`, data)
}