<template>
  <view class="hm-error">
    <view class="hm-error__icon">
      <text class="hm-error__emoji">⚠️</text>
    </view>
    <text class="hm-error__text">{{ text }}</text>
    <view class="hm-error__btn" @tap="$emit('retry')">
      <text class="hm-error__btn-text">重试</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  text: { type: String, default: '加载失败，请重试' }
})
defineEmits(['retry'])
</script>

<style lang="scss" scoped>
.hm-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 48rpx;

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 24rpx;
    background: #FFF2F0;
    border-radius: 50%;
  }

  &__emoji {
    font-size: 64rpx;
  }

  &__text {
    font-size: 28rpx;
    color: #999999;
    text-align: center;
    margin-bottom: 32rpx;
  }

  &__btn {
    padding: 16rpx 48rpx;
    background: #003366;
    border-radius: 8rpx;
  }

  &__btn-text {
    font-size: 28rpx;
    color: #FFFFFF;
  }
}
</style>
