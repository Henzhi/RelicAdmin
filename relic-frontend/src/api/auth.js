import adminApi from './request'

export function login(data) {
  return adminApi.post('/employee/login', data)
}

export function logout() {
  return adminApi.post('/employee/logout')
}