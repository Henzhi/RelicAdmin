<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户点赞管理</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column label="ID" type="index" width="60" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="artifactId" label="文物ID" width="100" />
        <el-table-column prop="likedAt" label="点赞时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定要取消该点赞记录吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger">取消点赞</el-button>
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
import { getLikePage, deleteLike } from '../api/likeAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getLikePage({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载点赞列表失败') }
  finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

async function handleDelete(row) {
  try { await deleteLike(row.userId, row.artifactId); ElMessage.success('已取消点赞'); fetchData() }
  catch { ElMessage.error('删除失败') }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>