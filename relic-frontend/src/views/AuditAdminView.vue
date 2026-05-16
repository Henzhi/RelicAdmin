<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">审核管理</span>
          <el-tag type="info">共 {{ pagination.total }} 条</el-tag>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchContentType" placeholder="内容类型" clearable style="width:140px" @change="handleSearch">
          <el-option label="评论" value="comment" />
          <el-option label="动态" value="post" />
          <el-option label="上传" value="upload" />
        </el-select>
        <el-select v-model="searchManualAuditResult" placeholder="审核状态" clearable style="width:140px" @change="handleSearch">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无审核记录" />
        </div>

        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="submitterName" label="提交者" width="120" />
          <el-table-column label="内容类型" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.contentType === 'comment'" type="info" size="small">评论</el-tag>
              <el-tag v-else-if="row.contentType === 'post'" size="small">动态</el-tag>
              <el-tag v-else-if="row.contentType === 'upload'" type="success" size="small">上传</el-tag>
              <span v-else>{{ row.contentType }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
          <el-table-column label="自动审核" width="100">
            <template #default="{ row }">
              <el-tag :type="auditResultTag(row.autoAuditResult)" size="small">{{ auditResultLabel(row.autoAuditResult) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="人工审核" width="100">
            <template #default="{ row }">
              <el-tag :type="auditResultTag(row.manualAuditResult)" size="small">{{ auditResultLabel(row.manualAuditResult) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rejectReason" label="拒绝原因" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="提交时间" width="170" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.manualAuditResult === 'pending'" type="primary" size="small" link @click="openAuditDialog(row)">审核</el-button>
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

    <el-dialog v-model="auditDialogVisible" title="执行审核" width="500px" :close-on-click-modal="false">
      <el-form :model="auditForm" label-width="90px">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getAuditPage, auditRecord } from '../api/auditAdmin'

const loading = ref(false)
const tableData = ref([])
const searchContentType = ref('')
const searchManualAuditResult = ref('')
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const auditDialogVisible = ref(false)
const auditSubmitting = ref(false)
const currentAuditRow = ref(null)
const auditForm = reactive({ manualAuditResult: 'approved', rejectReason: '' })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getAuditPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      contentType: searchContentType.value || undefined,
      manualAuditResult: searchManualAuditResult.value || undefined
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

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchContentType.value = ''
  searchManualAuditResult.value = ''
  handleSearch()
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
    ElMessage.warning('拒绝时必须填写拒绝原因')
    return
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
  } catch {
    ElMessage.error('审核操作失败')
  } finally {
    auditSubmitting.value = false
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
.audit-content { padding: 10px; background: #f5f7fa; border-radius: 4px; max-height: 150px; overflow-y: auto; white-space: pre-wrap; }
</style>