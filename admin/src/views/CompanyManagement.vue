<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between;">
      <a-button type="primary">+ 新增黄页</a-button>
      <a-input-search placeholder="搜索公司名称" style="width: 300px" />
    </div>
    <a-table :columns="columns" :data-source="mockData">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '已上线' ? 'green' : 'red'">{{ record.status }}</a-tag>
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
  { title: '公司名称', dataIndex: 'name', key: 'name' },
  { title: '行业', dataIndex: 'industry', key: 'industry', width: 120 },
  { title: '联系电话', dataIndex: 'phone', key: 'phone', width: 150 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 150 }
]
const mockData = [
  { key: '1', id: 1, name: '华为孟加拉办事处', industry: '通信', phone: '+880-17-12345678', status: '已上线' },
  { key: '2', id: 2, name: '中孟贸易有限公司', industry: '贸易', phone: '+880-18-87654321', status: '已上线' }
]
</script>
