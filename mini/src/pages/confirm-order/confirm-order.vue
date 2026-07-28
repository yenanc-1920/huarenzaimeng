<template>
  <view class="confirm-page">
    <view class="order-card hm-card">
      <view class="order-card__row">
        <text class="order-card__label">充值号码</text>
        <text class="order-card__value hm-mono order-card__value--phone">{{ phone }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">运营商</text>
        <text class="order-card__value">{{ operatorName }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">充值内容</text>
        <text class="order-card__value">{{ productName }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">到账面额</text>
        <text class="order-card__value hm-mono">৳ {{ product.amountBdt }}</text>
      </view>
      <view class="order-card__divider" />
      <view class="order-card__row order-card__row--total">
        <text class="order-card__label">支付金额</text>
        <text class="order-card__value hm-mono order-card__value--price">¥ {{ product.priceCny }}</text>
      </view>
    </view>

    <view class="risk-tip">
      <text class="risk-tip__text">为防止洗钱，同一微信号每天最多充值9次，同一手机号每天最多充值5次</text>
    </view>

    <view class="bottom-action hm-safe-bottom">
      <view
        class="confirm-btn"
        :class="{ 'confirm-btn--loading': submitting }"
        @tap="onConfirm"
      >
        <text class="confirm-btn__text">{{ submitting ? '提交中...' : '确认充值' }}</text>
      </view>
    </view>

    <ConfirmDialog
      :visible="showDialog"
      :phone="phone"
      @confirm="onDialogConfirm"
      @cancel="showDialog = false"
    />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { getOperators } from '@/utils/phone'

const phone = ref('')
const operatorName = ref('')
const productName = ref('')
const topupType = ref('AIRTIME')
const submitting = ref(false)
const showDialog = ref(false)

const product = ref({ priceCny: '0', amountBdt: '0', name: '' })

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const options = page.options || {}
  phone.value = options.phone || ''
  topupType.value = options.topupType || 'AIRTIME'

  const opId = Number(options.operatorId)
  const op = getOperators().find(o => o.id === opId)
  operatorName.value = op ? op.name : '未知运营商'

  loadMockProduct(options.productId)
})

function loadMockProduct(productId) {
  const mockAirtime = [
    { id: '1', priceCny: '21.5', amountBdt: '250', name: '话费 ৳250' },
    { id: '2', priceCny: '42.5', amountBdt: '500', name: '话费 ৳500' },
    { id: '3', priceCny: '85.0', amountBdt: '1000', name: '话费 ৳1000' },
    { id: '4', priceCny: '127.5', amountBdt: '1500', name: '话费 ৳1500' },
    { id: '5', priceCny: '170.0', amountBdt: '2000', name: '话费 ৳2000' },
    { id: '6', priceCny: '255.0', amountBdt: '3000', name: '话费 ৳3000' }
  ]
  const mockBundle = [
    { id: '101', priceCny: '35.0', amountBdt: '399', name: '7天 3GB流量包' },
    { id: '102', priceCny: '55.0', amountBdt: '699', name: '30天 10GB流量包' },
    { id: '103', priceCny: '85.0', amountBdt: '999', name: '30天 20GB流量包' }
  ]
  const list = topupType.value === 'AIRTIME' ? mockAirtime : mockBundle
  const found = list.find(p => p.id === productId)
  if (found) {
    product.value = found
    productName.value = found.name
  }
}

function onConfirm() {
  if (submitting.value) return
  showDialog.value = true
}

function onDialogConfirm() {
  showDialog.value = false
  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    uni.redirectTo({
      url: `/pages/result/result?status=success&phone=${phone.value}&amount=${product.value.amountBdt}&price=${product.value.priceCny}`
    })
  }, 1500)
}
</script>

<style lang="scss" scoped>
.confirm-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding: 24rpx;
  padding-bottom: 180rpx;
}

.order-card {
  &__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 0;

    &--total {
      padding-top: 28rpx;
    }
  }

  &__label {
    font-size: 28rpx;
    color: #666666;
  }

  &__value {
    font-size: 28rpx;
    color: #333333;

    &--phone {
      font-size: 36rpx;
      font-weight: 700;
    }

    &--price {
      font-size: 44rpx;
      font-weight: 700;
      color: #003366;
    }
  }

  &__divider {
    height: 1rpx;
    background: #E8E8E8;
    margin: 8rpx 0;
  }
}

.risk-tip {
  margin-top: 24rpx;
  padding: 0 8rpx;

  &__text {
    font-size: 24rpx;
    color: #999999;
    line-height: 1.6;
  }
}

.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 24rpx;
  background: #FFFFFF;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.confirm-btn {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #003366;
  border-radius: 12rpx;

  &--loading {
    background: #999999;
  }

  &__text {
    font-size: 34rpx;
    font-weight: 600;
    color: #FFFFFF;
  }
}
</style>
