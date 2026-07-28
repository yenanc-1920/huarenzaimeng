import { createRouter, createWebHashHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', public: true }
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
        meta: { title: '运营看板', icon: 'DashboardOutlined' }
      },
      {
        path: 'product',
        name: 'ProductManagement',
        component: () => import('../views/ProductManagement.vue'),
        meta: { title: '商品管理', icon: 'ShoppingOutlined' }
      },
      {
        path: 'order',
        name: 'OrderManagement',
        component: () => import('../views/OrderManagement.vue'),
        meta: { title: '订单管理', icon: 'OrderedListOutlined' }
      },
      {
        path: 'refund',
        name: 'RefundManagement',
        component: () => import('../views/RefundManagement.vue'),
        meta: { title: '退款管理', icon: 'RollbackOutlined' }
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
        meta: { title: '黄页管理', icon: 'ShopOutlined' }
      },
      {
        path: 'holiday',
        name: 'HolidayManagement',
        component: () => import('../views/HolidayManagement.vue'),
        meta: { title: '节假日管理', icon: 'CalendarOutlined' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('../views/SystemConfig.vue'),
        meta: { title: '系统配置', icon: 'SettingOutlined' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && auth.isLoggedIn) {
    return { path: '/dashboard' }
  }
})

export default router
