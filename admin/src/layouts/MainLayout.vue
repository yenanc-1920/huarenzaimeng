<template>
  <a-layout class="admin-layout">
    <a-layout-sider :width="220" :collapsed-width="64" v-model:collapsed="collapsed" theme="dark" class="admin-sider">
      <div class="sider-logo">
        <img src="/logo.svg" alt="华人在孟" class="logo-img" v-if="!collapsed" />
        <span v-if="!collapsed" class="logo-text">华人在孟</span>
        <span v-else class="logo-text-mini">HM</span>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <a-menu-item key="/dashboard">
          <template #icon><DashboardOutlined /></template>
          <span>运营看板</span>
        </a-menu-item>
        <a-menu-item key="/product">
          <template #icon><ShoppingOutlined /></template>
          <span>商品管理</span>
        </a-menu-item>
        <a-menu-item key="/order">
          <template #icon><OrderedListOutlined /></template>
          <span>订单管理</span>
        </a-menu-item>
        <a-menu-item key="/refund">
          <template #icon><RollbackOutlined /></template>
          <span>退款管理</span>
        </a-menu-item>
        <a-menu-item key="/news">
          <template #icon><FileTextOutlined /></template>
          <span>资讯管理</span>
        </a-menu-item>
        <a-menu-item key="/company">
          <template #icon><ShopOutlined /></template>
          <span>黄页管理</span>
        </a-menu-item>
        <a-menu-item key="/holiday">
          <template #icon><CalendarOutlined /></template>
          <span>节假日管理</span>
        </a-menu-item>
        <a-menu-item key="/config">
          <template #icon><SettingOutlined /></template>
          <span>系统配置</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="admin-header">
        <div class="header-left">
          <a-breadcrumb>
            <a-breadcrumb-item>{{ currentTitle }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <span class="admin-name">{{ auth.adminName || '管理员' }}</span>
          <a-button type="link" size="small" @click="handleLogout">退出登录</a-button>
        </div>
      </a-layout-header>

      <a-layout-content class="admin-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  DashboardOutlined,
  ShoppingOutlined,
  OrderedListOutlined,
  RollbackOutlined,
  FileTextOutlined,
  ShopOutlined,
  CalendarOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const collapsed = ref(false)
const selectedKeys = ref(['/dashboard'])

const currentTitle = computed(() => route.meta.title || '运营看板')

watch(() => route.path, (path) => {
  const seg = '/' + (path.split('/').filter(Boolean)[0] || 'dashboard')
  selectedKeys.value = [seg]
}, { immediate: true })

const handleMenuClick = ({ key }) => router.push(key)

const handleLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  min-width: 1200px;
}
.admin-sider {
  background: #001529;
}
.sider-logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.logo-img {
  width: 28px;
  height: 28px;
}
.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}
.logo-text-mini {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}
.admin-header {
  height: 56px;
  line-height: 56px;
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 10;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.admin-name {
  font-size: 14px;
  color: #333;
}
.admin-content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 56px - 48px);
}
</style>
