<template>
  <div>
    <a-card title="系统配置项" style="margin-bottom: 24px;">
      <a-form layout="vertical">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="汇率兜底时间(小时)">
              <a-input-number v-model:value="config.fxRateFallbackHours" :min="1" :max="72" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="Reloadly安全余额阈值(USD)">
              <a-input-number v-model:value="config.balanceThreshold" :min="0" :precision="2" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="单笔最大充值金额(BDT)">
              <a-input-number v-model:value="config.maxSingleAmount" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单日最大累计金额(BDT)">
              <a-input-number v-model:value="config.maxDailyAmount" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="openid日限次数">
              <a-input-number v-model:value="config.dailyLimitOpenid" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="号码日限次数">
              <a-input-number v-model:value="config.dailyLimitPhone" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item>
          <a-button type="primary">保存配置</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
const config = reactive({
  fxRateFallbackHours: 24,
  balanceThreshold: 50.00,
  maxSingleAmount: 500,
  maxDailyAmount: 1000,
  dailyLimitOpenid: 9,
  dailyLimitPhone: 3
})
</script>
