<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户关注管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="关注者">
          <el-input v-model="filter.followerName" placeholder="模糊搜索" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="被关注者">
          <el-input v-model="filter.followeeName" placeholder="模糊搜索" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="followerId" label="关注者ID" width="100" />
        <el-table-column prop="followeeId" label="被关注者ID" width="110" />
        <el-table-column prop="createdAt" label="关注时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定要取消该关注关系吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger">取消关注</el-button>
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
import { getFollowPage, deleteFollow } from '../api/followAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ followerName: '', followeeName: '' })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getFollowPage({
      page: pagination.page, pageSize: pagination.pageSize,
      followerName: filter.followerName || undefined,
      followeeName: filter.followeeName || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载关注列表失败') }
  finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }
function resetFilter() { filter.followerName = ''; filter.followeeName = ''; handleSearch() }

async function handleDelete(row) {
  try { await deleteFollow(row.followerId, row.followeeId); ElMessage.success('已取消关注'); fetchData() }
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