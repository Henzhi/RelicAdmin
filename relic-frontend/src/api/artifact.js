import adminApi from './request'

export function getArtifactPage(params) {
  return adminApi.get('/artifact/page', { params })
}

export function getArtifactById(id) {
  return adminApi.get('/artifact/' + id)
}

export function createArtifact(data) {
  return adminApi.post('/artifact', data)
}

export function updateArtifact(id, data) {
  return adminApi.put('/artifact/' + id, data)
}

export function deleteArtifact(id) {
  return adminApi.delete('/artifact/' + id)
}