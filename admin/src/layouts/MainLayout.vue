<template>
  <a-layout style="min-height: 100vh">
    <a-layout-sider v-model:collapsed="collapsed" collapsible theme="dark">
      <div class="logo">
        <h1 v-if="!collapsed">华人在孟</h1>
        <h1 v-else>HM</h1>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <a-menu-item key="/dashboard">
          <template #icon><DashboardOutlined /></template>
          <span>仪表盘</span>
        </a-menu-item>
        <a-menu-item key="/order">
          <template #icon><OrderedListOutlined /></template>
          <span>订单管理</span>
        </a-menu-item>
        <a-menu-item key="/news">
          <template #icon><FileTextOutlined /></template>
          <span>资讯管理</span>
        </a-menu-item>
        <a-menu-item key="/company">
          <template #icon><PhoneOutlined /></template>
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
        <a-menu-item key="/admin">
          <template #icon><UserOutlined /></template>
          <span>管理员管理</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header style="background: #fff; padding: 0 24px; display: flex; justify-content: space-between; align-items: center;">
        <h2 style="margin: 0; font-size: 18px;">{{ currentTitle }}</h2>
        <a-button type="link" @click="handleLogout">退出登录</a-button>
      </a-layout-header>

      <a-layout-content style="margin: 24px; padding: 24px; background: #fff; border-radius: 8px; min-height: 280px;">
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
  OrderedListOutlined,
  FileTextOutlined,
  PhoneOutlined,
  CalendarOutlined,
  SettingOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)
const selectedKeys = ref(['/dashboard'])

const currentTitle = computed(() => {
  return route.meta.title || '仪表盘'
})

watch(() => route.path, (newPath) => {
  selectedKeys.value = ['/' + newPath.split('/').filter(Boolean).slice(0, 1).join('/')]
  if (selectedKeys.value[0] === '/') selectedKeys.value = ['/dashboard']
}, { immediate: true })

const handleMenuClick = ({ key }) => {
  router.push(key)
}

const handleLogout = () => {
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
}
.logo h1 {
  color: #fff;
  font-size: 20px;
  margin: 0;
}
</style>
