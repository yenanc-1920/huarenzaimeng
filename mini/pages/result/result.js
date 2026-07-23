Page({
  data: {
    status: 'success', // success | pending | failed
    orderNo: ''
  },
  onLoad(options) {
    this.setData({
      status: options.status || 'pending',
      orderNo: options.orderNo || ''
    })
  },
  goHome() {
    wx.switchTab({ url: '/pages/index/index' })
  },
  goOrders() {
    wx.navigateTo({ url: '/pages/orders/orders' })
  }
})
