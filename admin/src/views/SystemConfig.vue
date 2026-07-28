<template>
  <div class="system-config">
    <a-card title="基础配置" size="small" style="margin-bottom: 24px">
      <a-form layout="vertical">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="Reloadly API Key">
              <a-input-password v-model:value="config.reloadlyApiKey" placeholder="sk-xxxx" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="Reloadly API Secret">
              <a-input-password v-model:value="config.reloadlyApiSecret" placeholder="xxxx" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="微信支付商户号">
              <a-input v-model:value="config.wechatMchId" placeholder="mch_id" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="微信支付API密钥">
              <a-input-password v-model:value="config.wechatApiKey" placeholder="API v3 密钥" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="汇率同步周期(小时)">
              <a-input-number v-model:value="config.fxSyncInterval" :min="1" :max="24" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="损耗率(%)">
              <a-input-number v-model:value="config.lossRate" :min="0" :max="10" :precision="2" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="默认利润率(%)">
              <a-input-number v-model:value="config.profitRate" :min="0" :max="50" :precision="2" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <a-card title="风控配置" size="small" style="margin-bottom: 24px">
      <a-form layout="vertical">
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="充值中超时阈值(分钟)">
              <a-input-number v-model:value="config.rechargeTimeout" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="二次超时时间(分钟)">
              <a-input-number v-model:value="config.secondTimeout" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="轮询间隔(秒)">
              <a-input-number v-model:value="config.pollInterval" :min="5" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="轮询总生命周期(分钟)">
              <a-input-number v-model:value="config.pollLifetime" :min="10" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="退款异常熔断阈值(次)">
              <a-input-number v-model:value="config.refundCircuitBreaker" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="MNP连续超时降级阈值(次)">
              <a-input-number v-model:value="config.mnpDegradeThreshold" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="单笔充值上限(CNY)">
              <a-input-number v-model:value="config.maxSingleAmount" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="openid日限次数">
              <a-input-number v-model:value="config.dailyLimitOpenid" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="手机号日限次数">
              <a-input-number v-model:value="config.dailyLimitPhone" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-alert message="设备指纹/IP频率限制为 V1.1 预留功能，当前版本暂不启用" type="info" show-icon style="margin-top: 8px" />
      </a-form>
    </a-card>

    <a-card title="客服配置" size="small" style="margin-bottom: 24px">
      <a-form layout="vertical">
        <a-form-item label="微信小程序在线客服入口">
          <a-radio-group v-model:value="config.customerServiceEnabled">
            <a-radio :value="true">启用</a-radio>
            <a-radio :value="false">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-card>

    <div class="save-bar">
      <a-button type="primary" size="large" :loading="saving" @click="handleSave">保存配置</a-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'

const saving = ref(false)

const config = reactive({
  reloadlyApiKey: '',
  reloadlyApiSecret: '',
  wechatMchId: '',
  wechatApiKey: '',
  fxSyncInterval: 12,
  lossRate: 1.5,
  profitRate: 8.0,
  rechargeTimeout: 30,
  secondTimeout: 120,
  pollInterval: 30,
  pollLifetime: 120,
  refundCircuitBreaker: 5,
  mnpDegradeThreshold: 3,
  maxSingleAmount: 500,
  dailyLimitOpenid: 9,
  dailyLimitPhone: 5,
  customerServiceEnabled: true
})

const handleSave = () => {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    message.success('配置已保存')
  }, 800)
}
</script>

<style scoped>
.save-bar {
  text-align: right;
  padding-top: 8px;
}
</style>
