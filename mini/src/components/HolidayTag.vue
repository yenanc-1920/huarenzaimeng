<template>
  <view class="holiday-tag" :class="{ 'holiday-tag--pending': isPending }">
    <text class="holiday-tag__text">{{ label }}</text>
    <view v-if="isPending" class="holiday-tag__help" @tap="showTip = !showTip">
      <text class="holiday-tag__help-icon">?</text>
    </view>
    <view v-if="isPending && showTip" class="holiday-tag__tooltip">
      <text class="holiday-tag__tooltip-text">孟加拉伊斯兰节假日日期须等待官方观月确认，可能有±1天偏差</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  status: { type: String, default: 'CONFIRMED' },
  name: { type: String, default: '' }
})

const showTip = ref(false)
const isPending = computed(() => props.status === 'PENDING_MOON')
const label = computed(() => {
  if (isPending.value) return `${props.name}（待确认）`
  return props.name
})
</script>

<style lang="scss" scoped>
.holiday-tag {
  display: inline-flex;
  align-items: center;
  position: relative;
  gap: 4rpx;

  &__text {
    font-size: 24rpx;
    color: #666666;
  }

  &--pending .holiday-tag__text {
    color: #FAAD14;
  }

  &__help {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28rpx;
    height: 28rpx;
    border-radius: 50%;
    background: #FAAD14;
  }

  &__help-icon {
    font-size: 20rpx;
    color: #FFFFFF;
    font-weight: bold;
  }

  &__tooltip {
    position: absolute;
    top: 100%;
    left: 0;
    margin-top: 8rpx;
    padding: 16rpx 20rpx;
    background: #333333;
    border-radius: 8rpx;
    width: 400rpx;
    z-index: 100;
  }

  &__tooltip-text {
    font-size: 22rpx;
    color: #FFFFFF;
    line-height: 1.5;
  }
}
</style>
