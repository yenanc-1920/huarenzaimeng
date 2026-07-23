<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between;">
      <a-button type="primary">+ 新增管理员</a-button>
    </div>
    <a-table :columns="columns" :data-source="mockData">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-switch :checked="record.status === 'active'" checked-children="启用" un-checked-children="禁用" />
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small">编辑</a-button>
            <a-button type="link" size="small">重置密码</a-button>
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
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '角色', dataIndex: 'role', key: 'role', width: 120 },
  { title: '状态', key: 'status', width: 100 },
  { title: '最后登录', dataIndex: 'lastLogin', key: 'lastLogin', width: 180 },
  { title: '操作', key: 'action', width: 220 }
]
const mockData = [
  { key: '1', id: 1, username: 'admin', role: '超级管理员', status: 'active', lastLogin: '2026-07-23 18:00' },
  { key: '2', id: 2, username: 'operator', role: '运营人员', status: 'active', lastLogin: '2026-07-23 10:30' }
]
</script>
