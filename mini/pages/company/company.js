Page({
  data: {
    companyList: [
      { id: 1, name: '华为孟加拉办事处', industry: '通信', phone: '+880-17-12345678', address: '达卡Gulshan-2' },
      { id: 2, name: '中孟贸易有限公司', industry: '贸易', phone: '+880-18-87654321', address: '达卡Banani' },
      { id: 3, name: '长城餐厅', industry: '餐饮', phone: '+880-19-11111111', address: '达卡Dhanmondi' }
    ]
  },
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/company-detail/company-detail?id=' + id })
  }
})
