<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>浏览历史</span>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="searchUsername" placeholder="用户名搜索" clearable style="width:160px"
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width:360px" clearable @change="handleSearch" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input-number v-model="filter.userId" placeholder="输入用户ID" :min="1" controls-position="right" style="width:180px" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="文物ID">
          <el-input-number v-model="filter.artifactId" placeholder="输入文物ID" :min="1" controls-position="right" style="width:180px" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="artifactName" label="文物名称" width="180" show-overflow-tooltip />
        <el-table-column prop="browseTime" label="浏览时间" width="170" />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
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
import { getBrowseHistoryPage } from '../api/userBehaviorAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ userId: undefined, artifactId: undefined })
const searchUsername = ref('')
const dateRange = ref(null)

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getBrowseHistoryPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      userId: filter.userId || undefined,
      artifactId: filter.artifactId || undefined,
      username: searchUsername.value || undefined,
      startTime,
      endTime
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
    pagination.page = res.data.page
    pagination.pageSize = res.data.pageSize
  } catch {
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
  filter.userId = undefined
  filter.artifactId = undefined
  searchUsername.value = ''
  dateRange.value = null
  handleSearch()
}
</script>

<style scoped>
.card-header {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
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