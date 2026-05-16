import adminApi from './request'

export function getFavoritePage(params) {
  return adminApi.get('/favorite/page', { params })
}

export function deleteFavorite(userId, artifactId) {
  return adminApi.delete(`/favorite/${userId}/${artifactId}`)
}