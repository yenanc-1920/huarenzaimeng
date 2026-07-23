<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; gap: 16px;">
      <a-select placeholder="订单状态" style="width: 150px" allowClear>
        <a-select-option value="PENDING_PAY">待支付</a-select-option>
        <a-select-option value="PENDING_CHARGE">待充值</a-select-option>
        <a-select-option value="CHARGING">充值中</a-select-option>
        <a-select-option value="SUCCESS">充值成功</a-select-option>
        <a-select-option value="FAILED">充值失败</a-select-option>
        <a-select-option value="REFUNDED">已退款</a-select-option>
      </a-select>
      <a-input-search placeholder="搜索订单号/手机号" style="width: 300px" />
      <a-range-picker />
    </div>
    <a-table :columns="columns" :data-source="mockData" :pagination="{ pageSize: 10 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColorMap[record.status]">{{ record.statusText }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-button type="link" size="small">详情</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
const statusColorMap = {
  PENDING_PAY: 'orange',
  PENDING_CHARGE: 'blue',
  CHARGING: 'processing',
  SUCCESS: 'green',
  FAILED: 'red',
  REFUNDED: 'default'
}
const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 180 },
  { title: '手机号', dataIndex: 'phone', key: 'phone', width: 130 },
  { title: '运营商', dataIndex: 'operator', key: 'operator', width: 100 },
  { title: '金额(BDT)', dataIndex: 'amount', key: 'amount', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 80 }
]
const mockData = [
  { key: '1', orderNo: 'HM20260723000001', phone: '01712345678', operator: 'Grameenphone', amount: '500.00', status: 'SUCCESS', statusText: '充值成功', createTime: '2026-07-23 18:30' },
  { key: '2', orderNo: 'HM20260723000002', phone: '01812345678', operator: 'Robi', amount: '1000.00', status: 'CHARGING', statusText: '充值中', createTime: '2026-07-23 18:25' },
  { key: '3', orderNo: 'HM20260723000003', phone: '01912345678', operator: 'Banglalink', amount: '200.00', status: 'PENDING_PAY', statusText: '待支付', createTime: '2026-07-23 18:20' },
  { key: '4', orderNo: 'HM20260723000004', phone: '01612345678', operator: 'Teletalk', amount: '300.00', status: 'REFUNDED', statusText: '已退款', createTime: '2026-07-23 18:15' },
  { key: '5', orderNo: 'HM20260723000005', phone: '01512345678', operator: 'Grameenphone', amount: '800.00', status: 'FAILED', statusText: '充值失败', createTime: '2026-07-23 18:10' }
]
</script>
