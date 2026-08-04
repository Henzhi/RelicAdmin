import axios from 'axios'
import { ElMessage } from 'element-plus'

// H-07/M-10：接口版本前缀 + 支持通过 VITE_API_BASE 覆盖生产 API 地址
const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

const userApi = axios.create({
  baseURL: API_BASE + '/v1/user',
  timeout: 15000
})

userApi.interceptors.request.use(config => {
  const token = localStorage.getItem('user_token')
  if (token) {
    // 与后端 JwtProperties.user-token-name 保持一致（默认 token），否则用户端鉴权永远 401
    config.headers['token'] = token
  }
  config.headers['Content-Type'] = 'application/json;charset=UTF-8'
  return config
})

userApi.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('user_token')
      ElMessage.warning('登录已过期，请重新登录')
      window.location.href = '/login'
    } else {
      ElMessage.error((error.response && error.response.data && error.response.data.msg) || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default userApi