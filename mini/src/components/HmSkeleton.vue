<template>
  <view class="hm-skeleton">
    <view v-if="avatar" class="hm-skeleton__avatar hm-skeleton__animate" :style="{ width: avatarSize, height: avatarSize }" />
    <view class="hm-skeleton__content">
      <view
        v-for="i in rows"
        :key="i"
        class="hm-skeleton__row hm-skeleton__animate"
        :style="{ width: i === rows ? '60%' : '100%' }"
      />
    </view>
  </view>
</template>

<script setup>
defineProps({
  rows: { type: Number, default: 3 },
  avatar: { type: Boolean, default: false },
  avatarSize: { type: String, default: '80rpx' }
})
</script>

<style lang="scss" scoped>
.hm-skeleton {
  display: flex;
  padding: 24rpx;

  &__avatar {
    flex-shrink: 0;
    margin-right: 24rpx;
    border-radius: 50%;
    background: #E8E8E8;
  }

  &__content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20rpx;
  }

  &__row {
    height: 28rpx;
    border-radius: 8rpx;
    background: #E8E8E8;
  }

  &__animate {
    animation: skeleton-pulse 1.5s ease-in-out infinite;
  }
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
</style>
