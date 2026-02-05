import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建专门用于AI请求的axios实例，设置更长的超时时间
const aiRequest = axios.create({
  baseURL: '/api',
  timeout: 60000 // AI请求设置60秒超时
})

// 请求拦截器
aiRequest.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
aiRequest.interceptors.response.use(
  response => {
    const { code, msg, data } = response.data
    
    if (code === 200) {
      return data
    } else {
      ElMessage.error(msg || '请求失败')
      return Promise.reject(new Error(msg || '请求失败'))
    }
  },
  error => {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// AI对话
export const chatWithAI = (message) => {
  return aiRequest.post('/user/ai/chat', { message })
}

