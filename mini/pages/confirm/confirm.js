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
    that.setData({ mnpStatus: 'checking' })
    setTimeout(function() {
      that.setData({ mnpStatus: 'success' })
      setTimeout(function() {
        wx.redirectTo({
          url: '/pages/result/result?status=success&orderNo=HM20260723000001'
        })
      }, 500)
    }, 1500)
  }
})