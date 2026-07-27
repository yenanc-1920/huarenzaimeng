<template>
  <view v-if="visible" class="operator-selector">
    <view class="operator-selector__mask" @tap="close" />
    <view class="operator-selector__sheet">
      <view class="operator-selector__header">
        <text class="operator-selector__title">选择运营商</text>
        <view class="operator-selector__close" @tap="close">
          <text class="operator-selector__close-icon">✕</text>
        </view>
      </view>
      <view class="operator-selector__list">
        <view
          v-for="op in operators"
          :key="op.id"
          class="operator-selector__item"
          :class="{ 'operator-selector__item--active': selected && selected.id === op.id }"
          @tap="select(op)"
        >
          <text class="operator-selector__name">{{ op.name }}</text>
          <text class="operator-selector__prefix">{{ op.prefix.join(' / ') }}</text>
          <text v-if="selected && selected.id === op.id" class="operator-selector__check">✓</text>
        </view>
      </view>
      <view class="operator-selector__safe hm-safe-bottom" />
    </view>
  </view>
</template>

<script setup>
import { getOperators } from '@/utils/phone'

defineProps({
  visible: { type: Boolean, default: false },
  selected: { type: Object, default: null }
})
const emit = defineEmits(['select', 'close'])

const operators = getOperators()

function select(op) {
  emit('select', op)
}
function close() {
  emit('close')
}
</script>

<style lang="scss" scoped>
.operator-selector {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;

  &__mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
  }

  &__sheet {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: #FFFFFF;
    border-radius: 24rpx 24rpx 0 0;
    padding: 32rpx 24rpx 0;
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
  }

  &__title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
  }

  &__close {
    padding: 8rpx;
  }

  &__close-icon {
    font-size: 32rpx;
    color: #999999;
  }

  &__list {
    max-height: 600rpx;
    overflow-y: auto;
  }

  &__item {
    display: flex;
    align-items: center;
    padding: 28rpx 24rpx;
    border-radius: 12rpx;
    margin-bottom: 12rpx;
    background: #F5F7FA;

    &--active {
      background: #F0F5FF;
      border: 2rpx solid #003366;
    }
  }

  &__name {
    font-size: 30rpx;
    color: #333333;
    font-weight: 500;
    flex: 1;
  }

  &__prefix {
    font-size: 24rpx;
    color: #999999;
    margin-right: 16rpx;
  }

  &__check {
    font-size: 32rpx;
    color: #003366;
    font-weight: bold;
  }

  &__safe {
    height: 0;
  }
}
</style>
