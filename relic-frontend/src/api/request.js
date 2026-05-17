import axios from 'axios'
import { ElMessage } from 'element-plus'

const adminApi = axios.create({
  baseURL: '/admin',
  timeout: 15000
})

export const longTimeoutApi = axios.create({
  baseURL: '/admin',
  timeout: 120000
})

function applyInterceptors(api) {
  api.interceptors.request.use(config => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers['token'] = token
    }
    config.headers['Content-Type'] = 'application/json;charset=UTF-8'
    return config
  })
  api.interceptors.response.use(
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
        localStorage.removeItem('admin_token')
        window.location.href = '/login'
      }
      ElMessage.error(error.message || '网络错误')
      return Promise.reject(error)
    }
  )
}

applyInterceptors(adminApi)
applyInterceptors(longTimeoutApi)

export default adminApi
