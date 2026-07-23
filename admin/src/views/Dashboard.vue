<template>
  <div>
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card>
          <a-statistic title="今日订单数" :value="156" style="margin-right: 50px">
            <template #prefix><OrderedListOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="今日充值金额(BDT)" :value="45280" style="margin-right: 50px" :precision="2">
            <template #prefix><DollarOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="Reloadly余额(USD)" :value="1280.50" style="margin-right: 50px" :precision="2">
            <template #prefix><WalletOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="今日退款数" :value="3" style="margin-right: 50px">
            <template #prefix><RollbackOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="最近订单" style="margin-top: 24px">
      <a-table :columns="columns" :data-source="mockData" :pagination="false" size="small">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.statusColor">{{ record.status }}</a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { OrderedListOutlined, DollarOutlined, WalletOutlined, RollbackOutlined } from '@ant-design/icons-vue'

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '金额(BDT)', dataIndex: 'amount', key: 'amount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime' }
]

const mockData = [
  { key: '1', orderNo: 'HM20260723001', phone: '01712345678', amount: '500.00', status: '充值成功', statusColor: 'green', createTime: '2026-07-23 18:30' },
  { key: '2', orderNo: 'HM20260723002', phone: '01812345678', amount: '1000.00', status: '充值中', statusColor: 'blue', createTime: '2026-07-23 18:25' },
  { key: '3', orderNo: 'HM20260723003', phone: '01912345678', amount: '200.00', status: '待支付', statusColor: 'orange', createTime: '2026-07-23 18:20' },
  { key: '4', orderNo: 'HM20260723004', phone: '01612345678', amount: '300.00', status: '已退款', statusColor: 'red', createTime: '2026-07-23 18:15' }
]
</script>
