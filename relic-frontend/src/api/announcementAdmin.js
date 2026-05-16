import adminApi from './request'

export function getAnnouncementPage(params) {
  return adminApi.get('/announcement/page', { params })
}

export function createAnnouncement(data) {
  return adminApi.post('/announcement', data)
}

export function updateAnnouncement(id, data) {
  return adminApi.put(`/announcement/${id}`, data)
}

export function offlineAnnouncement(id) {
  return adminApi.put(`/announcement/${id}/offline`)
}

export function deleteAnnouncement(id) {
  return adminApi.delete(`/announcement/${id}`)
}