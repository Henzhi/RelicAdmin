import request from './request'

export const previewImport = (formData) => request.post('/artifact-import/preview', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

export const confirmImport = (formData) => request.post('/artifact-import/confirm', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

export const getImportHistory = (params) => request.get('/artifact-import/history', { params })

export const getImportDetail = (id) => request.get(`/artifact-import/history/${id}`)

export const downloadTemplate = (fileType = 'xlsx') => request.get('/artifact-import/template', {
  params: { fileType },
  responseType: 'blob'
})
