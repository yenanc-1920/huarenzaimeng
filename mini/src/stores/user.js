import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const openid = ref(uni.getStorageSync('openid') || '')
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(null)
  const userType = ref(uni.getStorageSync('userType') || 'VISITOR')

  const isLoggedIn = computed(() => userType.value === 'MEMBER')
  const isVisitor = computed(() => userType.value === 'VISITOR')

  function setLogin(data) {
    openid.value = data.openid || ''
    token.value = data.token || ''
    userType.value = data.userType || 'VISITOR'
    userInfo.value = data.userInfo || null
    uni.setStorageSync('openid', openid.value)
    uni.setStorageSync('token', token.value)
    uni.setStorageSync('userType', userType.value)
  }

  function logout() {
    token.value = ''
    userType.value = 'VISITOR'
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.setStorageSync('userType', 'VISITOR')
  }

  return { openid, token, userInfo, userType, isLoggedIn, isVisitor, setLogin, logout }
})
