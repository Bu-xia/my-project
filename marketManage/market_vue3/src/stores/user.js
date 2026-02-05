import { defineStore } from 'pinia'
import { ref } from 'vue'
import { processAvatarUrl } from '@/utils/avatar'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))

  // 设置用户信息
  const setUser = (userInfo) => {
    if (userInfo) {
      // 处理头像URL，确保是完整的可访问地址
      userInfo.avatar = processAvatarUrl(userInfo.avatar, userInfo.username)
    }
    user.value = userInfo
    localStorage.setItem('user', JSON.stringify(userInfo))
  }

  // 设置token
  const setToken = (tokenValue) => {
    token.value = tokenValue
    localStorage.setItem('token', tokenValue)
  }

  // 清除用户信息
  const clearUser = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  // 初始化用户信息
  const initUser = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      user.value = JSON.parse(savedUser)
      // 处理头像URL，确保是完整的可访问地址
      if (user.value) {
        user.value.avatar = processAvatarUrl(user.value.avatar, user.value.username)
      }
    }
  }

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!token.value && !!user.value
  }

  return {
    user,
    token,
    setUser,
    setToken,
    clearUser,
    initUser,
    isLoggedIn
  }
})
