import { defineStore } from 'pinia'
import { ref } from 'vue'
import { processAvatarUrl } from '@/utils/avatar'

export const useAdminStore = defineStore('admin', () => {
  const admin = ref(null)
  const token = ref(localStorage.getItem('adminToken'))

  // 设置管理员信息
  const setAdmin = (adminInfo) => {
    if (adminInfo) {
      // 处理头像URL，确保是完整的可访问地址
      adminInfo.avatar = processAvatarUrl(adminInfo.avatar, adminInfo.username)
    }
    admin.value = adminInfo
    localStorage.setItem('admin', JSON.stringify(adminInfo))
  }

  // 设置token
  const setToken = (tokenValue) => {
    token.value = tokenValue
    localStorage.setItem('adminToken', tokenValue)
  }

  // 清除管理员信息
  const clearAdmin = () => {
    admin.value = null
    token.value = null
    localStorage.removeItem('admin')
    localStorage.removeItem('adminToken')
  }

  // 初始化管理员信息
  const initAdmin = () => {
    const savedAdmin = localStorage.getItem('admin')
    if (savedAdmin) {
      admin.value = JSON.parse(savedAdmin)
      // 处理头像URL，确保是完整的可访问地址
      if (admin.value) {
        admin.value.avatar = processAvatarUrl(admin.value.avatar, admin.value.username)
      }
    }
  }

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!token.value && !!admin.value
  }

  return {
    admin,
    token,
    setAdmin,
    setToken,
    clearAdmin,
    initAdmin,
    isLoggedIn
  }
})

