<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">评论管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="filter.username" placeholder="模糊搜索" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable style="width:120px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="artifactId" label="文物ID" width="80" />
        <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rejectReason" label="拒绝原因" width="150" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" size="small" type="success" @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 'pending'" size="small" type="warning" @click="handleRejectOpen(row)">拒绝</el-button>
            <el-popconfirm title="确定要删除该评论吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
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

    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRejectConfirm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCommentPage, approveComment, rejectComment, deleteComment } from '../api/commentAdmin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ username: '', status: '' })

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
let currentRejectId = null

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getCommentPage({
      page: pagination.page, pageSize: pagination.pageSize,
      username: filter.username || undefined, status: filter.status || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载评论列表失败') }
  finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }
function resetFilter() { filter.username = ''; filter.status = ''; handleSearch() }

function statusType(s) { return s === 'approved' ? 'success' : s === 'rejected' ? 'danger' : 'warning' }
function statusLabel(s) { return s === 'approved' ? '已通过' : s === 'rejected' ? '已拒绝' : '待审核' }

async function handleApprove(row) {
  try { await approveComment(row.id); row.status = 'approved'; ElMessage.success('已通过') }
  catch { ElMessage.error('操作失败') }
}

function handleRejectOpen(row) {
  currentRejectId = row.id; rejectReason.value = ''; rejectDialogVisible.value = true
}

async function handleRejectConfirm() {
  if (!rejectReason.value) { ElMessage.warning('请输入拒绝原因'); return }
  try { await rejectComment(currentRejectId, rejectReason.value); rejectDialogVisible.value = false; fetchData(); ElMessage.success('已拒绝') }
  catch { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try { await deleteComment(row.id); ElMessage.success('删除成功'); fetchData() }
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