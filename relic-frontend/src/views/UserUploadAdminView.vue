<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户上传</span>
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
          <el-input v-model="filter.userId" placeholder="请输入用户ID" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="媒体类型">
          <el-select v-model="filter.mediaType" placeholder="全部" clearable style="width:120px">
            <el-option label="全部" value="" />
            <el-option label="image" value="image" />
            <el-option label="video" value="video" />
            <el-option label="audio" value="audio" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable style="width:120px">
            <el-option label="全部" value="" />
            <el-option label="approved" value="approved" />
            <el-option label="pending" value="pending" />
            <el-option label="rejected" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border row-key="id">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="100" show-overflow-tooltip />
        <el-table-column prop="artifactName" label="文物名称" width="140" show-overflow-tooltip />
        <el-table-column label="媒体类型" width="90">
          <template #default="{ row }">
            <el-tag :type="mediaTypeTag(row.mediaType)" size="small">{{ row.mediaType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ truncateCaption(row.caption) }}
          </template>
        </el-table-column>
        <el-table-column prop="locationTaken" label="拍摄地点" width="120" show-overflow-tooltip />
        <el-table-column prop="likeCount" label="点赞数" width="80" align="center" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="170" />
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
import { getUploadsPage } from '../api/userBehaviorAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ userId: '', mediaType: '', status: '' })
const searchUsername = ref('')
const dateRange = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getUploadsPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      userId: filter.userId ? Number(filter.userId) : undefined,
      mediaType: filter.mediaType || undefined,
      status: filter.status || undefined,
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
  filter.userId = ''
  filter.mediaType = ''
  filter.status = ''
  searchUsername.value = ''
  dateRange.value = null
  handleSearch()
}

function mediaTypeTag(type) {
  if (type === 'image') return ''
  if (type === 'video') return 'danger'
  if (type === 'audio') return 'warning'
  return 'info'
}

function statusTag(status) {
  if (status === 'approved') return 'success'
  if (status === 'pending') return 'warning'
  if (status === 'rejected') return 'danger'
  return 'info'
}

function statusLabel(status) {
  if (status === 'approved') return '已通过'
  if (status === 'pending') return '待审核'
  if (status === 'rejected') return '已驳回'
  return status || '未知'
}

function truncateCaption(caption) {
  if (!caption) return ''
  return caption.length > 50 ? caption.slice(0, 50) + '...' : caption
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