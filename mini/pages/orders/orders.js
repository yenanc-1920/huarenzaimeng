Page({
  data: {
    orders: [
      { id: 1, orderNo: 'HM20260723000001', phone: '01712345678', amount: '500', status: 'SUCCESS', statusText: '充值成功', time: '2026-07-23 18:30' },
      { id: 2, orderNo: 'HM20260723000002', phone: '01812345678', amount: '1000', status: 'CHARGING', statusText: '充值中', time: '2026-07-23 18:25' },
      { id: 3, orderNo: 'HM20260723000003', phone: '01912345678', amount: '200', status: 'PENDING_PAY', statusText: '待支付', time: '2026-07-23 18:20' }
    ]
  },
  getStatusColor(status) {
    const map = { SUCCESS: '#52C41A', CHARGING: '#1890FF', PENDING_PAY: '#FAAD14', FAILED: '#FF4D4F', REFUNDED: '#999' }
    return map[status] || '#999'
  }
})
