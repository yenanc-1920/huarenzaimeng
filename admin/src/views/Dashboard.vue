<template>
  <div class="dashboard">
    <a-alert
      v-if="circuitBreaker.active"
      :message="circuitBreaker.message"
      type="warning"
      show-icon
      banner
      style="margin-bottom: 24px"
    />

    <a-row :gutter="[16, 16]">
      <a-col :span="6" v-for="item in userMetrics" :key="item.label">
        <a-card size="small">
          <a-statistic :title="item.label" :value="item.value" :value-style="{ fontSize: '24px', fontWeight: 600 }">
            <template #prefix><component :is="item.icon" /></template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <a-col :span="6" v-for="item in tradeMetrics" :key="item.label">
        <a-card size="small">
          <a-statistic
            :title="item.label"
            :value="item.value"
            :precision="item.precision"
            :value-style="{ fontSize: '24px', fontWeight: 600, color: item.color }"
          >
            <template #prefix><component :is="item.icon" /></template>
            <template #suffix v-if="item.suffix">{{ item.suffix }}</template>
          </a-statistic>
          <a-badge v-if="item.badge" :count="item.badge" :offset="[4, -4]" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <a-col :span="6" v-for="item in contentMetrics" :key="item.label">
        <a-card size="small">
          <a-statistic :title="item.label" :value="item.value" :value-style="{ fontSize: '24px', fontWeight: 600 }">
            <template #prefix><component :is="item.icon" /></template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" style="margin-top: 24px">
      <a-col :span="12">
        <a-card title="第三方服务状态" size="small">
          <div class="status-row">
            <span>Reloadly API</span>
            <a-badge :status="serviceStatus.reloadly ? 'success' : 'error'" :text="serviceStatus.reloadly ? '正常' : '异常'" />
          </div>
          <div class="status-row">
            <span>微信支付</span>
            <a-badge :status="serviceStatus.wechat ? 'success' : 'error'" :text="serviceStatus.wechat ? '正常' : '异常'" />
          </div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="Reloadly 账户余额" size="small">
          <div class="balance-display">
            <span class="hm-mono balance-value" :class="{ 'balance-low': reloadlyBalance < 500 }">
              ${{ reloadlyBalance.toFixed(2) }}
            </span>
            <span class="balance-time">最后更新：{{ lastBalanceUpdate }}</span>
          </div>
          <a-tag v-if="reloadlyBalance < 500" color="red" style="margin-top: 8px">余额不足，请及时充值</a-tag>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import {
  UserOutlined,
  UserAddOutlined,
  TeamOutlined,
  OrderedListOutlined,
  DollarOutlined,
  CheckCircleOutlined,
  WarningOutlined,
  FileTextOutlined,
  ShopOutlined
} from '@ant-design/icons-vue'

const circuitBreaker = reactive({ active: false, message: '' })

const userMetrics = [
  { label: '今日访问用户', value: 328, icon: UserOutlined },
  { label: '今日新增用户', value: 15, icon: UserAddOutlined },
  { label: '累计用户', value: 2861, icon: TeamOutlined },
  { label: '今日充值订单', value: 156, icon: OrderedListOutlined }
]

const tradeMetrics = [
  { label: '今日充值金额(CNY)', value: 4528.50, precision: 2, icon: DollarOutlined, color: '#003366' },
  { label: '充值成功率', value: 97.8, precision: 1, icon: CheckCircleOutlined, suffix: '%', color: '#52C41A' },
  { label: '异常订单', value: 3, icon: WarningOutlined, color: '#FF4D4F', badge: 3 },
  { label: '今日退款', value: 2, icon: DollarOutlined, color: '#FA8C16' }
]

const contentMetrics = [
  { label: '资讯数量', value: 42, icon: FileTextOutlined },
  { label: '企业数量', value: 18, icon: ShopOutlined },
  { label: '', value: 0, icon: FileTextOutlined },
  { label: '', value: 0, icon: FileTextOutlined }
].filter(i => i.label)

const serviceStatus = reactive({ reloadly: true, wechat: true })
const reloadlyBalance = ref(1280.50)
const lastBalanceUpdate = ref('2026-07-27 14:30')
</script>

<style scoped>
.status-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.status-row:last-child {
  border-bottom: none;
}
.balance-display {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.balance-value {
  font-size: 28px;
  font-weight: 700;
  color: #003366;
}
.balance-low {
  color: #FF4D4F;
}
.balance-time {
  font-size: 12px;
  color: #999;
}
</style>
