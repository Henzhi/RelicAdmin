import adminApi from './request'

export function getArtifactTypePage(params) {
  return adminApi.get('/artifact-type/page', { params })
}

export function getArtifactTypeList() {
  return adminApi.get('/artifact-type/list')
}

export function createArtifactType(data) {
  return adminApi.post('/artifact-type', data)
}

export function updateArtifactType(id, data) {
  return adminApi.put('/artifact-type/' + id, data)
}

export function deleteArtifactType(id) {
  return adminApi.delete('/artifact-type/' + id)
}