Page({
  data: {
    phone: '',
    productName: '',
    price: 0,
    cnyPrice: '0.00',
    operator: 'Grameenphone',
    mnpStatus: '' // '' | 'checking' | 'success' | 'failed'
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
    // Mock: 模拟MNP校验
    this.setData({ mnpStatus: 'checking' })
    setTimeout(() => {
      this.setData({ mnpStatus: 'success' })
      setTimeout(() => {
        // 模拟支付成功，跳转结果页
        wx.redirectTo({
          url: '/pages/result/result?status=success&orderNo=HM20260723000001'
        })
      }, 500)
    }, 1500)
  }
})
