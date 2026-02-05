import request from '@/utils/request'

// 提交申请
export const submitApply = (data) => {
  return request.post('/user/apply/submit', data)
}

// 获取我的申请列表
export const getMyApplyList = (params) => {
  return request.get('/user/apply/myList', { params })
}

// 获取申请详情
export const getApplyDetail = (id) => {
  return request.get(`/user/apply/detail/${id}`)
}

// 取消申请
export const cancelApply = (id) => {
  return request.delete(`/user/apply/cancel/${id}`)
}
