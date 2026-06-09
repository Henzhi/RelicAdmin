<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">问答日志</span>
      </template>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
            <el-option label="部分成功" value="partial" />
          </el-select>
        </el-form-item>
        <el-form-item label="意图">
          <el-input v-model="searchForm.intent" placeholder="意图类型" clearable style="width: 140px;" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="问题/回答关键词" clearable style="width: 160px;" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 260px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 数据表格 -->
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="qa_log_uuid" label="日志UUID" width="160" show-overflow-tooltip />
        <el-table-column prop="session_id" label="会话ID" width="140" show-overflow-tooltip />
        <el-table-column prop="question" label="用户问题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="intent" label="意图" width="130" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'success'" type="success" size="small">成功</el-tag>
            <el-tag v-else-if="row.status === 'failed'" type="danger" size="small">失败</el-tag>
            <el-tag v-else type="warning" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answer" label="系统回答" min-width="200" show-overflow-tooltip />
        <el-table-column prop="object_id" label="关联文物" width="110" show-overflow-tooltip />
        <el-table-column prop="source_client" label="来源" width="80" />
        <el-table-column prop="latency_ms" label="耗时(ms)" width="90" align="center" />
        <el-table-column prop="created_at" label="创建时间" width="170" />
      </el-table>
      <div class="pagination-container" v-if="total > 0">
        <el-pagination background v-model:current-page="page" v-model:page-size="pageSize"
          :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getQaLogs } from '../api/qa'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const searchForm = ref({ status: '', intent: '', keyword: '', dateRange: null })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.status) params.status = searchForm.value.status
    if (searchForm.value.intent) params.intent = searchForm.value.intent
    if (searchForm.value.keyword) params.keyword = searchForm.value.keyword
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      params.startTime = searchForm.value.dateRange[0]
      params.endTime = searchForm.value.dateRange[1]
    }
    const res = await getQaLogs(params)
    const data = res.data
    // 兼容不同的返回结构
    if (data && data.records) {
      tableData.value = data.records
      total.value = data.total || 0
    } else if (data && data.items) {
      tableData.value = data.items
      total.value = data.total || 0
    } else if (Array.isArray(data)) {
      tableData.value = data
      total.value = data.length
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('问答日志加载失败', e)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.value = { status: '', intent: '', keyword: '', dateRange: null }
  page.value = 1
  fetchData()
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-title { font-size: 18px; font-weight: 600; }
.search-form { margin-bottom: 16px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
