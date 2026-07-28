<template>
  <div class="order-management">
    <div class="filter-bar">
      <a-input v-model:value="filters.phone" placeholder="手机号" allow-clear style="width: 150px" @pressEnter="handleSearch" />
      <a-input v-model:value="filters.orderNo" placeholder="订单号" allow-clear style="width: 180px" @pressEnter="handleSearch" />
      <a-range-picker v-model:value="filters.dateRange" style="width: 240px" />
      <a-select v-model:value="filters.status" placeholder="全部状态" allow-clear style="width: 140px">
        <a-select-opt-group label="待处理">
          <a-select-option value="PENDING_PAY">待支付</a-select-option>
        </a-select-opt-group>
        <a-select-opt-group label="进行中">
          <a-select-option value="PAID">已支付</a-select-option>
          <a-select-option value="RECHARGING">充值中</a-select-option>
        </a-select-opt-group>
        <a-select-opt-group label="已完成">
          <a-select-option value="SUCCESS">充值成功</a-select-option>
          <a-select-option value="REFUNDED">已退款</a-select-option>
        </a-select-opt-group>
        <a-select-opt-group label="异常">
          <a-select-option value="FAILED">充值失败</a-select-option>
          <a-select-option value="PROCESSING_TIMEOUT">超时处理中</a-select-option>
        </a-select-opt-group>
      </a-select>
      <a-button type="primary" @click="handleSearch">搜索</a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredOrders"
      :pagination="{ pageSize: 15, showTotal: t => `共 ${t} 条` }"
      row-key="id"
      size="middle"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'amount'">
          <span class="hm-mono" style="color: #FF4D4F; font-weight: 600">¥{{ record.amount.toFixed(1) }}</span>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="statusMap[record.status]?.color">{{ statusMap[record.status]?.label }}</a-tag>
          <a-tag v-if="record.mnpDegraded" color="orange">未MNP校验</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-button type="link" size="small" @click="showDetail(record)">详情</a-button>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="detailVisible" title="订单详情" :width="600" :footer="null">
      <template v-if="currentOrder">
        <a-descriptions :column="2" size="small" bordered style="margin-bottom: 24px">
          <a-descriptions-item label="订单号">{{ currentOrder.orderNo }}</a-descriptions-item>
          <a-descriptions-item label="手机号">{{ currentOrder.phone }}</a-descriptions-item>
          <a-descriptions-item label="运营商">{{ currentOrder.operator }}</a-descriptions-item>
          <a-descriptions-item label="金额">¥{{ currentOrder.amount.toFixed(2) }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="statusMap[currentOrder.status]?.color">{{ statusMap[currentOrder.status]?.label }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ currentOrder.createTime }}</a-descriptions-item>
        </a-descriptions>

        <h4 style="margin-bottom: 16px">订单时间线</h4>
        <div class="timeline-container">
          <div v-for="(node, idx) in currentOrder.timeline" :key="idx" class="timeline-node">
            <div class="node-dot" :class="node.type">
              <span v-if="node.type === 'failed'">✕</span>
            </div>
            <div v-if="idx < currentOrder.timeline.length - 1" class="node-line"></div>
            <div class="node-content">
              <span class="node-title">{{ node.title }}</span>
              <span class="node-desc" :class="{ 'desc-failed': node.type === 'failed' }">{{ node.desc }}</span>
              <span class="node-time">{{ node.time }}</span>
            </div>
          </div>
        </div>
      </template>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

const filters = reactive({ phone: '', orderNo: '', dateRange: null, status: undefined })
const detailVisible = ref(false)
const currentOrder = ref(null)

const statusMap = {
  PENDING_PAY: { label: '待支付', color: 'default' },
  PAID: { label: '已支付', color: 'blue' },
  RECHARGING: { label: '充值中', color: 'processing' },
  SUCCESS: { label: '充值成功', color: 'green' },
  REFUNDED: { label: '已退款', color: 'purple' },
  FAILED: { label: '充值失败', color: 'red' },
  PROCESSING_TIMEOUT: { label: '超时处理中', color: 'orange' }
}

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 160 },
  { title: '手机号', dataIndex: 'phone', key: 'phone', width: 130 },
  { title: '运营商', dataIndex: 'operator', key: 'operator', width: 120 },
  { title: '金额', key: 'amount', width: 100 },
  { title: '状态', key: 'status', width: 180 },
  { title: '时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 80 }
]

const mockOrders = ref([
  { id: 1, orderNo: 'HM20260727001', phone: '01712345678', operator: 'Grameenphone', amount: 42.5, status: 'SUCCESS', mnpDegraded: false, createTime: '2026-07-27 14:30',
    timeline: [
      { title: '充值成功', desc: 'Reloadly返回SUCCESS', time: '2026-07-27 14:32', type: 'success' },
      { title: '调用Reloadly充值', desc: '交易ID: RLD-88901', time: '2026-07-27 14:31', type: 'normal' },
      { title: '微信支付成功', desc: '支付金额 ¥42.50', time: '2026-07-27 14:30', type: 'success' },
      { title: 'MNP校验通过', desc: '号码归属 Grameenphone', time: '2026-07-27 14:30', type: 'success' },
      { title: '用户提交订单', desc: '商品: GP 500 BDT Airtime', time: '2026-07-27 14:29', type: 'normal' }
    ]
  },
  { id: 2, orderNo: 'HM20260727002', phone: '01812345678', operator: 'Robi', amount: 25.8, status: 'RECHARGING', mnpDegraded: false, createTime: '2026-07-27 14:25',
    timeline: [
      { title: '充值中', desc: '等待Reloadly处理', time: '2026-07-27 14:26', type: 'normal' },
      { title: '调用Reloadly充值', desc: '交易ID: RLD-88902', time: '2026-07-27 14:26', type: 'normal' },
      { title: '微信支付成功', desc: '支付金额 ¥25.80', time: '2026-07-27 14:25', type: 'success' },
      { title: 'MNP校验通过', desc: '号码归属 Robi', time: '2026-07-27 14:25', type: 'success' },
      { title: '用户提交订单', desc: '商品: Robi 5GB Bundle', time: '2026-07-27 14:24', type: 'normal' }
    ]
  },
  { id: 3, orderNo: 'HM20260727003', phone: '01912345678', operator: 'Banglalink', amount: 8.5, status: 'FAILED', mnpDegraded: false, createTime: '2026-07-27 13:50',
    timeline: [
      { title: '充值失败', desc: 'Reloadly返回FAILED: 运营商拒绝', time: '2026-07-27 13:52', type: 'failed' },
      { title: '调用Reloadly充值', desc: '交易ID: RLD-88890', time: '2026-07-27 13:51', type: 'normal' },
      { title: '微信支付成功', desc: '支付金额 ¥8.50', time: '2026-07-27 13:50', type: 'success' },
      { title: 'MNP校验通过', desc: '号码归属 Banglalink', time: '2026-07-27 13:50', type: 'success' },
      { title: '用户提交订单', desc: '商品: BL 100 BDT Airtime', time: '2026-07-27 13:49', type: 'normal' }
    ]
  },
  { id: 4, orderNo: 'HM20260727004', phone: '01612345678', operator: 'Airtel', amount: 17.0, status: 'SUCCESS', mnpDegraded: true, createTime: '2026-07-27 12:10',
    timeline: [
      { title: '充值成功', desc: 'Reloadly返回SUCCESS', time: '2026-07-27 12:12', type: 'success' },
      { title: '调用Reloadly充值', desc: '交易ID: RLD-88870', time: '2026-07-27 12:11', type: 'normal' },
      { title: '微信支付成功', desc: '支付金额 ¥17.00', time: '2026-07-27 12:10', type: 'success' },
      { title: 'MNP校验降级', desc: 'MNP接口超时，降级放行', time: '2026-07-27 12:10', type: 'failed' },
      { title: '用户提交订单', desc: '商品: Airtel 200 BDT Airtime', time: '2026-07-27 12:09', type: 'normal' }
    ]
  }
])

const filteredOrders = computed(() => {
  return mockOrders.value.filter(o => {
    if (filters.phone && !o.phone.includes(filters.phone)) return false
    if (filters.orderNo && !o.orderNo.includes(filters.orderNo)) return false
    if (filters.status && o.status !== filters.status) return false
    return true
  })
})

const handleSearch = () => {}

const showDetail = (record) => {
  currentOrder.value = record
  detailVisible.value = true
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.timeline-container {
  padding-left: 4px;
}
.timeline-node {
  display: flex;
  position: relative;
  padding-bottom: 24px;
}
.timeline-node:last-child {
  padding-bottom: 0;
}
.node-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.node-dot.success { background: #52C41A; }
.node-dot.normal { background: #1890FF; }
.node-dot.failed {
  background: #FF4D4F;
  border-radius: 2px;
  width: 10px;
  height: 10px;
}
.node-dot.failed span {
  color: #fff;
  font-size: 7px;
  line-height: 1;
}
.node-line {
  position: absolute;
  left: 3.5px;
  top: 15px;
  bottom: 0;
  width: 1px;
  background: #E8E8E8;
}
.node-content {
  margin-left: 16px;
  display: flex;
  flex-direction: column;
}
.node-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}
.node-desc {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}
.desc-failed {
  color: #FF4D4F;
}
.node-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
</style>
