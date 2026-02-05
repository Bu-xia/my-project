import request from '@/utils/request'

// 获取摊位列表
export const getBoothList = (params) => {
  return request.get('/user/booth/list', { params })
}

// 获取摊位详情
export const getBoothDetail = (id) => {
  return request.get(`/user/booth/detail/${id}`)
}

// 搜索摊位
export const searchBooth = (params) => {
  return request.get('/user/booth/search', { params })
}

// 获取可租摊位
export const getAvailableBooths = () => {
  return request.get('/user/booth/available')
}
