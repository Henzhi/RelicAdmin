import adminApi from './request'

export function getOperationLogPage(params) {
  return adminApi.get('/operation-log/page', { params })
}

export function exportOperationLogCSV(params) {
  return adminApi.get('/operation-log/export-csv', { params, responseType: 'blob' })
}

export function exportOperationLogExcel(params) {
  return adminApi.get('/operation-log/export-excel', { params, responseType: 'blob' })
}

export function getSystemLogPage(params) {
  return adminApi.get('/system-log/page', { params })
}

export function exportSystemLogCSV(params) {
  return adminApi.get('/system-log/export-csv', { params, responseType: 'blob' })
}

export function exportSystemLogExcel(params) {
  return adminApi.get('/system-log/export-excel', { params, responseType: 'blob' })
}

export function getSecurityLogPage(params) {
  return adminApi.get('/security-log/page', { params })
}

export function exportSecurityLogCSV(params) {
  return adminApi.get('/security-log/export-csv', { params, responseType: 'blob' })
}

export function exportSecurityLogExcel(params) {
  return adminApi.get('/security-log/export-excel', { params, responseType: 'blob' })
}