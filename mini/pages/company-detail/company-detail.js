Page({
  data: {
    company: {
      id: 1,
      name: '华为孟加拉办事处',
      industry: '通信',
      phone: '+880-17-12345678',
      address: '达卡Gulshan-2',
      description: '华为技术有限公司在孟加拉国的办事处，提供通信设备和解决方案。',
      workingHours: '周日-周四 9:00-18:00'
    }
  },
  callPhone() {
    wx.makePhoneCall({ phoneNumber: this.data.company.phone })
  }
})
