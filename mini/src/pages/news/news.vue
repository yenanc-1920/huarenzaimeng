<template>
  <view class="page">
    <HmSkeleton v-if="loading" :rows="5" />
    <HmError v-else-if="error" :text="error" @retry="loadNews" />
    <template v-else>
      <view v-for="item in newsList" :key="item.id" class="card" @tap="goDetail(item.id)">
        <image v-if="item.coverUrl" class="cover" :src="item.coverUrl" mode="aspectFill" />
        <view v-else class="cover placeholder" />
        <view class="content">
          <view class="title-row"><text v-if="item.isTop" class="top">置顶</text><text class="title">{{ item.title }}</text></view>
          <text class="summary">{{ item.summary }}</text>
          <text class="time">{{ formatDate(item.publishedAt) }}</text>
        </view>
      </view>
      <HmEmpty v-if="!newsList.length" text="暂无资讯" />
    </template>
  </view>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { contentApi } from '@/api'
import HmEmpty from '@/components/HmEmpty.vue'
import HmError from '@/components/HmError.vue'
import HmSkeleton from '@/components/HmSkeleton.vue'
const newsList = ref([]); const loading = ref(true); const error = ref('')
async function loadNews(){ loading.value=true; error.value=''; try { const res=await contentApi.getNewsList({page:1,size:20}); newsList.value=res.data?.records||[] } catch(e){ error.value=e.message||'资讯加载失败' } finally { loading.value=false } }
function goDetail(id){ uni.navigateTo({url:`/pages/news-detail/news-detail?id=${id}`}) }
function formatDate(value){ return value ? String(value).replace('T',' ').slice(0,16) : '' }
onMounted(loadNews)
</script>
<style lang="scss" scoped>
.page{min-height:100vh;background:#F5F7FA;padding:24rpx}.card{display:flex;gap:20rpx;background:#fff;border-radius:16rpx;padding:24rpx;margin-bottom:16rpx}.cover{width:192rpx;height:128rpx;border-radius:8rpx;flex-shrink:0}.placeholder{background:#E8EDF3}.content{min-width:0;flex:1;display:flex;flex-direction:column;gap:10rpx}.title-row{display:flex;gap:8rpx;align-items:flex-start}.top{flex-shrink:0;font-size:20rpx;color:#fff;background:#FF4D4F;border-radius:4rpx;padding:2rpx 8rpx}.title{font-size:30rpx;font-weight:600;color:#333}.summary{font-size:26rpx;color:#666;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.time{font-size:24rpx;color:#999}
</style>