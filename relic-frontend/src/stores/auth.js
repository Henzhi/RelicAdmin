import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_user') || 'null'))

  async function login(form) {
    const res = await loginApi(form)
    token.value = res.data.token
    userInfo.value = {
      id: res.data.id,
      username: res.data.username,
      nickname: res.data.nickname
    }
    localStorage.setItem('admin_token', res.data.token)
    localStorage.setItem('admin_user', JSON.stringify(userInfo.value))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  }

  return { token, userInfo, login, logout }
})