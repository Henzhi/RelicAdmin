import axios from 'axios'
import { ElMessage } from 'element-plus'

const userApi = axios.create({
  baseURL: '/user',
  timeout: 15000
})

userApi.interceptors.request.use(config => {
  const token = localStorage.getItem('user_token')
  if (token) {
    config.headers['authentication'] = token
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
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default userApi