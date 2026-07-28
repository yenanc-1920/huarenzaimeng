<template>
  <view class="dual-clock">
    <view class="dual-clock__item">
      <text class="dual-clock__label">🇧🇩 达卡</text>
      <text class="dual-clock__time hm-mono">{{ dhakaTime }}</text>
    </view>
    <view class="dual-clock__item">
      <text class="dual-clock__label">🇨🇳 北京</text>
      <text class="dual-clock__time hm-mono">{{ beijingTime }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getDhakaTime, getBeijingTime, formatTime } from '@/utils/time'

const dhakaTime = ref('')
const beijingTime = ref('')
let timer = null

function update() {
  dhakaTime.value = formatTime(getDhakaTime())
  beijingTime.value = formatTime(getBeijingTime())
}

onMounted(() => {
  update()
  timer = setInterval(update, 60000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style lang="scss" scoped>
.dual-clock {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;

  &__item {
    display: flex;
    align-items: center;
    gap: 8rpx;
  }

  &__label {
    font-size: 20rpx;
    color: #999999;
  }

  &__time {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
  }
}
</style>
