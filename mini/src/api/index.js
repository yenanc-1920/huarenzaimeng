import { get, post } from './request'

export const userApi = {
  login: (code) => post('/api/user/login', { code }),
  getProfile: () => get('/api/user/me'),
  updateProfile: (data) => post('/api/user/profile', data)
}

export const rechargeApi = {
  getOperators: () => get('/api/operators'),
  getProducts: (operatorId, topupType) => get('/api/products', { operatorId, topupType }),
  validatePhone: (phone) => get('/api/recharge/validate', { phone }),
  createOrder: (data) => post('/api/recharge/order', data),
  getOrderStatus: (orderNo) => get(`/api/recharge/order/${orderNo}`)
}

export const contentApi = {
  getNewsList: (params) => get('/api/news', params),
  getNewsDetail: (id) => get(`/api/news/${id}`),
  getCompanies: (params) => get('/api/companies', params),
  getCompanyDetail: (id) => get(`/api/companies/${id}`)
}

export const orderApi = {
  getMyOrders: (params) => get('/api/orders/mine', params)
}

export const holidayApi = {
  getHolidays: () => get('/api/holidays')
}
