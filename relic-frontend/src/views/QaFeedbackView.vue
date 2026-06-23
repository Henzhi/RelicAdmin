<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">用户反馈</span>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="反馈类型">
          <el-select v-model="searchForm.feedbackType" placeholder="全部" clearable style="width: 130px;">
            <el-option label="有帮助" value="helpful" />
            <el-option label="不准确" value="inaccurate" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="反馈说明搜索" clearable style="width: 160px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="qa_log_id" label="问答日志ID" width="110" />
        <el-table-column prop="user_id" label="用户ID" width="90" />
        <el-table-column prop="feedback_type" label="反馈类型" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.feedback_type === 'helpful'" type="success" size="small">有帮助</el-tag>
            <el-tag v-else-if="row.feedback_type === 'inaccurate'" type="danger" size="small">不准确</el-tag>
            <el-tag v-else size="small">{{ row.feedback_type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="补充说明" min-width="200" show-overflow-tooltip />
        <el-table-column prop="source_client" label="来源" width="80" />
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
import { getQaFeedback } from '../api/qa'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const searchForm = ref({ feedbackType: '', keyword: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.feedbackType) params.feedbackType = searchForm.value.feedbackType
    if (searchForm.value.keyword) params.keyword = searchForm.value.keyword
    const res = await getQaFeedback(params)
    const data = res.data
    if (data && data.records) { tableData.value = data.records; total.value = data.total || 0 }
    else if (data && data.items) { tableData.value = data.items; total.value = data.total || 0 }
    else if (Array.isArray(data)) { tableData.value = data; total.value = data.length }
    else { tableData.value = []; total.value = 0 }
  } catch { tableData.value = [] } finally { loading.value = false }
}

function resetSearch() {
  searchForm.value = { feedbackType: '', keyword: '' }
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
