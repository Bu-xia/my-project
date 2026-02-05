import request from '@/utils/request'

// 获取公告列表
export const getNoticeList = (params) => {
  return request.get('/user/notice/list', { params })
}

// 获取公告详情
export const getNoticeDetail = (id) => {
  return request.get(`/user/notice/detail/${id}`)
}

// 获取置顶公告
export const getTopNotices = () => {
  return request.get('/user/notice/top')
}
