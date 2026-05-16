import adminApi from './request'

export function getPostPage(params) {
  return adminApi.get('/post/page', { params })
}

export function getPostById(id) {
  return adminApi.get(`/post/${id}`)
}

export function approvePost(id) {
  return adminApi.put(`/post/${id}/approve`)
}

export function rejectPost(id, reason) {
  return adminApi.put(`/post/${id}/reject`, { reason })
}

export function deletePost(id) {
  return adminApi.delete(`/post/${id}`)
}