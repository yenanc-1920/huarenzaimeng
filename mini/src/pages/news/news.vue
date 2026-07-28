<template>
  <view class="news-page">
    <view class="news-list">
      <view v-for="item in newsList" :key="item.id" class="news-card" @tap="goDetail(item.id)">
        <image v-if="item.cover" class="news-card__cover" :src="item.cover" mode="aspectFill" />
          <view v-else class="news-card__cover news-card__cover--placeholder" />
        <view class="news-card__content">
          <view class="news-card__title-row">
            <text v-if="item.isTop" class="news-card__top-tag">置顶</text>
            <text class="news-card__title">{{ item.title }}</text>
          </view>
          <text class="news-card__summary">{{ item.summary }}</text>
          <text class="news-card__time">{{ item.publishTime }}</text>
        </view>
      </view>
    </view>
    <HmEmpty v-if="!newsList.length" text="暂无资讯" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import HmEmpty from '@/components/HmEmpty.vue'

const newsList = ref([])

onMounted(() => {
  newsList.value = [
    { id: 1, title: '孟加拉签证政策更新通知', cover: '', publishTime: '2026-07-25', isTop: true, summary: '最新签证政策变动，请及时了解...' },
    { id: 2, title: '达卡华人超市新开张', cover: '', publishTime: '2026-07-24', isTop: false, summary: '位于Gulshan区域的新华人超市...' },
    { id: 3, title: '雨季出行安全提醒', cover: '', publishTime: '2026-07-23', isTop: false, summary: '孟加拉进入雨季，请注意出行安全...' }
  ]
})

function goDetail(id) {
  uni.navigateTo({ url: `/pages/news/news?id=${id}` })
}
</script>

<style lang="scss" scoped>
.news-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding: 24rpx;
}

.news-card {
  display: flex;
  gap: 20rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;

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
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.4;
  }

  &__summary {
    font-size: 26rpx;
    color: #666666;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__time {
    font-size: 24rpx;
    color: #999999;
  }
}
</style>
