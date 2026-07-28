<template>
  <view class="index-page">
    <!-- Custom Header -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header__content" :style="{ height: navHeight + 'px' }">
        <view class="header__left">
          <image class="header__logo" src="/static/logo.png" mode="aspectFit" />
          <text class="header__title">华人在孟</text>
        </view>
        <view class="header__right" :style="{ marginRight: capsuleRight + 'px' }">
          <DualClock />
        </view>
      </view>
      <view class="header__tags">
        <HolidayTag
          v-for="h in holidays"
          :key="h.id"
          :name="h.name"
          :status="h.status"
        />
        <text v-if="!holidays.length" class="header__workday">🇨🇳 工作日 | 🇧🇩 工作日</text>
      </view>
    </view>

    <!-- 充值入口卡片 -->
    <view class="recharge-card hm-card">
      <view class="recharge-card__title">
        <text class="recharge-card__title-text">话费充值</text>
      </view>
      <PhoneInput
        v-model="phone"
        @operator-change="onOperatorChange"
        @valid-change="onValidChange"
      />
      <view class="recharge-card__operator" @tap="showOperatorSheet = true">
        <text v-if="operator" class="recharge-card__operator-name">{{ operator.name }}</text>
        <text v-else class="recharge-card__operator-placeholder">自动识别运营商</text>
        <text class="recharge-card__operator-arrow">›</text>
      </view>
      <view
        class="recharge-card__btn"
        :class="{ 'recharge-card__btn--disabled': !phoneValid }"
        @tap="goRecharge"
      >
        <text class="recharge-card__btn-text">立即充值</text>
      </view>
    </view>

    <!-- 最新资讯 -->
    <view class="section">
      <view class="section__header">
        <text class="section__title">最新资讯</text>
        <text class="section__more" @tap="goNews">查看更多 ></text>
      </view>
      <view class="news-list">
        <view v-for="item in newsList" :key="item.id" class="news-item" @tap="goNewsDetail(item.id)">
          <image v-if="item.cover" class="news-item__cover" :src="item.cover" mode="aspectFill" />
          <view v-else class="news-item__cover news-item__cover--placeholder" />
          <view class="news-item__content">
            <view class="news-item__title-row">
              <text v-if="item.isTop" class="news-item__top-tag">置顶</text>
              <text class="news-item__title">{{ item.title }}</text>
            </view>
            <text class="news-item__time">{{ item.publishTime }}</text>
          </view>
        </view>
        <HmEmpty v-if="!newsList.length" text="暂无资讯" />
      </view>
    </view>

    <!-- 企业黄页入口 -->
    <view class="section">
      <view class="section__header">
        <text class="section__title">企业黄页</text>
        <text class="section__more" @tap="goCompany">查看更多 ></text>
      </view>
      <view class="company-grid">
        <view v-for="cat in categories" :key="cat.id" class="company-grid__item" @tap="goCompanyList(cat.id)">
          <text class="company-grid__icon">{{ cat.icon }}</text>
          <text class="company-grid__name">{{ cat.name }}</text>
        </view>
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
import { ref, onMounted } from 'vue'
import DualClock from '@/components/DualClock.vue'
import HolidayTag from '@/components/HolidayTag.vue'
import PhoneInput from '@/components/PhoneInput.vue'
import OperatorSelector from '@/components/OperatorSelector.vue'
import HmEmpty from '@/components/HmEmpty.vue'

const statusBarHeight = ref(20)
const navHeight = ref(44)
const capsuleRight = ref(100)

const phone = ref('')
const phoneValid = ref(false)
const operator = ref(null)
const showOperatorSheet = ref(false)
const holidays = ref([])
const newsList = ref([])

const categories = [
  { id: 1, name: '酒店住宿', icon: '🏨' },
  { id: 2, name: '餐饮生活', icon: '🍽️' },
  { id: 3, name: '企业服务', icon: '🏢' },
  { id: 4, name: '物流运输', icon: '🚚' }
]

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync()
  statusBarHeight.value = sysInfo.statusBarHeight || 20
  const menuBtn = uni.getMenuButtonBoundingClientRect()
  capsuleRight.value = sysInfo.windowWidth - menuBtn.left + 8
  navHeight.value = sysInfo.platform === 'ios' ? 44 : 48

  loadMockData()
})

function loadMockData() {
  holidays.value = [
    { id: 1, name: '开斋节', status: 'PENDING_MOON' }
  ]
  newsList.value = [
    { id: 1, title: '孟加拉签证政策更新通知', cover: '', publishTime: '2026-07-25', isTop: true },
    { id: 2, title: '达卡华人超市新开张', cover: '', publishTime: '2026-07-24', isTop: false },
    { id: 3, title: '雨季出行安全提醒', cover: '', publishTime: '2026-07-23', isTop: false }
  ]
}

function onOperatorChange(op) {
  operator.value = op
}

function onValidChange(valid) {
  phoneValid.value = valid
}

function onOperatorSelect(op) {
  operator.value = op
  showOperatorSheet.value = false
}

function goRecharge() {
  if (!phoneValid.value) return
  uni.navigateTo({
    url: `/pages/recharge/recharge?phone=${phone.value}&operatorId=${operator.value?.id || ''}`
  })
}

function goNews() {
  uni.switchTab({ url: '/pages/news/news' })
}

function goNewsDetail(id) {
  uni.navigateTo({ url: `/pages/news/news?id=${id}` })
}

function goCompany() {
  uni.switchTab({ url: '/pages/company/company' })
}

function goCompanyList(id) {
  uni.navigateTo({ url: `/pages/company/company?categoryId=${id}` })
}
</script>

<style lang="scss" scoped>
.index-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding-bottom: 24rpx;
}

.header {
  background: #FFFFFF;
  padding-left: 24rpx;
  padding-right: 24rpx;

  &__content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__left {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  &__logo {
    width: 48rpx;
    height: 48rpx;
  }

  &__title {
    font-size: 36rpx;
    font-weight: 700;
    color: #003366;
  }

  &__right {
    display: flex;
    align-items: center;
  }

  &__tags {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 12rpx 0 20rpx;
    flex-wrap: wrap;
  }

  &__workday {
    font-size: 24rpx;
    color: #666666;
  }
}

.recharge-card {
  margin-top: 24rpx;

  &__title {
    margin-bottom: 20rpx;
  }

  &__title-text {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
  }

  &__operator {
    display: flex;
    align-items: center;
    margin-top: 16rpx;
    padding: 12rpx 0;
  }

  &__operator-name {
    font-size: 28rpx;
    color: #003366;
    font-weight: 500;
  }

  &__operator-placeholder {
    font-size: 28rpx;
    color: #999999;
  }

  &__operator-arrow {
    font-size: 32rpx;
    color: #999999;
    margin-left: 8rpx;
  }

  &__btn {
    margin-top: 24rpx;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #003366;
    border-radius: 12rpx;

    &--disabled {
      background: #CCCCCC;
    }
  }

  &__btn-text {
    font-size: 32rpx;
    font-weight: 600;
    color: #FFFFFF;
  }
}

.section {
  margin-top: 24rpx;
  padding: 0 24rpx;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20rpx;
  }

  &__title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
  }

  &__more {
    font-size: 24rpx;
    color: #999999;
  }
}

.news-list {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
}

.news-item {
  display: flex;
  gap: 20rpx;
  padding: 16rpx 0;

  &:not(:last-child) {
    border-bottom: 1rpx solid #F0F0F0;
  }

  &__cover {
    width: 192rpx;
    height: 128rpx;
    border-radius: 8rpx;
    flex-shrink: 0;
    background: #F5F7FA;
  }

  &__content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  &__title-row {
    display: flex;
    align-items: flex-start;
    gap: 8rpx;
  }

  &__top-tag {
    flex-shrink: 0;
    font-size: 20rpx;
    color: #FFFFFF;
    background: #FF4D4F;
    border-radius: 4rpx;
    padding: 2rpx 8rpx;
  }

  &__title {
    font-size: 28rpx;
    font-weight: 500;
    color: #333333;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  &__time {
    font-size: 24rpx;
    color: #999999;
  }
}

.company-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;

  &__item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: #FFFFFF;
    border-radius: 16rpx;
    padding: 32rpx 24rpx;
    gap: 12rpx;
  }

  &__icon {
    font-size: 64rpx;
  }

  &__name {
    font-size: 28rpx;
    font-weight: 500;
    color: #333333;
  }
}
</style>
