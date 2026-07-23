import { createRouter, createWebHashHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', hideLayout: true }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'DashboardOutlined' }
      },
      {
        path: 'news',
        name: 'NewsManagement',
        component: () => import('../views/NewsManagement.vue'),
        meta: { title: '资讯管理', icon: 'FileTextOutlined' }
      },
      {
        path: 'company',
        name: 'CompanyManagement',
        component: () => import('../views/CompanyManagement.vue'),
        meta: { title: '黄页管理', icon: 'PhoneOutlined' }
      },
      {
        path: 'holiday',
        name: 'HolidayManagement',
        component: () => import('../views/HolidayManagement.vue'),
        meta: { title: '节假日管理', icon: 'CalendarOutlined' }
      },
      {
        path: 'order',
        name: 'OrderManagement',
        component: () => import('../views/OrderManagement.vue'),
        meta: { title: '订单管理', icon: 'OrderedListOutlined' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('../views/SystemConfig.vue'),
        meta: { title: '系统配置', icon: 'SettingOutlined' }
      },
      {
        path: 'admin',
        name: 'AdminManagement',
        component: () => import('../views/AdminManagement.vue'),
        meta: { title: '管理员管理', icon: 'UserOutlined' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
