<template>
  <div class="product-management">
    <div class="page-toolbar">
      <div class="toolbar-left">
        <a-select v-model:value="filterOperator" placeholder="全部运营商" allow-clear style="width: 160px" @change="handleFilter">
          <a-select-option v-for="op in operators" :key="op" :value="op">{{ op }}</a-select-option>
        </a-select>
        <a-select v-model:value="filterType" placeholder="全部类型" allow-clear style="width: 120px" @change="handleFilter">
          <a-select-option value="AIRTIME">话费</a-select-option>
          <a-select-option value="BUNDLE">流量</a-select-option>
        </a-select>
        <a-select v-model:value="filterStatus" placeholder="全部状态" allow-clear style="width: 140px" @change="handleFilter">
          <a-select-option value="ACTIVE">启用</a-select-option>
          <a-select-option value="DISABLED">停用</a-select-option>
          <a-select-option value="FX_EXPIRED">汇率过期</a-select-option>
        </a-select>
      </div>
      <div class="toolbar-right">
        <span class="sync-time">最后同步：{{ lastSyncTime }}</span>
        <a-button type="primary" :loading="syncing" @click="handleSync">
          {{ syncing ? '同步中...' : '同步商品' }}
        </a-button>
      </div>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredData"
      :pagination="{ pageSize: 15, showTotal: t => `共 ${t} 条` }"
      row-key="id"
      size="middle"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">
          <a-tag :color="record.type === 'AIRTIME' ? 'blue' : 'purple'">
            {{ record.type === 'AIRTIME' ? '话费' : '流量' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'price'">
          <span class="hm-mono">¥{{ record.price.toFixed(2) }}</span>
          <a-tag v-if="record.fxExpired && record.priceMode === 'AUTO'" color="orange" style="margin-left: 4px">+2%溢价</a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag v-if="record.status === 'ACTIVE'" color="green">启用</a-tag>
          <a-tag v-else-if="record.status === 'DISABLED'" color="default">停用</a-tag>
          <template v-if="record.fxExpired">
            <a-tag color="orange">汇率过期</a-tag>
            <a-tag v-if="record.priceMode === 'FIXED'" color="orange">固定价格未调整</a-tag>
          </template>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="toggleStatus(record)">
              {{ record.status === 'ACTIVE' ? '停用' : '启用' }}
            </a-button>
            <a-button type="link" size="small" @click="showHistory(record)">历史</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-drawer v-model:open="historyVisible" title="商品历史" :width="480" placement="right">
      <a-timeline v-if="historyData.length">
        <a-timeline-item v-for="h in historyData" :key="h.time" :color="h.color">
          <p style="margin: 0; font-size: 13px; color: #333">{{ h.action }}</p>
          <p style="margin: 2px 0 0; font-size: 12px; color: #999">{{ h.time }}</p>
        </a-timeline-item>
      </a-timeline>
      <a-empty v-else description="暂无历史记录" />
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'

const operators = ['Grameenphone', 'Robi', 'Banglalink', 'Teletalk', 'Airtel']
const filterOperator = ref(undefined)
const filterType = ref(undefined)
const filterStatus = ref(undefined)
const syncing = ref(false)
const lastSyncTime = ref('2026-07-27 10:15')
const historyVisible = ref(false)
const historyData = ref([])

const columns = [
  { title: '商品名称', dataIndex: 'name', key: 'name', ellipsis: true },
  { title: '运营商', dataIndex: 'operator', key: 'operator', width: 130 },
  { title: '类型', key: 'type', width: 80 },
  { title: '售价(CNY)', key: 'price', width: 140 },
  { title: '状态', key: 'status', width: 200 },
  { title: '操作', key: 'action', width: 120 }
]

const mockProducts = ref([
  { id: 1, name: 'GP 50 BDT Airtime', operator: 'Grameenphone', type: 'AIRTIME', price: 4.25, status: 'ACTIVE', fxExpired: false, priceMode: 'AUTO' },
  { id: 2, name: 'GP 100 BDT Airtime', operator: 'Grameenphone', type: 'AIRTIME', price: 8.50, status: 'ACTIVE', fxExpired: true, priceMode: 'AUTO' },
  { id: 3, name: 'GP 5GB Bundle 7Days', operator: 'Grameenphone', type: 'BUNDLE', price: 25.80, status: 'ACTIVE', fxExpired: false, priceMode: 'FIXED' },
  { id: 4, name: 'Robi 50 BDT Airtime', operator: 'Robi', type: 'AIRTIME', price: 4.25, status: 'ACTIVE', fxExpired: false, priceMode: 'AUTO' },
  { id: 5, name: 'Robi 10GB Bundle 30Days', operator: 'Robi', type: 'BUNDLE', price: 42.00, status: 'DISABLED', fxExpired: true, priceMode: 'FIXED' },
  { id: 6, name: 'BL 100 BDT Airtime', operator: 'Banglalink', type: 'AIRTIME', price: 8.50, status: 'ACTIVE', fxExpired: false, priceMode: 'AUTO' },
  { id: 7, name: 'Teletalk 50 BDT Airtime', operator: 'Teletalk', type: 'AIRTIME', price: 4.25, status: 'ACTIVE', fxExpired: false, priceMode: 'AUTO' },
  { id: 8, name: 'Airtel 200 BDT Airtime', operator: 'Airtel', type: 'AIRTIME', price: 17.00, status: 'ACTIVE', fxExpired: true, priceMode: 'AUTO' }
])

const filteredData = computed(() => {
  return mockProducts.value.filter(p => {
    if (filterOperator.value && p.operator !== filterOperator.value) return false
    if (filterType.value && p.type !== filterType.value) return false
    if (filterStatus.value) {
      if (filterStatus.value === 'FX_EXPIRED' && !p.fxExpired) return false
      if (filterStatus.value !== 'FX_EXPIRED' && p.status !== filterStatus.value) return false
    }
    return true
  })
})

const handleFilter = () => {}

const handleSync = () => {
  Modal.confirm({
    title: '手动同步确认',
    content: '手动同步将从Reloadly全量拉取最新运营商和商品数据，耗时约10-30秒，是否继续？',
    okText: '继续同步',
    cancelText: '取消',
    onOk: () => {
      syncing.value = true
      setTimeout(() => {
        syncing.value = false
        lastSyncTime.value = new Date().toLocaleString('zh-CN')
        message.success('同步成功，新增2个商品，更新3个商品')
      }, 2000)
    }
  })
}

const toggleStatus = (record) => {
  record.status = record.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  message.success(`已${record.status === 'ACTIVE' ? '启用' : '停用'}：${record.name}`)
}

const showHistory = (record) => {
  historyData.value = [
    { action: `售价调整为 ¥${record.price.toFixed(2)}`, time: '2026-07-26 14:00', color: 'blue' },
    { action: '汇率同步更新', time: '2026-07-26 10:00', color: 'green' },
    { action: '商品从Reloadly同步创建', time: '2026-07-20 09:30', color: 'gray' }
  ]
  historyVisible.value = true
}
</script>

<style scoped>
.page-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.toolbar-left {
  display: flex;
  gap: 12px;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.sync-time {
  font-size: 12px;
  color: #999;
}
</style>
