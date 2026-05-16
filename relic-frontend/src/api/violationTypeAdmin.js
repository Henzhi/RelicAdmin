import adminApi from './request'

export function getViolationTypePage(params) {
    return adminApi.get('/violation-type/page', { params })
}

export function createViolationType(data) {
    return adminApi.post('/violation-type', data)
}

export function updateViolationType(id, data) {
    return adminApi.put(`/violation-type/${id}`, data)
}

export function updateViolationTypeStatus(id, status) {
    return adminApi.put(`/violation-type/${id}/status`, { status })
}

export function deleteViolationType(id) {
    return adminApi.delete(`/violation-type/${id}`)
}