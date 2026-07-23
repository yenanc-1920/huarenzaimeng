<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between;">
      <a-button type="primary">+ 新增节假日</a-button>
      <a-select placeholder="选择年份" style="width: 120px" defaultValue="2026">
        <a-select-option value="2026">2026年</a-select-option>
        <a-select-option value="2027">2027年</a-select-option>
      </a-select>
    </div>
    <a-table :columns="columns" :data-source="mockData">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'confirmed'">
          <a-tag :color="record.confirmed ? 'green' : 'orange'">{{ record.confirmed ? '已确认' : '待确认' }}</a-tag>
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
  { title: '节日名称', dataIndex: 'name', key: 'name' },
  { title: '日期', dataIndex: 'date', key: 'date', width: 120 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '确认状态', key: 'confirmed', width: 100 },
  { title: '操作', key: 'action', width: 150 }
]
const mockData = [
  { key: '1', id: 1, name: '独立日', date: '2026-03-26', type: '法定假日', confirmed: true },
  { key: '2', id: 2, name: '开斋节', date: '2026-03-21', type: '宗教节日', confirmed: false },
  { key: '3', id: 3, name: '春节', date: '2026-01-29', type: '华人节日', confirmed: true }
]
</script>
