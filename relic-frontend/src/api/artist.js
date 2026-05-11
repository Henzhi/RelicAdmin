import adminApi from './request'

export function getArtistPage(params) {
  return adminApi.get('/artist/page', { params })
}

export function getArtistById(id) {
  return adminApi.get('/artist/' + id)
}

export function createArtist(data) {
  return adminApi.post('/artist', data)
}

export function updateArtist(id, data) {
  return adminApi.put('/artist/' + id, data)
}

export function deleteArtist(id) {
  return adminApi.delete('/artist/' + id)
}