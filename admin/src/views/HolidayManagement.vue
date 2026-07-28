<template>
  <div class="holiday-management">
    <div class="page-toolbar">
      <a-button type="primary" @click="openEdit(null)">+ 新增节假日</a-button>
      <a-select v-model:value="filterCountry" placeholder="全部国家" allow-clear style="width: 140px">
        <a-select-option value="BD">孟加拉</a-select-option>
        <a-select-option value="CN">中国</a-select-option>
      </a-select>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredData"
      :pagination="{ pageSize: 20, showTotal: t => `共 ${t} 条` }"
      row-key="id"
      size="middle"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'country'">
          <a-tag :color="record.country === 'BD' ? 'green' : 'red'">
            {{ record.country === 'BD' ? '孟加拉' : '中国' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.confirmed ? 'green' : 'orange'">
            {{ record.confirmed ? '已确认' : '待观月确认' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-popconfirm title="确认删除?" @confirm="handleDelete(record)">
              <a-button type="link" size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="editVisible" :title="editForm.id ? '编辑节假日' : '新增节假日'" @ok="handleSave" ok-text="保存" cancel-text="取消">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="日期" required>
          <a-date-picker v-model:value="editForm.date" style="width: 100%" />
        </a-form-item>
        <a-form-item label="国家" required>
          <a-select v-model:value="editForm.country" placeholder="选择国家">
            <a-select-option value="BD">孟加拉</a-select-option>
            <a-select-option value="CN">中国</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="节假日名称" required>
          <a-input v-model:value="editForm.name" placeholder="请输入名称" />
        </a-form-item>
        <a-form-item label="备注">
          <a-input v-model:value="editForm.remark" placeholder="可选备注" />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="editForm.confirmed">
            <a-radio :value="true">已确认</a-radio>
            <a-radio :value="false">待观月确认</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'

const filterCountry = ref(undefined)
const editVisible = ref(false)
const editForm = reactive({ id: null, date: null, country: undefined, name: '', remark: '', confirmed: true })

const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 120 },
  { title: '国家', key: 'country', width: 90 },
  { title: '节假日名称', dataIndex: 'name', key: 'name' },
  { title: '备注', dataIndex: 'remark', key: 'remark', ellipsis: true },
  { title: '状态', key: 'status', width: 110 },
  { title: '操作', key: 'action', width: 120 }
]

const mockData = ref([
  { id: 1, date: '2026-03-26', country: 'BD', name: '独立日', remark: '全国放假', confirmed: true },
  { id: 2, date: '2026-03-21', country: 'BD', name: '开斋节', remark: '日期视月相调整', confirmed: false },
  { id: 3, date: '2026-01-29', country: 'CN', name: '春节', remark: '', confirmed: true },
  { id: 4, date: '2026-12-16', country: 'BD', name: '胜利日', remark: '', confirmed: true },
  { id: 5, date: '2026-06-07', country: 'BD', name: '宰牲节', remark: '日期视月相调整', confirmed: false }
])

const filteredData = computed(() => {
  if (!filterCountry.value) return mockData.value
  return mockData.value.filter(h => h.country === filterCountry.value)
})

const openEdit = (record) => {
  if (record) {
    Object.assign(editForm, { id: record.id, date: null, country: record.country, name: record.name, remark: record.remark, confirmed: record.confirmed })
  } else {
    Object.assign(editForm, { id: null, date: null, country: undefined, name: '', remark: '', confirmed: true })
  }
  editVisible.value = true
}

const handleSave = () => {
  message.success('保存成功')
  editVisible.value = false
}

const handleDelete = (record) => {
  mockData.value = mockData.value.filter(h => h.id !== record.id)
  message.success('已删除')
}
</script>

<style scoped>
.page-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
