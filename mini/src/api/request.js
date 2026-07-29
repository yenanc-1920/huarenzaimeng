const CLOUD_ENV = 'prod-d3g9ntdmsdf9d7877'
const CLOUD_SERVICE = 'huarenzaimeng-server'
const HTTP_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

let cloudInitialized = false

export function initCloud() {
  // #ifdef MP-WEIXIN
  if (!cloudInitialized && wx.cloud) {
    wx.cloud.init({ env: CLOUD_ENV, traceUser: true })
    cloudInitialized = true
  }
  // #endif
}

function getHeaders(extraHeaders = {}) {
  const token = uni.getStorageSync('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extraHeaders
  }
}

function handleResponse(response, resolve, reject) {
  const statusCode = response.statusCode
  const data = response.data

  if (statusCode === 401) {
    uni.removeStorageSync('token')
    uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
    reject(new Error('UNAUTHORIZED'))
    return
  }

  if (statusCode >= 200 && statusCode < 300 && data && (data.code === 0 || data.code === 200)) {
    resolve(data)
    return
  }

  const message = data?.message || (statusCode ? `服务器错误(${statusCode})` : '请求失败')
  uni.showToast({ title: message, icon: 'none' })
  reject(new Error(message))
}

const request = (options) => new Promise((resolve, reject) => {
  const path = options.url.startsWith('/api/') ? options.url : `/api${options.url}`
  const method = options.method || 'GET'
  const data = options.data || {}
  const header = getHeaders(options.header)

  // #ifdef MP-WEIXIN
  initCloud()
  wx.cloud.callContainer({
    config: { env: CLOUD_ENV },
    path,
    method,
    data,
    header: {
      ...header,
      'X-WX-SERVICE': CLOUD_SERVICE
    },
    success: (response) => handleResponse(response, resolve, reject),
    fail: (error) => {
      uni.showToast({ title: '云托管服务连接失败', icon: 'none' })
      reject(error)
    }
  })
  // #endif

  // #ifndef MP-WEIXIN
  uni.request({
    url: HTTP_BASE_URL + path,
    method,
    data,
    header,
    success: (response) => handleResponse(response, resolve, reject),
    fail: (error) => {
      uni.showToast({ title: '网络连接失败', icon: 'none' })
      reject(error)
    }
  })
  // #endif
})

export const get = (url, data, options = {}) => request({ url, method: 'GET', data, ...options })
export const post = (url, data, options = {}) => request({ url, method: 'POST', data, ...options })
export const put = (url, data, options = {}) => request({ url, method: 'PUT', data, ...options })
export const del = (url, data, options = {}) => request({ url, method: 'DELETE', data, ...options })

export default request