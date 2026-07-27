import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('hm_admin_token') || '')
  const adminName = ref(localStorage.getItem('hm_admin_name') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(newToken, name) {
    token.value = newToken
    adminName.value = name
    localStorage.setItem('hm_admin_token', newToken)
    localStorage.setItem('hm_admin_name', name)
  }

  function logout() {
    token.value = ''
    adminName.value = ''
    localStorage.removeItem('hm_admin_token')
    localStorage.removeItem('hm_admin_name')
  }

  return { token, adminName, isLoggedIn, setAuth, logout }
})
