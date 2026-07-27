<template>
  <view class="company-page">
    <view class="category-grid">
      <view v-for="cat in categories" :key="cat.id" class="category-card" @tap="goList(cat.id)">
        <text class="category-card__icon">{{ cat.icon }}</text>
        <text class="category-card__name">{{ cat.name }}</text>
        <text class="category-card__count">{{ cat.count }}家企业</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const categories = ref([
  { id: 1, name: '酒店住宿', icon: '🏨', count: 12 },
  { id: 2, name: '餐饮生活', icon: '🍽️', count: 8 },
  { id: 3, name: '企业服务', icon: '🏢', count: 15 },
  { id: 4, name: '物流运输', icon: '🚚', count: 6 }
])

function goList(id) {
  uni.navigateTo({ url: `/pages/company/company?categoryId=${id}` })
}
</script>

<style lang="scss" scoped>
.company-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding: 24rpx;
}

.category-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 48rpx 24rpx;
  gap: 12rpx;

  &__icon {
    font-size: 64rpx;
  }

  &__name {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
  }

  &__count {
    font-size: 24rpx;
    color: #999999;
  }
}
</style>
