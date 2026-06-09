<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">审核任务</span>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.taskStatus" placeholder="全部" clearable style="width: 120px;">
            <el-option label="待审核" value="pending" />
            <el-option label="已完成" value="done" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-select v-model="searchForm.reviewResult" placeholder="全部" clearable style="width: 130px;">
            <el-option label="确认有问题" value="approved" />
            <el-option label="回答无问题" value="rejected" />
            <el-option label="已修复" value="fixed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="feedback_id" label="反馈ID" width="90" />
        <el-table-column prop="qa_log_id" label="日志ID" width="90" />
        <el-table-column prop="task_status" label="任务状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.task_status === 'done'" type="success" size="small">已完成</el-tag>
            <el-tag v-else type="warning" size="small">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="review_result" label="审核结果" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.review_result === 'approved'" type="danger" size="small">确认有问题</el-tag>
            <el-tag v-else-if="row.review_result === 'rejected'" type="success" size="small">回答无问题</el-tag>
            <el-tag v-else-if="row.review_result === 'fixed'" type="primary" size="small">已修复</el-tag>
            <el-tag v-else size="small">-</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" align="center" />
        <el-table-column prop="review_comment" label="审核意见" min-width="150" show-overflow-tooltip />
        <el-table-column prop="corrected_answer" label="修正答案" min-width="150" show-overflow-tooltip />
        <el-table-column prop="created_at" label="创建时间" width="170" />
        <el-table-column prop="reviewed_at" label="审核时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.task_status === 'pending'" type="primary" link size="small" @click="openReview(row)">审核</el-button>
            <span v-else class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container" v-if="total > 0">
        <el-pagination background v-model:current-page="page" v-model:page-size="pageSize"
          :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog v-model="reviewDialogVisible" title="审核任务" width="500px">
      <el-form :model="reviewForm" label-width="100px">
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="reviewForm.reviewResult">
            <el-radio value="approved">确认有问题</el-radio>
            <el-radio value="rejected">回答无问题</el-radio>
            <el-radio value="fixed">已修复</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.reviewComment" type="textarea" :rows="3" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getQaReviewTasks, reviewQaTask } from '../api/qa'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const searchForm = ref({ taskStatus: '', reviewResult: '' })

const reviewDialogVisible = ref(false)
const submitLoading = ref(false)
const currentTaskId = ref(null)
const reviewForm = ref({ reviewResult: 'approved', reviewComment: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.taskStatus) params.taskStatus = searchForm.value.taskStatus
    if (searchForm.value.reviewResult) params.reviewResult = searchForm.value.reviewResult
    const res = await getQaReviewTasks(params)
    const data = res.data
    if (data && data.records) { tableData.value = data.records; total.value = data.total || 0 }
    else if (data && data.items) { tableData.value = data.items; total.value = data.total || 0 }
    else if (Array.isArray(data)) { tableData.value = data; total.value = data.length }
    else { tableData.value = []; total.value = 0 }
  } catch { tableData.value = [] } finally { loading.value = false }
}

function resetSearch() {
  searchForm.value = { taskStatus: '', reviewResult: '' }
  page.value = 1
  fetchData()
}

function openReview(row) {
  currentTaskId.value = row.id
  reviewForm.value = { reviewResult: 'approved', reviewComment: '' }
  reviewDialogVisible.value = true
}

async function submitReview() {
  if (!reviewForm.value.reviewResult) { ElMessage.warning('请选择审核结果'); return }
  submitLoading.value = true
  try {
    const adminId = localStorage.getItem('admin_id') || 1
    await reviewQaTask(currentTaskId.value, {
      reviewResult: reviewForm.value.reviewResult,
      reviewComment: reviewForm.value.reviewComment,
      reviewerId: Number(adminId)
    })
    ElMessage.success('审核成功')
    reviewDialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('审核失败：' + (e.response?.data?.msg || e.message))
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-title { font-size: 18px; font-weight: 600; }
.search-form { margin-bottom: 16px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
.text-muted { color: #999; }
</style>
