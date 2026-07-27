<template>
  <view class="recharge-page">
    <!-- 顶部信息 -->
    <view class="top-bar">
      <view class="top-bar__phone">
        <text class="top-bar__number hm-mono">{{ phone }}</text>
        <view class="top-bar__operator" @tap="showOperatorSheet = true">
          <text class="top-bar__operator-name">{{ operator ? operator.name : '选择运营商' }}</text>
          <text class="top-bar__operator-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- Tab 切换 -->
    <view class="tabs">
      <view
        class="tabs__item"
        :class="{ 'tabs__item--active': activeTab === 'AIRTIME' }"
        @tap="switchTab('AIRTIME')"
      >
        <text class="tabs__text">话费充值</text>
      </view>
      <view
        class="tabs__item"
        :class="{ 'tabs__item--active': activeTab === 'BUNDLE' }"
        @tap="switchTab('BUNDLE')"
      >
        <text class="tabs__text">流量套餐</text>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="products">
      <!-- 话费：3列网格 -->
      <view v-if="activeTab === 'AIRTIME'" class="products__grid">
        <ProductCard
          v-for="item in airtimeProducts"
          :key="item.id"
          :product="item"
          :selected="selectedProduct && selectedProduct.id === item.id"
          @select="onSelectProduct"
        />
      </view>
      <!-- 流量：列表布局 -->
      <view v-else class="products__list">
        <view
          v-for="item in bundleProducts"
          :key="item.id"
          class="bundle-item"
          :class="{ 'bundle-item--selected': selectedProduct && selectedProduct.id === item.id }"
          @tap="onSelectProduct(item)"
        >
          <view class="bundle-item__info">
            <text class="bundle-item__name">{{ item.name }}</text>
            <text class="bundle-item__amount hm-mono">৳ {{ item.amountBdt }}</text>
          </view>
          <text class="bundle-item__price hm-mono">¥ {{ item.priceCny }}</text>
        </view>
      </view>
      <HmEmpty v-if="!currentProducts.length" text="暂无可用商品" />
    </view>

    <!-- 底部固定栏 -->
    <view class="bottom-bar hm-safe-bottom">
      <view class="bottom-bar__info">
        <template v-if="selectedProduct">
          <text class="bottom-bar__price hm-mono">¥ {{ selectedProduct.priceCny }}</text>
          <text class="bottom-bar__amount hm-mono">৳ {{ selectedProduct.amountBdt }}</text>
        </template>
        <text v-else class="bottom-bar__placeholder">请选择充值金额</text>
      </view>
      <view
        class="bottom-bar__btn"
        :class="{ 'bottom-bar__btn--disabled': !selectedProduct || mnpChecking }"
        @tap="goConfirm"
      >
        <text class="bottom-bar__btn-text">{{ mnpChecking ? '校验中...' : '确认充值' }}</text>
      </view>
    </view>

    <!-- 运营商选择 -->
    <OperatorSelector
      :visible="showOperatorSheet"
      :selected="operator"
      @select="onOperatorSelect"
      @close="showOperatorSheet = false"
    />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ProductCard from '@/components/ProductCard.vue'
import OperatorSelector from '@/components/OperatorSelector.vue'
import HmEmpty from '@/components/HmEmpty.vue'
import { getOperators } from '@/utils/phone'

const phone = ref('')
const operator = ref(null)
const activeTab = ref('AIRTIME')
const selectedAirtime = ref(null)
const selectedBundle = ref(null)
const showOperatorSheet = ref(false)
const mnpChecking = ref(false)

const airtimeProducts = ref([])
const bundleProducts = ref([])

const selectedProduct = computed(() =>
  activeTab.value === 'AIRTIME' ? selectedAirtime.value : selectedBundle.value
)

const currentProducts = computed(() =>
  activeTab.value === 'AIRTIME' ? airtimeProducts.value : bundleProducts.value
)

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const options = page.options || {}
  phone.value = options.phone || ''
  const opId = Number(options.operatorId)
  if (opId) {
    operator.value = getOperators().find(op => op.id === opId) || null
  }
  loadMockProducts()
})

function loadMockProducts() {
  airtimeProducts.value = [
    { id: 1, priceCny: '21.5', amountBdt: '250', name: '' },
    { id: 2, priceCny: '42.5', amountBdt: '500', name: '' },
    { id: 3, priceCny: '85.0', amountBdt: '1000', name: '' },
    { id: 4, priceCny: '127.5', amountBdt: '1500', name: '' },
    { id: 5, priceCny: '170.0', amountBdt: '2000', name: '' },
    { id: 6, priceCny: '255.0', amountBdt: '3000', name: '' }
  ]
  bundleProducts.value = [
    { id: 101, priceCny: '35.0', amountBdt: '399', name: '7天 3GB流量包' },
    { id: 102, priceCny: '55.0', amountBdt: '699', name: '30天 10GB流量包' },
    { id: 103, priceCny: '85.0', amountBdt: '999', name: '30天 20GB流量包' }
  ]
}

function switchTab(tab) {
  activeTab.value = tab
}

function onSelectProduct(product) {
  if (mnpChecking.value) return
  if (activeTab.value === 'AIRTIME') {
    selectedAirtime.value = product
  } else {
    selectedBundle.value = product
  }
}

function onOperatorSelect(op) {
  operator.value = op
  showOperatorSheet.value = false
}

function goConfirm() {
  if (!selectedProduct.value || mnpChecking.value) return
  uni.navigateTo({
    url: `/pages/confirm-order/confirm-order?phone=${phone.value}&operatorId=${operator.value?.id || ''}&productId=${selectedProduct.value.id}&topupType=${activeTab.value}`
  })
}
</script>

<style lang="scss" scoped>
.recharge-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding-bottom: 140rpx;
}

.top-bar {
  background: #FFFFFF;
  padding: 24rpx;

  &__phone {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__number {
    font-size: 36rpx;
    font-weight: 700;
    color: #333333;
  }

  &__operator {
    display: flex;
    align-items: center;
    gap: 4rpx;
  }

  &__operator-name {
    font-size: 28rpx;
    color: #003366;
  }

  &__operator-arrow {
    font-size: 28rpx;
    color: #999999;
  }
}

.tabs {
  display: flex;
  background: #FFFFFF;
  border-bottom: 1rpx solid #E8E8E8;

  &__item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24rpx 0;
    position: relative;

    &--active {
      .tabs__text {
        color: #003366;
        font-weight: 600;
      }

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 48rpx;
        height: 4rpx;
        background: #003366;
        border-radius: 2rpx;
      }
    }
  }

  &__text {
    font-size: 30rpx;
    color: #666666;
  }
}

.products {
  padding: 24rpx;

  &__grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16rpx;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 16rpx;
  }
}

.bundle-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  border-radius: 12rpx;
  border: 2rpx solid #E8E8E8;
  padding: 28rpx 24rpx;

  &--selected {
    border-color: #003366;
    background: #F0F5FF;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  &__name {
    font-size: 28rpx;
    color: #333333;
  }

  &__amount {
    font-size: 24rpx;
    color: #999999;
  }

  &__price {
    font-size: 40rpx;
    font-weight: 700;
    color: #003366;
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  padding: 20rpx 24rpx;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);

  &__info {
    display: flex;
    flex-direction: column;
  }

  &__price {
    font-size: 40rpx;
    font-weight: 700;
    color: #003366;
  }

  &__amount {
    font-size: 24rpx;
    color: #999999;
  }

  &__placeholder {
    font-size: 28rpx;
    color: #999999;
  }

  &__btn {
    padding: 20rpx 48rpx;
    background: #003366;
    border-radius: 12rpx;

    &--disabled {
      background: #CCCCCC;
    }
  }

  &__btn-text {
    font-size: 30rpx;
    font-weight: 600;
    color: #FFFFFF;
  }
}
</style>
