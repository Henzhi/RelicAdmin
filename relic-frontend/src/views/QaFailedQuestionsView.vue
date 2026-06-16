<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">失败问题</span>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="失败类型">
          <el-select v-model="searchForm.failureType" placeholder="全部" clearable style="width: 160px;">
            <el-option label="未找到数据" value="no_data_found" />
            <el-option label="文物未解析" value="object_not_resolved" />
            <el-option label="意图识别失败" value="intent_not_recognized" />
            <el-option label="系统错误" value="system_error" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已解决" value="resolved" />
          </el-select>
        </el-form-item>
        <el-form-item label="意图">
          <el-input v-model="searchForm.intent" placeholder="意图类型" clearable style="width: 130px;" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="问题关键词" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="qa_log_id" label="日志ID" width="90" />
        <el-table-column prop="session_id" label="会话ID" width="130" show-overflow-tooltip />
        <el-table-column prop="question" label="用户问题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="intent" label="意图" width="130" />
        <el-table-column prop="failure_type" label="失败类型" width="140">
          <template #default="{ row }">
            <el-tag type="danger" size="small">{{ failureTypeLabel(row.failure_type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="object_id" label="关联文物" width="110" show-overflow-tooltip />
        <el-table-column prop="error_detail" label="错误说明" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'resolved'" type="success" size="small">已解决</el-tag>
            <el-tag v-else-if="row.status === 'processing'" type="warning" size="small">处理中</el-tag>
            <el-tag v-else type="info" size="small">待处理</el-tag>
          </template>
        </el-table-column>
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
import { getQaFailedQuestions } from '../api/qa'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const searchForm = ref({ failureType: '', status: '', intent: '', keyword: '' })

const failureTypeMap = {
  no_data_found: '未找到数据',
  object_not_resolved: '文物未解析',
  intent_not_recognized: '意图识别失败',
  system_error: '系统错误'
}

function failureTypeLabel(type) { return failureTypeMap[type] || type }

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.failureType) params.failureType = searchForm.value.failureType
    if (searchForm.value.status) params.status = searchForm.value.status
    if (searchForm.value.intent) params.intent = searchForm.value.intent
    if (searchForm.value.keyword) params.keyword = searchForm.value.keyword
    const res = await getQaFailedQuestions(params)
    const data = res.data
    if (data && data.records) { tableData.value = data.records; total.value = data.total || 0 }
    else if (data && data.items) { tableData.value = data.items; total.value = data.total || 0 }
    else if (Array.isArray(data)) { tableData.value = data; total.value = data.length }
    else { tableData.value = []; total.value = 0 }
  } catch { tableData.value = [] } finally { loading.value = false }
}

function resetSearch() {
  searchForm.value = { failureType: '', status: '', intent: '', keyword: '' }
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
