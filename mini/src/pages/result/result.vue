<template>
  <view class="result-page">
    <!-- 成功 -->
    <view v-if="status === 'success'" class="result-status">
      <view class="result-status__icon result-status__icon--success">
        <text class="result-status__emoji">✓</text>
      </view>
      <text class="result-status__title">充值成功</text>
      <text class="result-status__desc hm-mono">৳ {{ amount }} 已到账</text>
      <text class="result-status__price hm-mono">支付 ¥ {{ price }}</text>
      <view class="result-status__btn" @tap="goHome">
        <text class="result-status__btn-text">完成</text>
      </view>
    </view>

    <!-- 充值中 -->
    <view v-else-if="status === 'processing'" class="result-status">
      <view class="result-status__icon result-status__icon--processing">
        <text class="result-status__emoji result-status__emoji--spin">⟳</text>
      </view>
      <text class="result-status__title">充值处理中</text>
      <text class="result-status__desc">系统正在为您充值，请耐心等待</text>
    </view>

    <!-- 异常 -->
    <view v-else class="result-status">
      <view class="result-status__icon result-status__icon--error">
        <text class="result-status__emoji">!</text>
      </view>
      <text class="result-status__title">充值异常，请联系客服</text>
      <view class="result-status__btn result-status__btn--outline" @tap="contactService">
        <text class="result-status__btn-text result-status__btn-text--outline">联系客服</text>
      </view>
    </view>

    <!-- 游客引导卡片 -->
    <view v-if="isVisitor && status !== 'success'" class="guide-card">
      <text class="guide-card__icon">💡</text>
      <text class="guide-card__text">当前为游客身份，建议绑定手机号以永久保存此订单</text>
      <text class="guide-card__action" @tap="goBind">去绑定</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { rechargeApi } from '@/api'
import { useUserStore } from '@/stores/user'
const status=ref('processing'),amount=ref(''),price=ref(''),orderNo=ref(''),isVisitor=ref(true),timer=ref(null),attempts=ref(0);const userStore=useUserStore()
function displayStatus(orderStatus){if(['SUCCESS','SUCCESSFUL'].includes(orderStatus))return'success';if(['FAILED','CANCELLED','REFUNDED'].includes(orderStatus))return'error';return'processing'}
async function refreshOrder(){if(!orderNo.value||attempts.value>=30)return;attempts.value+=1;try{const r=await rechargeApi.getOrderStatus(orderNo.value),order=r.data;status.value=displayStatus(order.orderStatus);amount.value=order.bdtAmount||amount.value;price.value=order.cnyPrice||price.value;if(status.value==='processing')timer.value=setTimeout(refreshOrder,2000)}catch(e){if(attempts.value<30)timer.value=setTimeout(refreshOrder,3000)}}
onMounted(()=>{const pages=getCurrentPages(),options=pages[pages.length-1].options||{};status.value=options.status||'processing';orderNo.value=options.orderNo||'';amount.value=options.amount||'';price.value=options.price||'';isVisitor.value=userStore.isVisitor;if(orderNo.value)refreshOrder()})
onUnmounted(()=>{if(timer.value)clearTimeout(timer.value)})
function goHome(){uni.switchTab({url:'/pages/index/index'})}function contactService(){uni.showToast({title:'请通过小程序客服联系我们',icon:'none'})}function goBind(){uni.switchTab({url:'/pages/my/my'})}
</script>

<style lang="scss" scoped>
.result-page {
  min-height: 100vh;
  background: #F5F7FA;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 48rpx 48rpx;
}

.result-status {
  display: flex;
  flex-direction: column;
  align-items: center;

  &__icon {
    width: 128rpx;
    height: 128rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 32rpx;

    &--success {
      background: #F6FFED;
    }

    &--processing {
      background: #FFFBE6;
    }

    &--error {
      background: #FFF2F0;
    }
  }

  &__emoji {
    font-size: 64rpx;
    font-weight: bold;

    &--spin {
      animation: spin 1.5s linear infinite;
    }
  }

  &__icon--success .result-status__emoji {
    color: #52C41A;
  }

  &__icon--processing .result-status__emoji {
    color: #FAAD14;
  }

  &__icon--error .result-status__emoji {
    color: #FF4D4F;
  }

  &__title {
    font-size: 40rpx;
    font-weight: 700;
    color: #333333;
    margin-bottom: 16rpx;
  }

  &__desc {
    font-size: 28rpx;
    color: #666666;
    margin-bottom: 8rpx;
  }

  &__price {
    font-size: 28rpx;
    color: #999999;
    margin-bottom: 48rpx;
  }

  &__btn {
    margin-top: 32rpx;
    padding: 24rpx 96rpx;
    background: #003366;
    border-radius: 12rpx;

    &--outline {
      background: transparent;
      border: 2rpx solid #003366;
    }
  }

  &__btn-text {
    font-size: 32rpx;
    font-weight: 600;
    color: #FFFFFF;

    &--outline {
      color: #003366;
    }
  }
}

.guide-card {
  margin-top: 64rpx;
  width: 100%;
  display: flex;
  align-items: center;
  background: #FFFBE6;
  border-radius: 12rpx;
  padding: 24rpx;
  gap: 12rpx;

  &__icon {
    font-size: 32rpx;
    flex-shrink: 0;
  }

  &__text {
    flex: 1;
    font-size: 26rpx;
    color: #666666;
    line-height: 1.5;
  }

  &__action {
    flex-shrink: 0;
    font-size: 28rpx;
    color: #003366;
    font-weight: 500;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
