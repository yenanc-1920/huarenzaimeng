<template>
  <view class="confirm-page">
    <view class="order-card hm-card">
      <view class="order-card__row">
        <text class="order-card__label">充值号码</text>
        <text class="order-card__value hm-mono order-card__value--phone">{{ phone }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">运营商</text>
        <text class="order-card__value">{{ operatorName }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">充值内容</text>
        <text class="order-card__value">{{ productName }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">到账面额</text>
        <text class="order-card__value hm-mono">৳ {{ product.amountBdt }}</text>
      </view>
      <view class="order-card__divider" />
      <view class="order-card__row order-card__row--total">
        <text class="order-card__label">支付金额</text>
        <text class="order-card__value hm-mono order-card__value--price">¥ {{ product.priceCny }}</text>
      </view>
    </view>

    <view class="risk-tip">
      <text class="risk-tip__text">为防止洗钱，同一微信号每天最多充值9次，同一手机号每天最多充值5次</text>
    </view>

    <view class="bottom-action hm-safe-bottom">
      <view
        class="confirm-btn"
        :class="{ 'confirm-btn--loading': submitting }"
        @tap="onConfirm"
      >
        <text class="confirm-btn__text">{{ submitting ? '提交中...' : '确认充值' }}</text>
      </view>
    </view>

    <ConfirmDialog
      :visible="showDialog"
      :phone="phone"
      @confirm="onDialogConfirm"
      @cancel="showDialog = false"
    />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rechargeApi } from '@/api'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
const phone=ref(''),operatorName=ref(''),productName=ref(''),topupType=ref('AIRTIME'),submitting=ref(false),showDialog=ref(false),product=ref({priceCny:'0',amountBdt:'0',name:''})
onMounted(()=>{const pages=getCurrentPages(),options=pages[pages.length-1].options||{};phone.value=options.phone||'';topupType.value=options.topupType||'AIRTIME';operatorName.value=decodeURIComponent(options.operatorName||'未知运营商');const cached=uni.getStorageSync('pendingProduct');if(cached&&String(cached.id)===String(options.productId)){product.value=cached;productName.value=cached.name||(topupType.value==='AIRTIME'?`话费 ৳${cached.amountBdt}`:'流量套餐')}})
function onConfirm(){if(!submitting.value)showDialog.value=true}
async function onDialogConfirm(){showDialog.value=false;submitting.value=true;try{const response=await rechargeApi.createOrder({phone:phone.value,productId:product.value.id});const order=response.data;uni.removeStorageSync('pendingProduct');uni.redirectTo({url:`/pages/result/result?status=processing&orderNo=${order.orderNo}&phone=${phone.value}&amount=${order.bdtAmount||product.value.amountBdt}&price=${order.cnyPrice||product.value.priceCny}`})}catch(e){uni.showToast({title:e.message||'订单创建失败',icon:'none'})}finally{submitting.value=false}}
</script>

<style lang="scss" scoped>
.confirm-page {
  min-height: 100vh;
  background: #F5F7FA;
  padding: 24rpx;
  padding-bottom: 180rpx;
}

.order-card {
  &__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 0;

    &--total {
      padding-top: 28rpx;
    }
  }

  &__label {
    font-size: 28rpx;
    color: #666666;
  }

  &__value {
    font-size: 28rpx;
    color: #333333;

    &--phone {
      font-size: 36rpx;
      font-weight: 700;
    }

    &--price {
      font-size: 44rpx;
      font-weight: 700;
      color: #003366;
    }
  }

  &__divider {
    height: 1rpx;
    background: #E8E8E8;
    margin: 8rpx 0;
  }
}

.risk-tip {
  margin-top: 24rpx;
  padding: 0 8rpx;

  &__text {
    font-size: 24rpx;
    color: #999999;
    line-height: 1.6;
  }
}

.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 24rpx;
  background: #FFFFFF;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.confirm-btn {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #003366;
  border-radius: 12rpx;

  &--loading {
    background: #999999;
  }

  &__text {
    font-size: 34rpx;
    font-weight: 600;
    color: #FFFFFF;
  }
}
</style>
