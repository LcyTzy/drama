import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 初始化用户信息与 token
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(localStorage.getItem('token') || '')

  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)

  // 设置用户信息并持久化
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 设置 token 并持久化
  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }

  // 清除用户信息与 token
  function clearUserInfo() {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    setUserInfo,
    setToken,
    clearUserInfo
  }
})
