<template>
  <view class="my-page">
    <!-- 头像区域 -->
    <view class="profile">
      <view class="profile__avatar" @tap="onLogin">
        <text v-if="!isLoggedIn" class="profile__avatar-text">登录</text>
        <text v-else class="profile__avatar-text">{{ nickname.charAt(0) }}</text>
      </view>
      <text class="profile__name">{{ isLoggedIn ? nickname : '点击登录' }}</text>
    </view>

    <!-- 充值记录 -->
    <view class="section">
      <view class="section__header">
        <text class="section__title">我的充值记录</text>
        <text class="section__more" @tap="goOrders">查看全部 ></text>
      </view>
      <view v-if="orders.length" class="order-list">
        <view v-for="order in orders" :key="order.id" class="order-card">
          <view class="order-card__top">
            <text class="order-card__no hm-mono">{{ order.orderNo }}</text>
            <text class="order-card__status" :style="{ color: statusColor(order.status) }">{{ statusText(order.status) }}</text>
          </view>
          <view class="order-card__mid">
            <text class="order-card__phone hm-mono">{{ order.phone }}</text>
            <text class="order-card__amount hm-mono">¥{{ order.amount }}</text>
          </view>
          <text class="order-card__time">{{ order.time }}</text>
        </view>
      </view>
      <HmEmpty v-else text="暂无充值记录" />
    </view>

    <!-- 登录引导（游客） -->
    <view v-if="!isLoggedIn" class="login-guide">
      <text class="login-guide__text">登录后可保存常用号码、同步个人信息</text>
      <view class="login-guide__btn" @tap="onLogin">
        <text class="login-guide__btn-text">去登录</text>
      </view>
    </view>

    <!-- 底部固定区 -->
    <view class="footer hm-safe-bottom">
      <text class="footer__service" @tap="contactService">联系客服</text>
      <text v-if="isLoggedIn" class="footer__logout" @tap="onLogout">退出登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import HmEmpty from '@/components/HmEmpty.vue'

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const nickname = ref('用户')
const orders = ref([])

onMounted(() => {
  orders.value = [
    { id: 1, orderNo: 'HM20260725001', phone: '01712345678', amount: '42.5', status: 'SUCCESSFUL', time: '2026-07-25 18:30' },
    { id: 2, orderNo: 'HM20260724002', phone: '01898765432', amount: '85.0', status: 'PROCESSING', time: '2026-07-24 14:20' },
    { id: 3, orderNo: 'HM20260723003', phone: '01712345678', amount: '21.5', status: 'FAILED', time: '2026-07-23 09:15' }
  ]
})

function statusColor(status) {
  const map = { SUCCESSFUL: '#52C41A', PROCESSING: '#1890FF', PENDING: '#FAAD14', FAILED: '#FF4D4F' }
  return map[status] || '#999999'
}

function statusText(status) {
  const map = { SUCCESSFUL: '成功', PROCESSING: '充值中', PENDING: '待支付', FAILED: '异常' }
  return map[status] || status
}

function onLogin() {
  uni.showToast({ title: '登录功能开发中', icon: 'none' })
}

function onLogout() {
  userStore.logout()
  uni.showToast({ title: '已退出登录', icon: 'none' })
}

function goOrders() {
  uni.navigateTo({ url: '/pages/my/my?tab=orders' })
}

function contactService() {
  // 微信小程序在线客服
}
</script>

<style lang="scss" scoped>
.my-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding-bottom: 120rpx;
}

.profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 24rpx 40rpx;
  background: #FFFFFF;

  &__avatar {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    background: #F5F7FA;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16rpx;
  }

  &__avatar-text {
    font-size: 36rpx;
    color: #999999;
  }

  &__name {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
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

.order-card {
  background: #FFFFFF;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;

  &__top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12rpx;
  }

  &__no {
    font-size: 24rpx;
    color: #999999;
  }

  &__status {
    font-size: 24rpx;
    font-weight: 500;
  }

  &__mid {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8rpx;
  }

  &__phone {
    font-size: 28rpx;
    color: #333333;
  }

  &__amount {
    font-size: 32rpx;
    font-weight: 700;
    color: #FF4D4F;
  }

  &__time {
    font-size: 24rpx;
    color: #999999;
  }
}

.login-guide {
  margin: 24rpx;
  background: #F0F5FF;
  border-radius: 12rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;

  &__text {
    font-size: 26rpx;
    color: #666666;
    flex: 1;
  }

  &__btn {
    padding: 12rpx 32rpx;
    background: #003366;
    border-radius: 8rpx;
  }

  &__btn-text {
    font-size: 26rpx;
    color: #FFFFFF;
  }
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48rpx;
  padding: 24rpx;
  background: #FFFFFF;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);

  &__service {
    font-size: 28rpx;
    color: #003366;
  }

  &__logout {
    font-size: 28rpx;
    color: #FF4D4F;
  }
}
</style>
