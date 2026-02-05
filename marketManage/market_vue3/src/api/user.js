import request from '@/utils/request'

// 获取用户信息
export const getUserInfo = (id) => {
  return request.get(`/user/info/${id}`)
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request.put('/user/update', data)
}

// 上传头像
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 修改密码
export const changePassword = (data) => {
  return request.put('/user/password', data)
}
