import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const openid = ref(uni.getStorageSync('openid') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') || null)
  const userType = ref(uni.getStorageSync('userType') || 'VISITOR')
  const loginReady = ref(false)
  const loginLoading = ref(false)

  const isLoggedIn = computed(() => userType.value === 'MEMBER')
  const isVisitor = computed(() => userType.value === 'VISITOR')

  function setLogin(data) {
    const user = data.user || data
    openid.value = user.openid || ''
    userType.value = user.userType || 'VISITOR'
    userInfo.value = user
    uni.setStorageSync('openid', openid.value)
    uni.setStorageSync('userType', userType.value)
    uni.setStorageSync('userInfo', userInfo.value)
  }

  async function silentLogin() {
    if (loginLoading.value) return
    loginLoading.value = true
    try {
      const loginResult = await new Promise((resolve, reject) => {
        uni.login({ provider: 'weixin', success: resolve, fail: reject })
      })
      const response = await userApi.login(loginResult.code)
      setLogin(response.data)
      return response.data
    } finally {
      loginLoading.value = false
      loginReady.value = true
    }
  }

  function logout() {
    userType.value = 'VISITOR'
    userInfo.value = userInfo.value ? { ...userInfo.value, userType: 'VISITOR' } : null
    uni.setStorageSync('userType', 'VISITOR')
    uni.setStorageSync('userInfo', userInfo.value)
  }

  return {
    openid,
    userInfo,
    userType,
    loginReady,
    loginLoading,
    isLoggedIn,
    isVisitor,
    setLogin,
    silentLogin,
    logout
  }
})