<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">
        <div class="logo-circle">华</div>
        <h1 class="login-title">华人在孟</h1>
        <p class="login-subtitle">后台管理系统</p>
      </div>
      <a-form :model="form" layout="vertical" @finish="handleLogin">
        <a-form-item name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" placeholder="用户名" size="large" allow-clear>
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="form.password" placeholder="密码" size="large">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            登 录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  loading.value = true
  try {
    // Mock: 后端就绪后替换为真实API
    await new Promise(r => setTimeout(r, 600))
    if (form.username === 'admin' && form.password === 'admin123') {
      auth.setAuth('mock_token_' + Date.now(), form.username)
      message.success('登录成功')
      const redirect = route.query.redirect || '/dashboard'
      router.push(redirect)
    } else {
      message.error('用户名或密码错误')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #003366 0%, #001a33 60%, #000d1a 100%);
}
.login-card {
  width: 380px;
  padding: 48px 40px 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 48px rgba(0,0,0,0.25);
}
.login-logo {
  text-align: center;
  margin-bottom: 36px;
}
.logo-circle {
  width: 56px;
  height: 56px;
  margin: 0 auto 12px;
  background: #003366;
  color: #fff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
}
.login-title {
  font-size: 22px;
  color: #333;
  margin: 0 0 4px;
}
.login-subtitle {
  font-size: 13px;
  color: #999;
  margin: 0;
}
</style>
