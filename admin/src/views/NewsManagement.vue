<template>
  <div class="news-management">
    <div class="page-toolbar">
      <a-button type="primary" @click="openEdit(null)">+ 新增资讯</a-button>
      <a-input-search v-model:value="searchText" placeholder="搜索资讯标题" style="width: 280px" @search="handleSearch" />
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredData"
      :pagination="{ pageSize: 15, showTotal: t => `共 ${t} 条` }"
      row-key="id"
      size="middle"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'cover'">
          <div class="img-status-cell">
            <template v-if="record.imgStatus === 'PASS'">
              <a-image :src="record.cover" :width="60" :height="40" style="object-fit: cover; border-radius: 4px" />
            </template>
            <template v-else-if="record.imgStatus === 'PENDING'">
              <div class="img-placeholder pending">审核中</div>
            </template>
            <template v-else-if="record.imgStatus === 'REJECT'">
              <div class="img-placeholder reject">图片内容违规</div>
            </template>
          </div>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.published ? 'green' : 'default'">{{ record.published ? '已发布' : '草稿' }}</a-tag>
          <a-tag v-if="record.pinned" color="blue">置顶</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-popconfirm title="确认删除该资讯?" @confirm="handleDelete(record)">
              <a-button type="link" size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-drawer v-model:open="editVisible" :title="editForm.id ? '编辑资讯' : '新增资讯'" :width="560" @close="editVisible = false">
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="标题" required>
          <a-input v-model:value="editForm.title" placeholder="请输入资讯标题" />
        </a-form-item>
        <a-form-item label="分类" required>
          <a-select v-model:value="editForm.category" placeholder="选择分类">
            <a-select-option value="公告">公告</a-select-option>
            <a-select-option value="活动">活动</a-select-option>
            <a-select-option value="生活">生活</a-select-option>
            <a-select-option value="政策">政策</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="封面图">
          <a-upload :max-count="1" list-type="picture-card" :before-upload="() => false">
            <div>+ 上传</div>
          </a-upload>
        </a-form-item>
        <a-form-item label="正文">
          <a-textarea v-model:value="editForm.content" :rows="8" placeholder="请输入正文内容" />
        </a-form-item>
        <a-form-item label="发布时间">
          <a-date-picker v-model:value="editForm.publishTime" show-time style="width: 100%" />
        </a-form-item>
        <a-form-item label="是否置顶">
          <a-switch v-model:checked="editForm.pinned" />
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
const editForm = reactive({ id: null, title: '', category: undefined, content: '', publishTime: null, pinned: false })

const columns = [
  { title: '标题', dataIndex: 'title', key: 'title', ellipsis: true },
  { title: '分类', dataIndex: 'category', key: 'category', width: 80 },
  { title: '封面', key: 'cover', width: 90 },
  { title: '状态', key: 'status', width: 130 },
  { title: '发布时间', dataIndex: 'publishTime', key: 'publishTime', width: 160 },
  { title: '操作', key: 'action', width: 120 }
]

const mockData = ref([
  { id: 1, title: '孟加拉国独立日放假通知', category: '公告', published: true, pinned: true, imgStatus: 'PASS', cover: 'https://via.placeholder.com/120x80', publishTime: '2026-07-23 10:00' },
  { id: 2, title: '达卡华人商会活动预告', category: '活动', published: false, pinned: false, imgStatus: 'PENDING', cover: '', publishTime: '2026-07-22 15:30' },
  { id: 3, title: '签证政策更新提醒', category: '政策', published: true, pinned: false, imgStatus: 'REJECT', cover: '', publishTime: '2026-07-20 09:00' }
])

const filteredData = computed(() => {
  if (!searchText.value) return mockData.value
  return mockData.value.filter(n => n.title.includes(searchText.value))
})

const handleSearch = () => {}

const openEdit = (record) => {
  if (record) {
    Object.assign(editForm, { id: record.id, title: record.title, category: record.category, content: '', publishTime: null, pinned: record.pinned })
  } else {
    Object.assign(editForm, { id: null, title: '', category: undefined, content: '', publishTime: null, pinned: false })
  }
  editVisible.value = true
}

const handleSave = () => {
  message.success('保存成功')
  editVisible.value = false
}

const handleDelete = (record) => {
  mockData.value = mockData.value.filter(n => n.id !== record.id)
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
  width: 60px;
  height: 40px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
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
