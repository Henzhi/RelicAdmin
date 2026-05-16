<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">申诉管理</span>
          <el-tag type="info">共 {{ pagination.total }} 条</el-tag>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchStatus" placeholder="申诉状态" clearable style="width:140px" @change="handleSearch">
          <el-option label="待处理" value="pending" />
          <el-option label="申诉成立" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无申诉记录" />
        </div>

        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="申诉用户" width="120" />
          <el-table-column prop="penaltyType" label="处罚类型" width="100">
            <template #default="{ row }">
              <el-tag :type="penaltyTypeTag(row.penaltyType)" size="small">{{ penaltyTypeLabel(row.penaltyType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="penaltyReason" label="处罚原因" min-width="150" show-overflow-tooltip />
          <el-table-column prop="appealReason" label="申诉理由" min-width="200" show-overflow-tooltip />
          <el-table-column prop="evidence" label="证据材料" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.evidence">{{ row.evidence }}</span>
              <span v-else class="text-muted">无</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="appealStatusTag(row.status)" size="small">{{ appealStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reviewerName" label="处理人" width="120">
            <template #default="{ row }">
              <span v-if="row.reviewerName">{{ row.reviewerName }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="reviewResult" label="处理结果" width="120" show-overflow-tooltip />
          <el-table-column prop="reviewRemark" label="处理备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="提交时间" width="170" />
          <el-table-column prop="reviewTime" label="处理时间" width="170">
            <template #default="{ row }">
              <span v-if="row.reviewTime">{{ row.reviewTime }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 'pending'" type="primary" size="small" link @click="openHandleDialog(row)">处理</el-button>
              <span v-else class="text-muted">已处理</span>
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

    <el-dialog v-model="handleDialogVisible" title="处理申诉" width="500px" :close-on-click-modal="false">
      <el-form :model="handleForm" label-width="90px">
        <el-form-item label="申诉理由">
          <div class="audit-content">{{ currentAppealRow?.appealReason }}</div>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleForm.status">
            <el-radio value="approved">申诉成立</el-radio>
            <el-radio value="rejected">驳回申诉</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="结果描述">
          <el-input v-model="handleForm.reviewResult" type="textarea" :rows="2" placeholder="请输入处理结果描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="handleForm.reviewRemark" type="textarea" :rows="2" placeholder="处理备注（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="handleSubmitting" @click="submitHandle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getAppealPage, handleAppeal } from '../api/appealAdmin'

const loading = ref(false)
const tableData = ref([])
const searchStatus = ref('')
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const handleDialogVisible = ref(false)
const handleSubmitting = ref(false)
const currentAppealRow = ref(null)
const handleForm = reactive({ status: 'approved', reviewResult: '', reviewRemark: '' })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getAppealPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: searchStatus.value || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch {
    ElMessage.error('加载申诉记录失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchStatus.value = ''
  handleSearch()
}

function penaltyTypeTag(type) {
  if (type === 'warning') return 'warning'
  if (type === 'temp_ban' || type === 'permanent_ban') return 'danger'
  return 'info'
}

function penaltyTypeLabel(type) {
  if (type === 'warning') return '警告'
  if (type === 'temp_ban') return '临时封禁'
  if (type === 'permanent_ban') return '永久封禁'
  return type || '未知'
}

function appealStatusTag(status) {
  if (status === 'approved') return 'success'
  if (status === 'rejected') return 'danger'
  return 'warning'
}

function appealStatusLabel(status) {
  if (status === 'approved') return '申诉成立'
  if (status === 'rejected') return '已驳回'
  if (status === 'pending') return '待处理'
  return status || '未知'
}

function openHandleDialog(row) {
  currentAppealRow.value = row
  handleForm.status = 'approved'
  handleForm.reviewResult = ''
  handleForm.reviewRemark = ''
  handleDialogVisible.value = true
}

async function submitHandle() {
  if (!handleForm.reviewResult) {
    ElMessage.warning('请输入处理结果描述')
    return
  }
  handleSubmitting.value = true
  try {
    await handleAppeal(currentAppealRow.value.id, {
      status: handleForm.status,
      reviewResult: handleForm.reviewResult,
      reviewRemark: handleForm.reviewRemark || undefined
    })
    ElMessage.success('申诉处理完成')
    handleDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('处理申诉失败')
  } finally {
    handleSubmitting.value = false
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 0; gap: 16px; }
.text-muted { color: #999; }
.audit-content { padding: 10px; background: #f5f7fa; border-radius: 4px; max-height: 120px; overflow-y: auto; white-space: pre-wrap; }
</style>