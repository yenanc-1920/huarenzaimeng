import { get, post, put } from './request'

export const userApi = {
  login: (code) => post('/wx/user/login', code ? { code } : {}),
  getProfile: () => get('/wx/user/me'),
  updateProfile: (data) => put('/wx/user/me', data)
}

export const rechargeApi = {
  getOperators: () => get('/wx/operators'),
  getProducts: (operatorId, topupType) => get('/wx/products', { operatorId, topupType }),
  createOrder: (data) => post('/wx/order/create', data),
  getOrderStatus: (orderNo) => get(`/wx/order/${orderNo}`)
}

export const contentApi = {
  getNewsList: (params) => get('/wx/news', params),
  getNewsDetail: (id) => get(`/wx/news/${id}`),
  getCompanies: (params) => get('/wx/companies', params),
  getCompanyDetail: (id) => get(`/wx/companies/${id}`)
}

export const orderApi = {
  getMyOrders: (params) => get('/wx/orders', params)
}

export const holidayApi = {
  getHolidays: (params) => get('/wx/holidays/upcoming', params)
}