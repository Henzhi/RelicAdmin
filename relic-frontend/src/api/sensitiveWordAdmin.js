import adminApi from './request'

export function getSensitiveWordPage(params) {
  return adminApi.get('/sensitive-word/page', { params })
}

export function createSensitiveWord(data) {
  return adminApi.post('/sensitive-word', data)
}

export function updateSensitiveWord(id, data) {
  return adminApi.put(`/sensitive-word/${id}`, data)
}

export function updateSensitiveWordStatus(id, status) {
  return adminApi.put(`/sensitive-word/${id}/status`, { status })
}

export function deleteSensitiveWord(id) {
  return adminApi.delete(`/sensitive-word/${id}`)
}

export function batchImportSensitiveWords(content) {
  return adminApi.post('/sensitive-word/import', { content })
}