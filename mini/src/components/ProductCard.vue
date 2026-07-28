<template>
  <view
    class="product-card"
    :class="{ 'product-card--selected': selected }"
    @tap="$emit('select', product)"
  >
    <text class="product-card__price hm-mono">¥ {{ product.priceCny }}</text>
    <text class="product-card__amount hm-mono">৳ {{ product.amountBdt }}</text>
    <text v-if="product.name" class="product-card__name">{{ product.name }}</text>
  </view>
</template>

<script setup>
defineProps({
  product: { type: Object, required: true },
  selected: { type: Boolean, default: false }
})
defineEmits(['select'])
</script>

<style lang="scss" scoped>
.product-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24rpx;
  background: #FFFFFF;
  border-radius: 12rpx;
  border: 2rpx solid #E8E8E8;
  transition: all 0.2s;

  &--selected {
    border-color: #003366;
    background: #F0F5FF;
  }

  &__price {
    font-size: 40rpx;
    font-weight: 700;
    color: #003366;
  }

  &__amount {
    font-size: 24rpx;
    color: #999999;
    margin-top: 8rpx;
  }

  &__name {
    font-size: 24rpx;
    color: #333333;
    margin-top: 8rpx;
    text-align: center;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 100%;
  }
}
</style>
