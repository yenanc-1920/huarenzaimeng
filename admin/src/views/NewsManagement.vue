<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between;">
      <a-button type="primary">+ 新增资讯</a-button>
      <a-input-search placeholder="搜索资讯标题" style="width: 300px" />
    </div>
    <a-table :columns="columns" :data-source="mockData">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '已发布' ? 'green' : 'default'">{{ record.status }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small">编辑</a-button>
            <a-popconfirm title="确认删除?"><a-button type="link" size="small" danger>删除</a-button></a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '分类', dataIndex: 'category', key: 'category', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 },
  { title: '操作', key: 'action', width: 150 }
]
const mockData = [
  { key: '1', id: 1, title: '孟加拉国独立日放假通知', category: '公告', status: '已发布', updateTime: '2026-07-23 10:00' },
  { key: '2', id: 2, title: '达卡华人商会活动预告', category: '活动', status: '草稿', updateTime: '2026-07-22 15:30' }
]
</script>
