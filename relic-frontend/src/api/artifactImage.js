import adminApi from './request'

export function getArtifactImages(artifactId) {
  return adminApi.get('/artifact/' + artifactId + '/images')
}

export function createArtifactImage(artifactId, data) {
  return adminApi.post('/artifact/' + artifactId + '/images', data)
}

export function updateArtifactImage(artifactId, imageId, data) {
  return adminApi.put('/artifact/' + artifactId + '/images/' + imageId, data)
}

export function deleteArtifactImage(artifactId, imageId) {
  return adminApi.delete('/artifact/' + artifactId + '/images/' + imageId)
}

export function setPrimaryImage(artifactId, imageId) {
  return adminApi.put('/artifact/' + artifactId + '/images/' + imageId + '/primary')
}