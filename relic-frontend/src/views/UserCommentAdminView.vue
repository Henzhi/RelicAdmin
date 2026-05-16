<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论模块</span>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="searchUsername" placeholder="用户名搜索" clearable style="width:160px"
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filter.keyword" placeholder="搜索评论内容" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width:360px" clearable @change="handleSearch" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="filter.userId" placeholder="用户ID" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="文物ID">
          <el-input v-model="filter.artifactId" placeholder="文物ID" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable style="width:120px">
            <el-option label="正常" value="active" />
            <el-option label="已删除" value="deleted" />
            <el-option label="待审核" value="pending" />
            <el-option label="已隐藏" value="hidden" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="artifactName" label="文物名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="评论内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ truncateContent(row.content) }}
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="90" align="center" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="评论时间" width="170" />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          @size-change="handleSearch"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCommentsPage } from '../api/userBehaviorAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ keyword: '', userId: '', artifactId: '', status: '' })
const searchUsername = ref('')
const dateRange = ref(null)

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getCommentsPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filter.keyword || undefined,
      userId: filter.userId || undefined,
      artifactId: filter.artifactId || undefined,
      status: filter.status || undefined,
      username: searchUsername.value || undefined,
      startTime,
      endTime
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
    pagination.page = res.data.page
    pagination.pageSize = res.data.pageSize
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handlePageChange(page) {
  pagination.page = page
  fetchData()
}

function resetFilter() {
  filter.keyword = ''
  filter.userId = ''
  filter.artifactId = ''
  filter.status = ''
  searchUsername.value = ''
  dateRange.value = null
  handleSearch()
}

function truncateContent(content) {
  if (!content) return ''
  return content.length > 60 ? content.slice(0, 60) + '...' : content
}

function statusType(status) {
  const map = { active: 'success', deleted: 'danger', pending: 'warning', hidden: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { active: '正常', deleted: '已删除', pending: '待审核', hidden: '已隐藏' }
  return map[status] || status
}
</script>

<style scoped>
.card-header {
  font-weight: bold;
}
.filter-form {
  margin-bottom: 16px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>