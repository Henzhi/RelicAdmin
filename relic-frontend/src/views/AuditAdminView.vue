<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">审核管理</span>
          <div>
            <el-button :icon="DataAnalysis" @click="$router.push('/audit-stats')">审核统计</el-button>
            <el-tag type="info" style="margin-left: 8px;">共 {{ pagination.total }} 条</el-tag>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchContentType" placeholder="内容类型" clearable style="width:120px" @change="handleSearch">
          <el-option label="评论" value="comment" />
          <el-option label="动态" value="post" />
          <el-option label="上传" value="upload" />
        </el-select>
        <el-select v-model="searchSourceType" placeholder="来源表" clearable style="width:150px" @change="handleSearch">
          <el-option label="用户评论" value="user_comments" />
          <el-option label="用户动态" value="user_posts" />
          <el-option label="用户上传" value="user_uploads" />
        </el-select>
        <el-select v-model="searchManualAuditResult" placeholder="审核状态" clearable style="width:120px" @change="handleSearch">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-date-picker v-model="searchDateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期"
          value-format="YYYY-MM-DD" style="width:260px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div class="stats-bar" v-if="quickStats.totalCount > 0">
        <div class="stat-item">
          <span class="stat-label">待审核</span>
          <el-tag type="warning" size="large" effect="dark">{{ quickStats.pendingCount || 0 }}</el-tag>
        </div>
        <div class="stat-item">
          <span class="stat-label">已通过</span>
          <el-tag type="success" size="large" effect="dark">{{ quickStats.approvedCount || 0 }}</el-tag>
        </div>
        <div class="stat-item">
          <span class="stat-label">已拒绝</span>
          <el-tag type="danger" size="large" effect="dark">{{ quickStats.rejectedCount || 0 }}</el-tag>
        </div>
        <div class="stat-item">
          <span class="stat-label">自动拦截</span>
          <el-tag type="info" size="large" effect="dark">{{ quickStats.autoRejectedCount || 0 }}</el-tag>
        </div>
        <div class="stat-item">
          <span class="stat-label">总计</span>
          <span class="stat-total">{{ quickStats.totalCount || 0 }}</span>
        </div>
      </div>

      <div class="batch-bar" v-if="selectedIds.length > 0">
        <span>已选 <strong>{{ selectedIds.length }}</strong> 条</span>
        <el-button type="success" size="small" @click="openBatchDialog('approved')">批量通过</el-button>
        <el-button type="danger" size="small" @click="openBatchDialog('rejected')">批量拒绝</el-button>
        <el-button size="small" @click="handleClearSelection">取消选择</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无审核记录" />
        </div>

        <el-table v-else ref="tableRef" :data="tableData" border stripe style="width: 100%"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="45" />
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="来源" width="110">
            <template #default="{ row }">
              <el-tag v-if="row.sourceType === 'user_comments'" type="info" size="small">用户评论</el-tag>
              <el-tag v-else-if="row.sourceType === 'user_posts'" size="small">用户动态</el-tag>
              <el-tag v-else-if="row.sourceType === 'user_uploads'" type="success" size="small">用户上传</el-tag>
              <span v-else>{{ row.sourceType || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="submitterName" label="提交者" width="110" />
          <el-table-column label="类型" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.contentType === 'comment'" type="info" size="small">评论</el-tag>
              <el-tag v-else-if="row.contentType === 'post'" size="small">动态</el-tag>
              <el-tag v-else-if="row.contentType === 'upload'" type="success" size="small">上传</el-tag>
              <span v-else>{{ row.contentType }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
          <el-table-column label="自动审核" width="90">
            <template #default="{ row }">
              <el-tag :type="auditResultTag(row.autoAuditResult)" size="small">{{ auditResultLabel(row.autoAuditResult) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="人工审核" width="90">
            <template #default="{ row }">
              <el-tag :type="auditResultTag(row.manualAuditResult)" size="small">{{ auditResultLabel(row.manualAuditResult) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rejectReason" label="拒绝原因" min-width="130" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="提交时间" width="160" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.manualAuditResult === 'pending'" type="primary" size="small" link @click="openAuditDialog(row)">审核</el-button>
              <span v-else class="text-muted">已处理</span>
            </template>
          </el-table-column>
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

    <el-dialog v-model="auditDialogVisible" title="执行审核" width="500px" :close-on-click-modal="false">
      <el-form :model="auditForm" label-width="90px">
        <el-form-item label="内容ID">
          <span>{{ currentAuditRow?.contentId }}</span>
        </el-form-item>
        <el-form-item label="来源">
          <el-tag v-if="currentAuditRow?.sourceType === 'user_comments'" type="info" size="small">用户评论</el-tag>
          <el-tag v-else-if="currentAuditRow?.sourceType === 'user_posts'" size="small">用户动态</el-tag>
          <el-tag v-else-if="currentAuditRow?.sourceType === 'user_uploads'" type="success" size="small">用户上传</el-tag>
        </el-form-item>
        <el-form-item label="审核内容">
          <div class="audit-content">{{ currentAuditRow?.content }}</div>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.manualAuditResult">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="拒绝原因" v-if="auditForm.manualAuditResult === 'rejected'">
          <el-input v-model="auditForm.rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditSubmitting" @click="submitAudit">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" :title="batchAction === 'approved' ? '批量通过' : '批量拒绝'" width="460px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item v-if="batchAction === 'rejected'" label="拒绝原因">
          <el-input v-model="batchRejectReason" type="textarea" :rows="3" placeholder="请输入批量拒绝原因" />
        </el-form-item>
        <el-form-item label="确认信息">
          <span>将对选中的 <strong>{{ selectedIds.length }}</strong> 条记录执行批量{{ batchAction === 'approved' ? '通过' : '拒绝' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button :type="batchAction === 'approved' ? 'success' : 'danger'" :loading="batchSubmitting" @click="submitBatch">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, DataAnalysis } from '@element-plus/icons-vue'
import { getAuditPage, auditRecord, batchAudit, getAuditStats } from '../api/auditAdmin'

const loading = ref(false)
const tableRef = ref(null)
const tableData = ref([])
const selectedIds = ref([])
const searchContentType = ref('')
const searchSourceType = ref('')
const searchManualAuditResult = ref('')
const searchDateRange = ref(null)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const quickStats = reactive({ totalCount: 0, approvedCount: 0, rejectedCount: 0, pendingCount: 0, autoRejectedCount: 0 })

const auditDialogVisible = ref(false)
const auditSubmitting = ref(false)
const currentAuditRow = ref(null)
const auditForm = reactive({ manualAuditResult: 'approved', rejectReason: '' })

const batchDialogVisible = ref(false)
const batchAction = ref('approved')
const batchRejectReason = ref('')
const batchSubmitting = ref(false)

onMounted(() => { fetchQuickStats(); fetchData() })

async function fetchQuickStats() {
  try {
    const res = await getAuditStats({})
    Object.assign(quickStats, res.data || {})
  } catch { /* ignore */ }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getAuditPage({
      page: pagination.page, pageSize: pagination.pageSize,
      contentType: searchContentType.value || undefined,
      sourceType: searchSourceType.value || undefined,
      manualAuditResult: searchManualAuditResult.value || undefined,
      startDate: searchDateRange.value ? searchDateRange.value[0] : undefined,
      endDate: searchDateRange.value ? searchDateRange.value[1] : undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch {
    ElMessage.error('加载审核记录失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; selectedIds.value = []; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchContentType.value = ''; searchSourceType.value = ''; searchManualAuditResult.value = ''; searchDateRange.value = null
  handleSearch()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(row => row.id)
}

function handleClearSelection() {
  tableRef.value?.clearSelection()
  selectedIds.value = []
}

function auditResultTag(result) {
  if (result === 'approved') return 'success'
  if (result === 'rejected') return 'danger'
  return 'warning'
}

function auditResultLabel(result) {
  if (result === 'approved') return '通过'
  if (result === 'rejected') return '拒绝'
  if (result === 'pending') return '待审核'
  return result || '未知'
}

function openAuditDialog(row) {
  currentAuditRow.value = row
  auditForm.manualAuditResult = 'approved'
  auditForm.rejectReason = ''
  auditDialogVisible.value = true
}

async function submitAudit() {
  if (auditForm.manualAuditResult === 'rejected' && !auditForm.rejectReason) {
    ElMessage.warning('拒绝时必须填写拒绝原因'); return
  }
  auditSubmitting.value = true
  try {
    await auditRecord(currentAuditRow.value.id, {
      manualAuditResult: auditForm.manualAuditResult,
      rejectReason: auditForm.rejectReason || undefined
    })
    ElMessage.success('审核完成')
    auditDialogVisible.value = false
    fetchData()
    fetchQuickStats()
  } catch { ElMessage.error('审核操作失败') }
  finally { auditSubmitting.value = false }
}

function openBatchDialog(action) {
  batchAction.value = action
  batchRejectReason.value = ''
  batchDialogVisible.value = true
}

async function submitBatch() {
  if (batchAction.value === 'rejected' && !batchRejectReason.value) {
    ElMessage.warning('批量拒绝时必须填写拒绝原因'); return
  }
  batchSubmitting.value = true
  try {
    await batchAudit({
      ids: selectedIds.value,
      manualAuditResult: batchAction.value,
      rejectReason: batchAction.value === 'rejected' ? batchRejectReason.value : undefined
    })
    ElMessage.success(`已批量${batchAction.value === 'approved' ? '通过' : '拒绝'} ${selectedIds.value.length} 条记录`)
    batchDialogVisible.value = false
    handleClearSelection()
    fetchData()
    fetchQuickStats()
  } catch { ElMessage.error('批量审核失败') }
  finally { batchSubmitting.value = false }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.stats-bar { margin-bottom: 16px; padding: 12px 16px; background: #f5f7fa; border-radius: 6px; display: flex; align-items: center; gap: 24px; }
.stat-item { display: flex; align-items: center; gap: 6px; }
.stat-label { font-size: 13px; color: #666; }
.stat-total { font-size: 20px; font-weight: 700; color: #303133; }
.batch-bar { margin-bottom: 12px; padding: 8px 12px; background: #ecf5ff; border-radius: 4px; display: flex; align-items: center; gap: 8px; font-size: 14px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 0; gap: 16px; }
.text-muted { color: #999; }
.audit-content { padding: 10px; background: #f5f7fa; border-radius: 4px; max-height: 150px; overflow-y: auto; white-space: pre-wrap; }
</style>
