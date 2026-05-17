<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">备份记录管理</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">手动备份</el-button>
            <el-button :icon="Setting" @click="$router.push('/backup-strategy')">备份策略</el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width:120px" @change="handleSearch">
          <el-option label="进行中" :value="0" />
          <el-option label="已完成" :value="1" />
          <el-option label="失败" :value="2" />
        </el-select>
        <el-select v-model="searchBackupType" placeholder="备份类型" clearable style="width:130px" @change="handleSearch">
          <el-option label="全量备份" value="full" />
          <el-option label="增量备份" value="incremental" />
          <el-option label="导出备份" value="export" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-tag type="info" style="margin-left:auto">共 {{ pagination.total }} 条</el-tag>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无备份记录" />
        </div>
        <el-table v-else :data="tableData" border stripe style="width:100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="backupName" label="备份名称" min-width="200" show-overflow-tooltip />
          <el-table-column label="备份类型" width="110">
            <template #default="{ row }">
              <el-tag v-if="row.backupType === 'full'" size="small">全量备份</el-tag>
              <el-tag v-else-if="row.backupType === 'incremental'" type="warning" size="small">增量备份</el-tag>
              <el-tag v-else-if="row.backupType === 'export'" type="info" size="small">导出备份</el-tag>
              <span v-else>{{ row.backupType }}</span>
            </template>
          </el-table-column>
          <el-table-column label="文件大小" width="110">
            <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0" type="warning" size="small">进行中</el-tag>
              <el-tag v-else-if="row.status === 1" type="success" size="small">已完成</el-tag>
              <el-tag v-else-if="row.status === 2" type="danger" size="small">失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operatorName" label="操作人" width="100" />
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="备份时间" width="170" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" size="small" link type="primary" @click="handleDownload(row)">下载</el-button>
              <el-button v-if="row.status === 1" size="small" link type="success" @click="openRestoreDialog(row)">恢复</el-button>
              <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container" v-if="tableData.length > 0">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
          :page-sizes="[10,20,50]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch" @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-card shadow="never" style="margin-top:16px" v-loading="storageLoading">
      <template #header>
        <div class="card-header">
          <span class="card-title">存储容量监控</span>
          <el-tag :type="storageWarning ? 'danger' : 'success'" size="small">
            {{ storageWarning ? '告警' : '正常' }}
          </el-tag>
        </div>
      </template>
      <div class="storage-info" v-if="storageInfo">
        <div class="storage-item">
          <span class="storage-label">已用空间</span>
          <span class="storage-value">{{ storageInfo.usageMB }} MB</span>
        </div>
        <div class="storage-item">
          <span class="storage-label">告警阈值</span>
          <span class="storage-value">{{ storageInfo.thresholdMB }} MB</span>
        </div>
        <div class="storage-item">
          <span class="storage-label">使用比例</span>
          <span class="storage-value">{{ storageInfo.percentage }}%</span>
        </div>
        <el-progress :percentage="Math.min(storageInfo.percentage, 100)" :status="storageWarning ? 'exception' : 'success'" />
      </div>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="手动备份" width="480px" :close-on-click-modal="false" @closed="resetCreateForm">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="备份名称">
          <el-input v-model="createForm.backupName" placeholder="留空则自动生成" />
        </el-form-item>
        <el-form-item label="备份类型">
          <el-select v-model="createForm.backupType" style="width:100%">
            <el-option label="全量备份" value="full" />
            <el-option label="增量备份" value="incremental" />
            <el-option label="导出备份" value="export" />
          </el-select>
        </el-form-item>
        <el-form-item label="备份范围">
          <el-input v-model="createForm.scope" placeholder="如：seitem全部" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="submitCreate">开始备份</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="restoreDialogVisible" title="数据恢复 - 二次确认" width="460px" :close-on-click-modal="false">
      <el-alert type="error" :closable="false" show-icon style="margin-bottom:12px">
        <template #title>数据恢复将覆盖现有数据，操作不可逆</template>
      </el-alert>
      <el-form :model="restoreForm" label-width="90px">
        <el-form-item label="恢复源">
          <strong>{{ restoreRow?.backupName }}</strong>
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="restoreForm.confirmPassword" type="password" placeholder="请输入管理员密码确认" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="restoreDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="restoreLoading" @click="submitRestore">确认恢复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Setting } from '@element-plus/icons-vue'
import { getBackupPage, createBackup, deleteBackup, downloadBackup, restoreFromBackup, getStorageInfo } from '../api/backupAdmin'

const loading = ref(false)
const tableData = ref([])
const searchStatus = ref(null)
const searchBackupType = ref('')
const pagination = reactive({ page:1, pageSize:10, total:0 })

const createDialogVisible = ref(false)
const createLoading = ref(false)
const createForm = reactive({ backupName:'', backupType:'full', scope:'全部数据' })

const restoreDialogVisible = ref(false)
const restoreLoading = ref(false)
const restoreRow = ref(null)
const restoreForm = reactive({ confirmPassword:'' })

const storageLoading = ref(false)
const storageInfo = ref(null)
const storageWarning = ref(false)

onMounted(() => { fetchData(); fetchStorageInfo() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getBackupPage({ page:pagination.page, pageSize:pagination.pageSize,
      status:searchStatus.value, backupType:searchBackupType.value || undefined })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载备份记录失败'); tableData.value=[] }
  finally { loading.value=false }
}

async function fetchStorageInfo() {
  storageLoading.value = true
  try {
    const res = await getStorageInfo()
    storageInfo.value = res.data
    storageWarning.value = res.data.warning
  } catch {} finally { storageLoading.value=false }
}

function handleSearch() { pagination.page=1; fetchData() }
function handlePageChange(p) { pagination.page=p; fetchData() }
function handleReset() { searchStatus.value=null; searchBackupType.value=''; handleSearch() }

function formatFileSize(bytes) {
  if (!bytes || bytes===0) return '0 B'
  if (bytes<1024) return bytes+' B'
  if (bytes<1048576) return (bytes/1024).toFixed(2)+' KB'
  if (bytes<1073741824) return (bytes/1048576).toFixed(2)+' MB'
  return (bytes/1073741824).toFixed(2)+' GB'
}

function resetCreateForm() { createForm.backupName=''; createForm.backupType='full'; createForm.scope='全部数据' }
function openCreateDialog() { resetCreateForm(); createDialogVisible.value=true }

async function submitCreate() {
  createLoading.value = true
  try {
    await createBackup({ backupName:createForm.backupName||undefined,
      backupType:createForm.backupType, scope:createForm.scope })
    ElMessage.success('备份任务已提交')
    createDialogVisible.value = false
    setTimeout(() => { fetchData(); fetchStorageInfo() }, 5000)
  } catch { ElMessage.error('备份创建失败') }
  finally { createLoading.value=false }
}

async function handleDownload(row) {
  try {
    const token = localStorage.getItem('admin_token')
    const response = await fetch(`/admin/backup/${row.id}/download`, {
      headers: { token }
    })
    if (!response.ok) {
      const text = await response.text()
      throw new Error(text || '下载失败')
    }
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = (row.backupName || 'backup') + '.sql'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (err) {
    console.error('下载失败:', err)
    ElMessage.error('下载失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除备份「${row.backupName}」吗？`, '确认删除', { type:'warning' })
    await deleteBackup(row.id)
    ElMessage.success('已删除')
    fetchData(); fetchStorageInfo()
  } catch {}
}

function openRestoreDialog(row) {
  restoreRow.value = row
  restoreForm.confirmPassword = ''
  restoreDialogVisible.value = true
}

async function submitRestore() {
  if (!restoreForm.confirmPassword) { ElMessage.warning('请输入确认密码'); return }
  restoreLoading.value = true
  try {
    const res = await restoreFromBackup({ backupId:restoreRow.value.id, confirmPassword:restoreForm.confirmPassword })
    if (res.data.status === 'success') {
      ElMessage.success('数据恢复成功')
      restoreDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.data.msg || '恢复操作失败')
    }
  } catch (err) { ElMessage.error(err.response?.data?.msg || '恢复操作失败') }
  finally { restoreLoading.value=false }
}
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.search-bar { margin-bottom:16px; display:flex; align-items:center; gap:8px; flex-wrap:wrap }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
.storage-info { display:flex; gap:32px; align-items:center; flex-wrap:wrap; margin-bottom:12px }
.storage-item { display:flex; flex-direction:column; align-items:center }
.storage-label { font-size:12px; color:#999 }
.storage-value { font-size:20px; font-weight:600; margin-top:4px }
</style>