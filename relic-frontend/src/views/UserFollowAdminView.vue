<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户关注</span>
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
        <el-form-item label="关注者ID">
          <el-input v-model="filter.followerId" placeholder="请输入关注者ID" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="被关注者ID">
          <el-input v-model="filter.followeeId" placeholder="请输入被关注者ID" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border row-key="id">
        <el-table-column prop="followerName" label="关注者" width="120" show-overflow-tooltip />
        <el-table-column prop="followeeName" label="被关注者" width="120" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="关注时间" width="170" />
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
import { getFollowsPage } from '../api/userBehaviorAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ followerId: '', followeeId: '' })
const searchUsername = ref('')
const dateRange = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getFollowsPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      followerId: filter.followerId ? Number(filter.followerId) : undefined,
      followeeId: filter.followeeId ? Number(filter.followeeId) : undefined,
      username: searchUsername.value || undefined,
      startTime,
      endTime
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
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
  filter.followerId = ''
  filter.followeeId = ''
  searchUsername.value = ''
  dateRange.value = null
  handleSearch()
}

onMounted(() => {
  fetchData()
})
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