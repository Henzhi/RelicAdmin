<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">违规处罚管理</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">处罚用户</el-button>
            <el-tag type="info" style="margin-left: 12px;">共 {{ pagination.total }} 条</el-tag>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchUserId" placeholder="用户ID" clearable style="width:120px" @keyup.enter="handleSearch" />
        <el-select v-model="searchPenaltyType" placeholder="处罚类型" clearable style="width:140px" @change="handleSearch">
          <el-option label="警告" value="warning" />
          <el-option label="临时封禁" value="temp_ban" />
          <el-option label="永久封禁" value="permanent_ban" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width:120px" @change="handleSearch">
          <el-option label="生效中" :value="1" />
          <el-option label="已解除" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无处罚记录" />
        </div>

        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="被处罚用户" width="120" />
          <el-table-column label="处罚类型" width="100">
            <template #default="{ row }">
              <el-tag :type="penaltyTypeTag(row.penaltyType)" size="small">{{ penaltyTypeLabel(row.penaltyType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="处罚原因" min-width="200" show-overflow-tooltip />
          <el-table-column prop="operatorName" label="操作人" width="120" />
          <el-table-column prop="penaltyTime" label="处罚时间" width="170" />
          <el-table-column prop="expireTime" label="过期时间" width="170">
            <template #default="{ row }">
              <span v-if="row.expireTime">{{ row.expireTime }}</span>
              <span v-else class="text-muted">永久</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'danger' : 'success'" size="small">
                {{ row.status === 1 ? '生效中' : '已解除' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="创建时间" width="170" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" type="warning" size="small" link @click="openRevokeDialog(row)">解除</el-button>
              <span v-else class="text-muted">已解除</span>
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

    <el-dialog v-model="createDialogVisible" title="处罚用户" width="500px" :close-on-click-modal="false">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="用户ID">
          <el-input-number v-model="createForm.userId" :min="1" placeholder="输入被处罚用户ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="处罚类型">
          <el-select v-model="createForm.penaltyType" placeholder="选择处罚类型" style="width: 100%">
            <el-option v-for="vt in violationTypes" :key="vt.typeCode" :label="vt.typeName + ' (' + penaltyTypeLabel(vt.defaultPenalty) + ')'" :value="vt.defaultPenalty" />
          </el-select>
        </el-form-item>
        <el-form-item label="处罚原因">
          <el-input v-model="createForm.reason" type="textarea" :rows="3" placeholder="请输入处罚原因" />
        </el-form-item>
        <el-form-item label="过期时间" v-if="createForm.penaltyType === 'temp_ban'">
          <el-date-picker v-model="createForm.expireTime" type="datetime" placeholder="选择过期时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="2" placeholder="备注信息（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="submitCreate">确认处罚</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="revokeDialogVisible" title="解除处罚" width="400px" :close-on-click-modal="false">
      <el-form :model="revokeForm" label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="revokeForm.remark" type="textarea" :rows="3" placeholder="请输入解除原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="revokeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="revokeSubmitting" @click="submitRevoke">确认解除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getPenaltyPage, createPenalty, revokePenalty, getViolationTypes } from '../api/penaltyAdmin'

const loading = ref(false)
const tableData = ref([])
const violationTypes = ref([])
const searchUserId = ref('')
const searchPenaltyType = ref('')
const searchStatus = ref(null)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const createDialogVisible = ref(false)
const createSubmitting = ref(false)
const createForm = reactive({ userId: null, penaltyType: '', reason: '', expireTime: '', remark: '' })

const revokeDialogVisible = ref(false)
const revokeSubmitting = ref(false)
const currentRevokeId = ref(null)
const revokeForm = reactive({ remark: '' })

onMounted(async () => {
  await loadViolationTypes()
  fetchData()
})

async function loadViolationTypes() {
  try {
    const res = await getViolationTypes()
    violationTypes.value = res.data.records || []
  } catch {
    violationTypes.value = []
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPenaltyPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      userId: searchUserId.value ? Number(searchUserId.value) : undefined,
      penaltyType: searchPenaltyType.value || undefined,
      status: searchStatus.value !== null && searchStatus.value !== '' ? Number(searchStatus.value) : undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch {
    ElMessage.error('加载处罚记录失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchUserId.value = ''
  searchPenaltyType.value = ''
  searchStatus.value = null
  handleSearch()
}

function penaltyTypeTag(type) {
  if (type === 'warning') return 'warning'
  if (type === 'temp_ban') return 'danger'
  if (type === 'permanent_ban') return 'danger'
  return 'info'
}

function penaltyTypeLabel(type) {
  if (type === 'warning') return '警告'
  if (type === 'temp_ban') return '临时封禁'
  if (type === 'permanent_ban') return '永久封禁'
  return type || '未知'
}

function openCreateDialog() {
  createForm.userId = null
  createForm.penaltyType = ''
  createForm.reason = ''
  createForm.expireTime = ''
  createForm.remark = ''
  createDialogVisible.value = true
}

async function submitCreate() {
  if (!createForm.userId) { ElMessage.warning('请输入被处罚用户ID'); return }
  if (!createForm.penaltyType) { ElMessage.warning('请选择处罚类型'); return }
  if (!createForm.reason) { ElMessage.warning('请输入处罚原因'); return }
  if (createForm.penaltyType === 'temp_ban' && !createForm.expireTime) {
    ElMessage.warning('临时封禁必须指定过期时间'); return
  }
  createSubmitting.value = true
  try {
    await createPenalty({
      userId: createForm.userId,
      penaltyType: createForm.penaltyType,
      reason: createForm.reason,
      expireTime: createForm.expireTime || undefined,
      remark: createForm.remark || undefined
    })
    ElMessage.success('处罚已执行')
    createDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('处罚操作失败')
  } finally {
    createSubmitting.value = false
  }
}

function openRevokeDialog(row) {
  currentRevokeId.value = row.id
  revokeForm.remark = ''
  revokeDialogVisible.value = true
}

async function submitRevoke() {
  revokeSubmitting.value = true
  try {
    await revokePenalty(currentRevokeId.value, { remark: revokeForm.remark || '已解除' })
    ElMessage.success('处罚已解除')
    revokeDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('解除处罚失败')
  } finally {
    revokeSubmitting.value = false
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
</style>