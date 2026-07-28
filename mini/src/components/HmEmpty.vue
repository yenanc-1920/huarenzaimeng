<template>
  <view class="hm-empty">
    <image v-if="icon" :src="icon" class="hm-empty__icon" mode="aspectFit" />
    <view v-else class="hm-empty__icon hm-empty__icon--default">
      <text class="hm-empty__emoji">📭</text>
    </view>
    <text class="hm-empty__text">{{ text }}</text>
    <view v-if="showAction" class="hm-empty__action" @tap="$emit('action')">
      <text class="hm-empty__action-text">{{ actionText }}</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  text: { type: String, default: '暂无数据' },
  icon: { type: String, default: '' },
  showAction: { type: Boolean, default: false },
  actionText: { type: String, default: '重试' }
})
defineEmits(['action'])
</script>

<style lang="scss" scoped>
.hm-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 48rpx;

  &__icon {
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 24rpx;

    &--default {
      display: flex;
      align-items: center;
      justify-content: center;
      background: #F5F7FA;
      border-radius: 50%;
    }
  }

  &__emoji {
    font-size: 64rpx;
  }

  &__text {
    font-size: 28rpx;
    color: #999999;
    text-align: center;
  }

  &__action {
    margin-top: 32rpx;
    padding: 16rpx 48rpx;
    border: 2rpx solid #003366;
    border-radius: 8rpx;
  }

  &__action-text {
    font-size: 28rpx;
    color: #003366;
  }
}
</style>
