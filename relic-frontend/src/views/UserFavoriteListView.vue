<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">我的收藏</span>
          <el-tag type="info">共 {{ pagination.total }} 件</el-tag>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchGroup" placeholder="按分组筛选" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无收藏数据" />
          <el-button type="primary" @click="$router.push('/artifacts')">去浏览文物</el-button>
        </div>

        <el-table v-else :data="tableData" border stripe row-key="artifactId" style="width: 100%">
          <el-table-column label="预览" width="100">
            <template #default="{ row }">
              <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 60px; height: 60px" fit="cover" :preview-src-list="[row.imageUrl]" />
              <el-icon v-else :size="40" color="#ccc"><Picture /></el-icon>
            </template>
          </el-table-column>
          <el-table-column prop="artifactId" label="文物ID" width="80" />
          <el-table-column prop="titleZh" label="中文名" min-width="150" show-overflow-tooltip />
          <el-table-column prop="titleEn" label="英文名" min-width="180" show-overflow-tooltip />
          <el-table-column prop="groupName" label="分组" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.groupName" type="success" size="small">{{ row.groupName }}</el-tag>
              <span v-else class="text-muted">未分组</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="收藏时间" width="170" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" size="small" :icon="Delete" link @click="handleRemove(row)">取消收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container" v-if="tableData.length > 0">
        <el-pagination
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Picture } from '@element-plus/icons-vue'
import { getFavoriteList, removeFavorite } from '@/api/favorite'

const loading = ref(false)
const tableData = ref([])
const searchGroup = ref('')
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getFavoriteList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      groupName: searchGroup.value || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('加载收藏列表失败：请确保已登录用户账号')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchGroup.value = ''
  handleSearch()
}

async function handleRemove(row) {
  try {
    await ElMessageBox.confirm('确定取消收藏该文物？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await removeFavorite(row.artifactId)
    ElMessage.success('已取消收藏')
    if (tableData.value.length === 1 && pagination.page > 1) pagination.page--
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '取消收藏失败')
    }
  }
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