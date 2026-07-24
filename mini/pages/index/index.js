Page({
  data: {
    phone: '',
    selectedOperator: null,
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
    this.setData({ phone: e.detail.value })
  },
  selectProduct(e) {
    var product = e.currentTarget.dataset.product
    var phone = this.data.phone
    wx.navigateTo({
      url: '/pages/confirm/confirm?phone=' + phone + '&productId=' + product.id + '&price=' + product.price + '&name=' + product.name
    })
  },
  useFavorite(e) {
    var phone = e.currentTarget.dataset.phone
    this.setData({ phone: phone })
  }
})