import adminApi from './request'

export function getLikePage(params) {
  return adminApi.get('/like/page', { params })
}

export function deleteLike(userId, artifactId) {
  return adminApi.delete(`/like/${userId}/${artifactId}`)
}