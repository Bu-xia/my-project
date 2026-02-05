import request from '@/utils/request'

// 获取待支付账单
export const getUnpaidPayments = (params) => {
  return request.get('/user/payment/unpaid', { params })
}

// 获取已支付账单
export const getPaidPayments = (params) => {
  return request.get('/user/payment/paid', { params })
}

// 支付账单
export const payBill = (id, paymentMethod) => {
  return request.post('/user/payment/pay', null, {
    params: { id, paymentMethod }
  })
}

// 获取账单详情
export const getPaymentDetail = (id) => {
  return request.get(`/user/payment/detail/${id}`)
}
