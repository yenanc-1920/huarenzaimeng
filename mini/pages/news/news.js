Page({
  data: {
    newsList: [
      { id: 1, title: '孟加拉国独立日放假通知', summary: '3月26日为孟加拉国独立日，全国放假一天...', date: '2026-07-20', image: '' },
      { id: 2, title: '达卡华人商会活动预告', summary: '本周六将在达卡市中心举办华人商会活动...', date: '2026-07-18', image: '' },
      { id: 3, title: '孟加拉签证政策更新', summary: '最新签证政策调整，请相关人员注意...', date: '2026-07-15', image: '' }
    ]
  },
  goDetail(e) {
    var id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/news-detail/news-detail?id=' + id })
  }
})