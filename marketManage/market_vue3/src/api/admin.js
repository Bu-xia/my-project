import request from '@/utils/request'

// 管理员登录
export const adminLogin = (username, password) => {
  return request.post('/admin/login', null, {
    params: { username, password }
  })
}

// ==================== 管理员个人信息 ====================

// 获取管理员信息
export const getAdminInfo = (id) => {
  return request.get(`/admin/info/${id}`)
}

// 更新管理员信息
export const updateAdminInfo = (data) => {
  return request.put('/admin/update', data)
}

// 修改管理员密码
export const changeAdminPassword = (data) => {
  return request.put('/admin/password', data)
}

// ==================== 租户管理 ====================

// 获取租户列表
export const getTenantList = (params) => {
  return request.get('/admin/tenant/list', { params })
}

// 获取租户详情
export const getTenantDetail = (id) => {
  return request.get(`/admin/tenant/detail/${id}`)
}

// 添加租户
export const addTenant = (data) => {
  return request.post('/admin/tenant/add', data)
}

// 更新租户信息
export const updateTenant = (data) => {
  return request.put('/admin/tenant/update', data)
}

// 删除租户
export const deleteTenant = (id) => {
  return request.delete(`/admin/tenant/delete/${id}`)
}

// ==================== 合同管理 ====================

// 获取合同列表
export const getContractList = (params) => {
  return request.get('/admin/contract/list', { params })
}

// 获取合同详情
export const getContractDetail = (id) => {
  return request.get(`/admin/contract/detail/${id}`)
}

// 更新合同状态
export const updateContractStatus = (data) => {
  return request.put('/admin/contract/updateStatus', data)
}

// 搜索合同
export const searchContract = (params) => {
  return request.get('/admin/contract/search', { params })
}

// ==================== 费用收支管理 ====================

// 获取收支列表
export const getFinanceList = (params) => {
  return request.get('/admin/finance/list', { params })
}

// 获取统计数据
export const getFinanceStatistics = (params) => {
  return request.get('/admin/finance/statistics', { params })
}


// ==================== 公告通知管理 ====================

// 获取公告列表
export const getNoticeList = (params) => {
  return request.get('/admin/notice/list', { params })
}

// 添加公告
export const addNotice = (data) => {
  return request.post('/admin/notice/add', data)
}

// 更新公告
export const updateNotice = (data) => {
  return request.put('/admin/notice/update', data)
}

// 删除公告
export const deleteNotice = (id) => {
  return request.delete(`/admin/notice/delete/${id}`)
}

// 置顶公告
export const topNotice = (id) => {
  return request.put(`/admin/notice/top/${id}`)
}

// ==================== 物资仓储管理 ====================

// 获取物资列表
export const getInventoryList = (params) => {
  return request.get('/admin/inventory/list', { params })
}

// 物资入库
export const inventoryInbound = (data) => {
  return request.post('/admin/inventory/inbound', data)
}

// 物资出库
export const inventoryOutbound = (data) => {
  return request.post('/admin/inventory/outbound', data)
}

// 获取库存预警
export const getInventoryWarning = () => {
  return request.get('/admin/inventory/warning')
}

// 获取指定物资的出入库记录
export const getInventoryRecords = (inventoryId) => {
  return request.get(`/admin/inventory/records/${inventoryId}`)
}

// ==================== 数据统计 ====================

// 获取概览数据
export const getOverview = () => {
  return request.get('/admin/statistics/overview')
}

// 获取收入趋势
export const getIncomeTrend = () => {
  return request.get('/admin/statistics/income')
}

// 获取摊位统计
export const getBoothStatistics = () => {
  return request.get('/admin/statistics/booth')
}

// 获取租户统计
export const getTenantStatistics = () => {
  return request.get('/admin/statistics/tenant')
}

// ==================== 申请管理 ====================

// 获取申请列表
export const getApplyList = (params) => {
  return request.get('/admin/apply/list', { params })
}

// 获取申请详情
export const getApplyDetail = (id) => {
  return request.get(`/admin/apply/detail/${id}`)
}

// 审核申请
export const auditApply = (data) => {
  return request.post('/admin/apply/audit', data)
}

// 获取申请统计信息
export const getApplyStatistics = () => {
  return request.get('/admin/apply/statistics')
}

// ==================== 摊位管理 ====================

// 获取摊位列表
export const getBoothList = (params) => {
  return request.get('/admin/booth/list', { params })
}

// 获取摊位详情
export const getBoothDetail = (id) => {
  return request.get(`/admin/booth/detail/${id}`)
}

// 添加摊位
export const addBooth = (data) => {
  return request.post('/admin/booth/add', data)
}

// 更新摊位信息
export const updateBooth = (data) => {
  return request.put('/admin/booth/update', data)
}

// 删除摊位
export const deleteBooth = (id) => {
  return request.delete(`/admin/booth/delete/${id}`)
}

