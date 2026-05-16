<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户收藏管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户ID">
          <el-input v-model="filter.userId" placeholder="输入用户ID" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column label="ID" type="index" width="60" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="artifactId" label="文物ID" width="100" />
        <el-table-column prop="groupName" label="分组" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.groupName" type="success" size="small">{{ row.groupName }}</el-tag>
            <span v-else style="color:#999">未分组</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="收藏时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定要取消收藏吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger">取消收藏</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]" :total="pagination.total"
          layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handlePageChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavoritePage, deleteFavorite } from '../api/favoriteAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ userId: '' })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getFavoritePage({
      page: pagination.page, pageSize: pagination.pageSize,
      userId: filter.userId || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载收藏列表失败') }
  finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }
function resetFilter() { filter.userId = ''; handleSearch() }

async function handleDelete(row) {
  try { await deleteFavorite(row.userId, row.artifactId); ElMessage.success('已取消收藏'); fetchData() }
  catch { ElMessage.error('删除失败') }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.filter-form { margin-bottom: 16px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>