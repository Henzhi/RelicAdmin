import adminApi from './request'

export function getCommentPage(params) {
  return adminApi.get('/comment/page', { params })
}

export function getCommentById(id) {
  return adminApi.get(`/comment/${id}`)
}

export function approveComment(id) {
  return adminApi.put(`/comment/${id}/approve`)
}

export function rejectComment(id, reason) {
  return adminApi.put(`/comment/${id}/reject`, { reason })
}

export function deleteComment(id) {
  return adminApi.delete(`/comment/${id}`)
}