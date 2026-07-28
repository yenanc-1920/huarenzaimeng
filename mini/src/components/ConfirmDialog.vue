<template>
  <view v-if="visible" class="confirm-dialog">
    <view class="confirm-dialog__mask" />
    <view class="confirm-dialog__body">
      <text class="confirm-dialog__title">请核对号码 {{ phone }}</text>
      <text class="confirm-dialog__subtitle">充值成功后无法退款/冲正</text>
      <view class="confirm-dialog__actions">
        <view class="confirm-dialog__btn confirm-dialog__btn--cancel" @tap="$emit('cancel')">
          <text class="confirm-dialog__btn-text confirm-dialog__btn-text--cancel">取消</text>
        </view>
        <view class="confirm-dialog__btn confirm-dialog__btn--confirm" @tap="$emit('confirm')">
          <text class="confirm-dialog__btn-text confirm-dialog__btn-text--confirm">确认充值</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
defineProps({
  visible: { type: Boolean, default: false },
  phone: { type: String, default: '' }
})
defineEmits(['confirm', 'cancel'])
</script>

<style lang="scss" scoped>
.confirm-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;

  &__mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
  }

  &__body {
    position: relative;
    width: 560rpx;
    background: #FFFFFF;
    border-radius: 24rpx;
    padding: 48rpx 40rpx 40rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__title {
    font-size: 36rpx;
    font-weight: 700;
    color: #333333;
    text-align: center;
  }

  &__subtitle {
    font-size: 28rpx;
    color: #999999;
    margin-top: 16rpx;
    text-align: center;
  }

  &__actions {
    display: flex;
    gap: 24rpx;
    margin-top: 48rpx;
    width: 100%;
  }

  &__btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 88rpx;
    border-radius: 12rpx;

    &--cancel {
      background: #F5F7FA;
    }

    &--confirm {
      background: #003366;
    }
  }

  &__btn-text {
    font-size: 32rpx;
    font-weight: 500;

    &--cancel {
      color: #666666;
    }

    &--confirm {
      color: #FFFFFF;
    }
  }
}
</style>
