<template>
  <a-config-provider :theme="themeConfig">
    <router-view />
  </a-config-provider>
</template>

<script setup>
const themeConfig = {
  token: {
    colorPrimary: '#003366',
    colorLink: '#003366',
    colorSuccess: '#52C41A',
    colorWarning: '#FA8C16',
    colorError: '#FF4D4F',
    colorInfo: '#1890FF',
    borderRadius: 6,
    fontFamily: "-apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', sans-serif"
  }
}
</script>

<style>
body {
  margin: 0;
  background: #F5F7FA;
}
</style>
