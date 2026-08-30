import adminApi from './request'

/**
 * 管理端登录。
 * @param {{username: string, password: string}} data
 * @returns {Promise<{code: number, msg: string, data: {id: number, username: string, realName: string, avatarUrl: string, token: string}}>}
 */
export function login(data) {
  return adminApi.post('/employee/login', data)
}

export function logout() {
  return adminApi.post('/employee/logout')
}