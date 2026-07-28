<template>
  <div class="company-management">
    <div class="page-toolbar">
      <a-button type="primary" @click="openEdit(null)">+ 新增企业</a-button>
      <a-input-search v-model:value="searchText" placeholder="搜索企业名称" style="width: 280px" @search="handleSearch" />
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredData"
      :pagination="{ pageSize: 15, showTotal: t => `共 ${t} 条` }"
      row-key="id"
      size="middle"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'logo'">
          <div class="img-status-cell">
            <template v-if="record.imgStatus === 'PASS'">
              <a-avatar :src="record.logo" shape="square" :size="40" />
            </template>
            <template v-else-if="record.imgStatus === 'PENDING'">
              <div class="img-placeholder pending">审核中</div>
            </template>
            <template v-else-if="record.imgStatus === 'REJECT'">
              <div class="img-placeholder reject">违规</div>
            </template>
          </div>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.online ? 'green' : 'default'">{{ record.online ? '已上线' : '已下线' }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-popconfirm title="确认删除该企业?" @confirm="handleDelete(record)">
              <a-button type="link" size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-drawer v-model:open="editVisible" :title="editForm.id ? '编辑企业' : '新增企业'" :width="560" @close="editVisible = false">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="企业名称" required>
          <a-input v-model:value="editForm.name" placeholder="请输入企业名称" />
        </a-form-item>
        <a-form-item label="Logo">
          <a-upload :max-count="1" list-type="picture-card" :before-upload="() => false">
            <div>+ 上传</div>
          </a-upload>
        </a-form-item>
        <a-form-item label="分类" required>
          <a-select v-model:value="editForm.category" placeholder="选择分类">
            <a-select-option value="餐饮">餐饮</a-select-option>
            <a-select-option value="贸易">贸易</a-select-option>
            <a-select-option value="服务">服务</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="editForm.intro" :rows="3" placeholder="企业简介" />
        </a-form-item>
        <a-form-item label="地址">
          <a-input v-model:value="editForm.address" placeholder="企业地址" />
        </a-form-item>
        <a-form-item label="联系方式">
          <a-input v-model:value="editForm.phone" placeholder="联系电话" />
        </a-form-item>
        <a-form-item label="营业信息">
          <a-input v-model:value="editForm.businessHours" placeholder="如：周一至周六 9:00-18:00" />
        </a-form-item>
      </a-form>
      <template #footer>
        <a-space>
          <a-button @click="editVisible = false">取消</a-button>
          <a-button type="primary" @click="handleSave">保存</a-button>
        </a-space>
      </template>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'

const searchText = ref('')
const editVisible = ref(false)
const editForm = reactive({ id: null, name: '', category: undefined, intro: '', address: '', phone: '', businessHours: '' })

const columns = [
  { title: '企业名称', dataIndex: 'name', key: 'name', ellipsis: true },
  { title: 'Logo', key: 'logo', width: 70 },
  { title: '分类', dataIndex: 'category', key: 'category', width: 80 },
  { title: '联系电话', dataIndex: 'phone', key: 'phone', width: 150 },
  { title: '状态', key: 'status', width: 90 },
  { title: '操作', key: 'action', width: 120 }
]

const mockData = ref([
  { id: 1, name: '华为孟加拉办事处', category: '通信', phone: '+880-17-12345678', online: true, imgStatus: 'PASS', logo: 'https://via.placeholder.com/40' },
  { id: 2, name: '中孟贸易有限公司', category: '贸易', phone: '+880-18-87654321', online: true, imgStatus: 'PENDING', logo: '' },
  { id: 3, name: '达卡中餐馆', category: '餐饮', phone: '+880-19-11223344', online: false, imgStatus: 'REJECT', logo: '' }
])

const filteredData = computed(() => {
  if (!searchText.value) return mockData.value
  return mockData.value.filter(c => c.name.includes(searchText.value))
})

const handleSearch = () => {}

const openEdit = (record) => {
  if (record) {
    Object.assign(editForm, { id: record.id, name: record.name, category: record.category, intro: '', address: '', phone: record.phone, businessHours: '' })
  } else {
    Object.assign(editForm, { id: null, name: '', category: undefined, intro: '', address: '', phone: '', businessHours: '' })
  }
  editVisible.value = true
}

const handleSave = () => {
  message.success('保存成功')
  editVisible.value = false
}

const handleDelete = (record) => {
  mockData.value = mockData.value.filter(c => c.id !== record.id)
  message.success('已删除')
}
</script>

<style scoped>
.page-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
.img-status-cell {
  display: flex;
  align-items: center;
}
.img-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 9px;
}
.img-placeholder.pending {
  background: #F5F5F5;
  color: #999;
}
.img-placeholder.reject {
  background: #FFF2F0;
  color: #FF4D4F;
}
</style>
