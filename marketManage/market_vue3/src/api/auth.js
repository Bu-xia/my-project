import request from '@/utils/request'

// 用户注册
export const register = (data) => {
  return request.post('/auth/register', data)
}

// 用户登录
export const login = (username, password) => {
  return request.post('/auth/login', null, {
    params: { username, password }
  })
}

// 用户登出
export const logout = () => {
  return request.post('/auth/logout')
}

// 重置密码
export const resetPassword = (username, newPassword) => {
  return request.post('/auth/resetPassword', null, {
    params: { username, newPassword }
  })
}
