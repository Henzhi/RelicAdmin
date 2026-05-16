import userApi from './requestUser'

export function addFavorite(data) {
  return userApi.post('/favorite', data)
}

export function removeFavorite(artifactId) {
  return userApi.delete('/favorite/' + artifactId)
}

export function getFavoriteList(params) {
  return userApi.get('/favorite/list', { params })
}

export function checkFavorite(artifactId) {
  return userApi.get('/favorite/check/' + artifactId)
}