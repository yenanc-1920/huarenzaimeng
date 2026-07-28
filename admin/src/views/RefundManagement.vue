<template>
  <div class="refund-management">
    <div class="filter-bar">
      <a-select v-model:value="filterView" style="width: 160px" @change="handleViewChange">
        <a-select-option value="all">全部退款</a-select-option>
        <a-select-option value="abnormal">退款异常</a-select-option>
        <a-select-option value="preFunded">垫资坏账</a-select-option>
      </a-select>
      <a-input v-model:value="searchOrderNo" placeholder="原订单号" allow-clear style="width: 180px" />
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredRefunds"
      :pagination="{ pageSize: 15, showTotal: t => `共 ${t} 条` }"
      row-key="id"
      size="middle"
      :row-class-name="rowClassName"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'amount'">
          <span class="hm-mono" style="font-weight: 600">¥{{ record.amount.toFixed(2) }}</span>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag v-if="record.status === 'SUCCESS'" color="green">退款成功</a-tag>
          <a-tag v-else-if="record.status === 'PROCESSING'" color="blue">退款中</a-tag>
          <a-tag v-else-if="record.status === 'FAILED'" color="red">退款异常</a-tag>
          <a-tag v-if="record.type === 'PRE_FUNDED_LOSS'" color="red">垫资坏账</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <template v-if="record.reloadlyStatus === 'FAILED'">
              <a-button type="primary" size="small" @click="confirmRefund(record)">确认退款</a-button>
            </template>
            <template v-if="record.reloadlyStatus === 'PROCESSING'">
              <a-button size="small" @click="refreshStatus(record)">刷新状态</a-button>
              <a-button size="small" @click="markObserve(record)">标记待观察</a-button>
              <a-button
                v-if="record.overTwoHours"
                size="small"
                style="color: #FA8C16; border-color: #FA8C16"
                @click="showAdvanceRefund(record)"
              >申请垫资退款</a-button>
            </template>
            <template v-if="record.reloadlyStatus === 'SUCCESSFUL'">
              <span style="color: #52C41A; font-size: 12px">充值已成功，不可退款</span>
            </template>
            <template v-if="record.reloadlyStatus === 'REFUNDED'">
              <span style="color: #999; font-size: 12px">已自动退款</span>
            </template>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="advanceModalVisible"
      title="先行垫资退款确认"
      ok-text="确认垫资退款"
      cancel-text="取消"
      :ok-button-props="{ style: { background: '#FA8C16', borderColor: '#FA8C16' } }"
      @ok="executeAdvanceRefund"
    >
      <p>先行垫资退款将平台先垫付退款给用户。若后续Reloadly返回成功，平台承担实际成本。确认操作？</p>
      <a-descriptions v-if="advanceTarget" :column="1" size="small" style="margin-top: 12px">
        <a-descriptions-item label="退款单号">{{ advanceTarget.refundNo }}</a-descriptions-item>
        <a-descriptions-item label="退款金额">¥{{ advanceTarget.amount.toFixed(2) }}</a-descriptions-item>
        <a-descriptions-item label="原订单号">{{ advanceTarget.orderNo }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'

const filterView = ref('all')
const searchOrderNo = ref('')
const advanceModalVisible = ref(false)
const advanceTarget = ref(null)

const columns = [
  { title: '退款单号', dataIndex: 'refundNo', key: 'refundNo', width: 160 },
  { title: '原订单号', dataIndex: 'orderNo', key: 'orderNo', width: 160 },
  { title: '退款金额', key: 'amount', width: 110 },
  { title: '退款状态', key: 'status', width: 180 },
  { title: '退款时间', dataIndex: 'refundTime', key: 'refundTime', width: 160 },
  { title: '触发原因', dataIndex: 'reason', key: 'reason', ellipsis: true },
  { title: '操作', key: 'action', width: 240 }
]

const mockRefunds = ref([
  { id: 1, refundNo: 'RF20260727001', orderNo: 'HM20260727003', amount: 8.50, status: 'FAILED', type: 'NORMAL', reloadlyStatus: 'FAILED', refundTime: '2026-07-27 14:00', reason: '充值失败自动退款', overTwoHours: false },
  { id: 2, refundNo: 'RF20260727002', orderNo: 'HM20260726015', amount: 42.50, status: 'PROCESSING', type: 'NORMAL', reloadlyStatus: 'PROCESSING', refundTime: '2026-07-27 10:30', reason: '充值超时人工退款', overTwoHours: true },
  { id: 3, refundNo: 'RF20260726003', orderNo: 'HM20260726008', amount: 25.80, status: 'SUCCESS', type: 'PRE_FUNDED_LOSS', reloadlyStatus: 'REFUNDED', refundTime: '2026-07-26 16:20', reason: '垫资退款-Reloadly后续成功', overTwoHours: false },
  { id: 4, refundNo: 'RF20260726004', orderNo: 'HM20260726012', amount: 17.00, status: 'SUCCESS', type: 'NORMAL', reloadlyStatus: 'SUCCESSFUL', refundTime: '2026-07-26 11:00', reason: '充值失败自动退款', overTwoHours: false }
])

const filteredRefunds = computed(() => {
  return mockRefunds.value.filter(r => {
    if (filterView.value === 'abnormal' && r.status !== 'FAILED') return false
    if (filterView.value === 'preFunded' && r.type !== 'PRE_FUNDED_LOSS') return false
    if (searchOrderNo.value && !r.orderNo.includes(searchOrderNo.value)) return false
    return true
  })
})

const rowClassName = (record) => {
  if (record.status === 'FAILED') return 'row-abnormal'
  if (record.type === 'PRE_FUNDED_LOSS') return 'row-pre-funded'
  return ''
}

const handleViewChange = () => {}

const confirmRefund = (record) => {
  record.status = 'SUCCESS'
  message.success('退款已发起')
}

const refreshStatus = (record) => {
  message.info('已刷新，Reloadly状态：PROCESSING')
}

const markObserve = (record) => {
  message.success('已标记为待观察')
}

const showAdvanceRefund = (record) => {
  advanceTarget.value = record
  advanceModalVisible.value = true
}

const executeAdvanceRefund = () => {
  if (advanceTarget.value) {
    advanceTarget.value.status = 'SUCCESS'
    advanceTarget.value.type = 'PRE_FUNDED_LOSS'
    message.success('垫资退款已发起')
  }
  advanceModalVisible.value = false
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
:deep(.row-abnormal) {
  background: #FFF2F0 !important;
}
:deep(.row-pre-funded) {
  background: #FFF7E6 !important;
}
:deep(.row-abnormal:hover > td),
:deep(.row-pre-funded:hover > td) {
  background: inherit !important;
}
</style>
