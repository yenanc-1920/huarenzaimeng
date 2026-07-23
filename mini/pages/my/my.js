Page({
  data: {
    userInfo: null,
    menuList: [
      { id: 'orders', name: '我的订单', icon: 'order' },
      { id: 'favorites', name: '常用号码', icon: 'phone' },
      { id: 'about', name: '关于我们', icon: 'info' }
    ]
  },
  getUserProfile() {
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        this.setData({ userInfo: res.userInfo })
      }
    })
  },
  goPage(e) {
    const id = e.currentTarget.dataset.id
    if (id === 'orders') {
      wx.navigateTo({ url: '/pages/orders/orders' })
    } else {
      wx.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
})
