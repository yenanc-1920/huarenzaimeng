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
    var that = this
    wx.showLoading({ title: '登录中...' })
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: function(res) {
        wx.hideLoading()
        that.setData({ userInfo: res.userInfo })
        wx.showToast({ title: '登录成功', icon: 'success' })
      },
      fail: function() {
        wx.hideLoading()
        wx.showToast({ title: '登录取消', icon: 'none' })
      }
    })
  },
  goPage(e) {
    var id = e.currentTarget.dataset.id
    if (id === 'orders') {
      wx.navigateTo({ url: '/pages/orders/orders' })
    } else if (id === 'favorites') {
      wx.showToast({ title: '常用号码功能开发中', icon: 'none' })
    } else if (id === 'about') {
      wx.showToast({ title: '关于我们功能开发中', icon: 'none' })
    } else {
      wx.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
})