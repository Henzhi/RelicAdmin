import adminApi from './request'

export function getCrawlTaskPage(params) {
  return adminApi.get('/crawl-task/page', { params })
}

export function getCrawlTaskDetail(id) {
  return adminApi.get(`/crawl-task/${id}`)
}

export function createCrawlTask(data) {
  return adminApi.post('/crawl-task', data)
}

export function updateCrawlTask(id, data) {
  return adminApi.put(`/crawl-task/${id}`, data)
}

export function executeCrawlTask(id) {
  return adminApi.post(`/crawl-task/${id}/execute`)
}

export function pauseCrawlTask(id) {
  return adminApi.post(`/crawl-task/${id}/pause`)
}

export function resumeCrawlTask(id) {
  return adminApi.post(`/crawl-task/${id}/resume`)
}

export function deleteCrawlTask(id) {
  return adminApi.delete(`/crawl-task/${id}`)
}

export function getCrawlTaskLogs(params) {
  return adminApi.get('/crawl-task/logs/page', { params })
}