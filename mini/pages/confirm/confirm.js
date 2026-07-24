Page({
  data: {
    phone: '',
    productName: '',
    price: 0,
    cnyPrice: '0.00',
    operator: 'Grameenphone',
    mnpStatus: ''
  },
  onLoad(options) {
    this.setData({
      phone: options.phone || '',
      productName: options.name || '',
      price: Number(options.price) || 0,
      cnyPrice: (Number(options.price) * 0.062).toFixed(2)
    })
  },
  confirmPay() {
    if (!this.data.phone || this.data.phone.length < 10) {
      wx.showToast({ title: '请输入正确手机号', icon: 'none' })
      return
    }
    var that = this
    wx.showLoading({ title: '正在校验号码...' })
    that.setData({ mnpStatus: 'checking' })
    
    setTimeout(function() {
      wx.hideLoading()
      that.setData({ mnpStatus: 'success' })
      wx.showToast({ title: '校验通过', icon: 'success', duration: 1000 })
      
      setTimeout(function() {
        wx.showLoading({ title: '支付处理中...' })
        setTimeout(function() {
          wx.hideLoading()
          wx.showToast({ title: '支付成功', icon: 'success', duration: 1500 })
          setTimeout(function() {
            wx.redirectTo({
              url: '/pages/result/result?status=success&orderNo=HM' + Date.now()
            })
          }, 1500)
        }, 1000)
      }, 1000)
    }, 1500)
  }
})