<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>行为日志</span>
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
        <el-form-item label="行为类型">
          <el-select v-model="filter.behaviorType" placeholder="全部" clearable style="width:120px">
            <el-option label="全部" value="" />
            <el-option label="登录" value="login" />
            <el-option label="浏览" value="browse" />
            <el-option label="搜索" value="search" />
            <el-option label="收藏" value="favorite" />
            <el-option label="点赞" value="like" />
            <el-option label="评论" value="comment" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filter.keyword" placeholder="搜索目标描述和详情" clearable style="width:200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="行为类型" width="100">
          <template #default="{ row }">
            <el-tag :type="behaviorTagType(row.behaviorType)" :color="behaviorTagColor(row.behaviorType)" size="small">{{ row.behaviorType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="目标类型" width="100" />
        <el-table-column label="目标描述" min-width="160">
          <template #default="{ row }">
            {{ truncate(row.targetDesc, 40) }}
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="device" label="设备" width="100">
          <template #default="{ row }">
            {{ deviceLabel(row.device) }}
          </template>
        </el-table-column>
        <el-table-column label="详情" min-width="200">
          <template #default="{ row }">
            {{ truncate(row.detail, 50) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="操作时间" width="170" />
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
import { getBehaviorLogsPage } from '../api/userBehaviorAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ userId: undefined, behaviorType: '', keyword: '' })
const searchUsername = ref('')
const dateRange = ref(null)

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getBehaviorLogsPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      userId: filter.userId || undefined,
      behaviorType: filter.behaviorType || undefined,
      keyword: filter.keyword || undefined,
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
  filter.behaviorType = ''
  filter.keyword = ''
  searchUsername.value = ''
  dateRange.value = null
  handleSearch()
}

function truncate(str, max) {
  if (!str) return ''
  return str.length > max ? str.substring(0, max) + '...' : str
}

function behaviorTagType(type) {
  const map = { login: 'success', browse: '', search: 'warning', favorite: 'danger', like: '', comment: 'info' }
  return map[type] || 'info'
}

function behaviorTagColor(type) {
  if (type === 'like') return '#8B5CF6'
  return undefined
}

function deviceLabel(d) {
  if (!d) return ''
  const m = { web: 'Web', ios: 'iOS', android: 'Android', mini: '小程序', h5: 'H5' }
  return m[d] || d
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