<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="title">华人在孟 - 后台管理</h1>
      <a-form :model="form" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="form.username" placeholder="请输入用户名" size="large" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="form.password" placeholder="请输入密码" size="large" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" size="large" block @click="handleLogin">登 录</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({ username: '', password: '' })

const handleLogin = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}
.title {
  text-align: center;
  margin-bottom: 32px;
  color: #333;
  font-size: 24px;
}
</style>
