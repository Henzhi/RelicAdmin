<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户收藏</span>
          <el-tag type="info">共 {{ pagination.total }} 条</el-tag>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchUsername" placeholder="用户名搜索" clearable style="width:160px" @keyup.enter="handleSearch" />
        <el-input v-model="searchArtifactName" placeholder="按文物名称筛选" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width:360px" clearable @change="handleSearch" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无收藏数据" />
        </div>

        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="username" label="用户名" min-width="150" show-overflow-tooltip />
          <el-table-column prop="artifactName" label="文物名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="groupName" label="分组" width="150">
            <template #default="{ row }">
              <el-tag v-if="row.groupName" type="success" size="small">{{ row.groupName }}</el-tag>
              <span v-else class="text-muted">未分组</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="收藏时间" width="180" />
        </el-table>
      </div>

      <div class="pagination-container" v-if="tableData.length > 0">
        <el-pagination
          background
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getFavoritesPage } from '../api/userBehaviorAdmin'

const loading = ref(false)
const tableData = ref([])
const searchUsername = ref('')
const searchArtifactName = ref('')
const dateRange = ref(null)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const startTime = dateRange.value?.[0] ? dateRange.value[0] : undefined
    const endTime = dateRange.value?.[1] ? dateRange.value[1] : undefined
    const res = await getFavoritesPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      username: searchUsername.value || undefined,
      artifactName: searchArtifactName.value || undefined,
      startTime,
      endTime
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('加载用户收藏列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchUsername.value = ''
  searchArtifactName.value = ''
  dateRange.value = null
  handleSearch()
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 0; gap: 16px; }
.text-muted { color: #999; }
</style>