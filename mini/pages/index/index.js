Page({
  data: {
    phone: '',
    selectedOperator: null,
    operators: [
      { id: 'gp', name: 'Grameenphone', prefix: ['017', '013'] },
      { id: 'robi', name: 'Robi', prefix: ['018', '019'] },
      { id: 'bl', name: 'Banglalink', prefix: ['014', '015'] },
      { id: 'teletalk', name: 'Teletalk', prefix: ['016'] }
    ],
    products: [
      { id: 1, name: '10 Taka', price: 10, operator: 'Grameenphone' },
      { id: 2, name: '20 Taka', price: 20, operator: 'Grameenphone' },
      { id: 3, name: '50 Taka', price: 50, operator: 'Grameenphone' },
      { id: 4, name: '100 Taka', price: 100, operator: 'Grameenphone' },
      { id: 5, name: '200 Taka', price: 200, operator: 'Grameenphone' },
      { id: 6, name: '500 Taka', price: 500, operator: 'Grameenphone' }
    ],
    favorites: [
      { phone: '01712345678', operator: 'Grameenphone' },
      { phone: '01812345678', operator: 'Robi' }
    ]
  },
  onPhoneInput(e) {
    var phone = e.detail.value
    var operator = this.detectOperator(phone)
    this.setData({ phone: phone, selectedOperator: operator })
    
    // Show warning if phone is long enough but no operator matched
    if (phone.length >= 3 && !operator) {
      wx.showToast({ title: '未识别运营商，请手动选择', icon: 'none', duration: 2000 })
    }
  },
  detectOperator(phone) {
    if (phone.length < 3) return null
    var prefix = phone.substring(0, 3)
    var operators = this.data.operators
    for (var i = 0; i < operators.length; i++) {
      var op = operators[i]
      for (var j = 0; j < op.prefix.length; j++) {
        if (op.prefix[j] === prefix) return op.id
      }
    }
    return null
  },
  selectOperator(e) {
    var id = e.currentTarget.dataset.id
    this.setData({ selectedOperator: id })
    var opName = ''
    for (var i = 0; i < this.data.operators.length; i++) {
      if (this.data.operators[i].id === id) {
        opName = this.data.operators[i].name
        break
      }
    }
    wx.showToast({ title: '已选择' + opName, icon: 'none', duration: 1000 })
  },
  selectProduct(e) {
    var product = e.currentTarget.dataset.product
    var phone = this.data.phone
    if (!phone || phone.length < 10) {
      wx.showToast({ title: '请输入正确手机号', icon: 'none' })
      return
    }
    if (!this.data.selectedOperator) {
      wx.showToast({ title: '请选择运营商', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: '/pages/confirm/confirm?phone=' + phone + '&productId=' + product.id + '&price=' + product.price + '&name=' + product.name + '&operator=' + this.data.selectedOperator
    })
  },
  useFavorite(e) {
    var phone = e.currentTarget.dataset.phone
    var operator = this.detectOperator(phone)
    this.setData({ phone: phone, selectedOperator: operator })
    if (operator) {
      var opName = ''
      for (var i = 0; i < this.data.operators.length; i++) {
        if (this.data.operators[i].id === operator) {
          opName = this.data.operators[i].name
          break
        }
      }
      wx.showToast({ title: '已选择' + opName, icon: 'none', duration: 1000 })
    } else {
      wx.showToast({ title: '未识别运营商，请手动选择', icon: 'none', duration: 2000 })
    }
  }
})