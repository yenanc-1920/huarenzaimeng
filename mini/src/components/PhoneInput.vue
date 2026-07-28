<template>
  <view class="phone-input">
    <view class="phone-input__wrapper" :class="{ 'phone-input__wrapper--error': errorMsg }">
      <text class="phone-input__prefix">+880</text>
      <view class="phone-input__divider" />
      <input
        class="phone-input__field"
        type="number"
        :maxlength="11"
        :value="modelValue"
        placeholder="01XXXXXXXXX"
        placeholder-class="phone-input__placeholder"
        @input="onInput"
        @blur="onBlur"
      />
    </view>
    <text v-if="errorMsg" class="phone-input__error">{{ errorMsg }}</text>
    <view v-if="operator" class="phone-input__operator">
      <text class="phone-input__operator-name">{{ operator.name }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { validatePhone, identifyOperator } from '@/utils/phone'

const props = defineProps({
  modelValue: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue', 'operator-change', 'valid-change'])

const errorMsg = ref('')
const operator = ref(null)

function onInput(e) {
  const val = e.detail.value.replace(/\D/g, '').slice(0, 11)
  emit('update:modelValue', val)
  if (val.length === 11) {
    validate(val)
  } else {
    errorMsg.value = ''
    emit('valid-change', false)
  }
}

function onBlur() {
  if (props.modelValue && props.modelValue.length > 0 && props.modelValue.length < 11) {
    errorMsg.value = '请输入完整的11位手机号'
    emit('valid-change', false)
  }
}

function validate(val) {
  if (validatePhone(val)) {
    errorMsg.value = ''
    emit('valid-change', true)
    const op = identifyOperator(val)
    operator.value = op
    emit('operator-change', op)
  } else {
    errorMsg.value = '号码格式不正确，应为01[3-9]开头'
    operator.value = null
    emit('valid-change', false)
    emit('operator-change', null)
  }
}

watch(() => props.modelValue, (val) => {
  if (val && val.length === 11) {
    validate(val)
  } else {
    operator.value = null
  }
})
</script>

<style lang="scss" scoped>
.phone-input {
  &__wrapper {
    display: flex;
    align-items: center;
    background: #F5F7FA;
    border-radius: 12rpx;
    padding: 20rpx 24rpx;
    border: 2rpx solid #E8E8E8;

    &--error {
      border-color: #FF4D4F;
    }
  }

  &__prefix {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
  }

  &__divider {
    width: 2rpx;
    height: 36rpx;
    background: #E8E8E8;
    margin: 0 20rpx;
  }

  &__field {
    flex: 1;
    font-size: 32rpx;
    color: #333333;
    font-family: 'SF Mono', 'Menlo', 'Roboto Mono', monospace;
  }

  &__placeholder {
    color: #999999;
    font-size: 32rpx;
  }

  &__error {
    display: block;
    margin-top: 8rpx;
    font-size: 24rpx;
    color: #FF4D4F;
  }

  &__operator {
    margin-top: 8rpx;
  }

  &__operator-name {
    font-size: 24rpx;
    color: #52C41A;
  }
}
</style>
