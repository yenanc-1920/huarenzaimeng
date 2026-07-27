const BASE_URL = ''

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.header
      },
      success: (res) => {
        const { statusCode, data } = res
        if (statusCode === 401) {
          uni.removeStorageSync('token')
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/my/my' })
          }, 1500)
          reject(new Error('UNAUTHORIZED'))
          return
        }
        if (statusCode >= 200 && statusCode < 300) {
          if (data.code === 0 || data.code === 200) {
            resolve(data)
          } else {
            uni.showToast({ title: data.message || '请求失败', icon: 'none' })
            reject(new Error(data.message || 'BIZ_ERROR'))
          }
        } else {
          uni.showToast({ title: `服务器错误(${statusCode})`, icon: 'none' })
          reject(new Error(`HTTP_${statusCode}`))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url, data, options = {}) => request({ url, method: 'GET', data, ...options })
export const post = (url, data, options = {}) => request({ url, method: 'POST', data, ...options })
export const put = (url, data, options = {}) => request({ url, method: 'PUT', data, ...options })
export const del = (url, data, options = {}) => request({ url, method: 'DELETE', data, ...options })

export default request
