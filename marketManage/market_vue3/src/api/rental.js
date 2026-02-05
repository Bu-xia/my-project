import request from '@/utils/request'

// 获取当前租赁信息
export const getCurrentRental = (userId) => {
  return request.get('/user/rental/current', {
    params: { userId }
  })
}

// 获取历史租赁信息
export const getHistoryRental = (params) => {
  return request.get('/user/rental/history', { params })
}

// 获取租赁详情
export const getRentalDetail = (id) => {
  return request.get(`/user/rental/detail/${id}`)
}

// 申请续租
export const renewRental = (id) => {
  return request.post(`/user/rental/renew/${id}`)
}
