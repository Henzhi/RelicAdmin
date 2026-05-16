<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">审核策略配置</span>
          <el-tag type="warning">修改策略将影响自动审核行为</el-tag>
        </div>
      </template>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无审核策略，请先初始化" />
          <el-button type="primary" @click="fetchData">刷新</el-button>
        </div>

        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="contentType" label="内容类型" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.contentType === 'post'" size="small">动态</el-tag>
              <el-tag v-else-if="row.contentType === 'comment'" type="info" size="small">评论</el-tag>
              <el-tag v-else-if="row.contentType === 'upload'" type="success" size="small">上传</el-tag>
              <span v-else>{{ row.contentType }}</span>
            </template>
          </el-table-column>
          <el-table-column label="自动审核模式" width="150">
            <template #default="{ row }">
              <el-tag :type="autoModeTag(row.autoMode)" size="small">{{ autoModeLabel(row.autoMode) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="敏感词检测" width="120">
            <template #default="{ row }">
              <el-tag :type="row.enableSensitiveCheck === 1 ? 'success' : 'info'" size="small">
                {{ row.enableSensitiveCheck === 1 ? '已启用' : '未启用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="图片审核" width="120">
            <template #default="{ row }">
              <el-tag :type="row.enableImageCheck === 1 ? 'success' : 'info'" size="small">
                {{ row.enableImageCheck === 1 ? '已启用' : '未启用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="riskThreshold" label="风险阈值" width="100" align="center" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link type="primary" @click="openEditDialog(row)">配置</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="配置审核策略" width="480px" :close-on-click-modal="false" @closed="resetForm">
      <el-form :model="form" label-width="110px">
        <el-form-item label="自动审核模式">
          <el-select v-model="form.autoMode" style="width: 100%">
            <el-option label="自动通过" value="auto_pass">
              <span>自动通过</span>
              <span style="color:#999;margin-left:8px;font-size:12px;">低风险内容自动放行</span>
            </el-option>
            <el-option label="转人工审核" value="auto_review">
              <span>转人工审核</span>
              <span style="color:#999;margin-left:8px;font-size:12px;">所有内容需人工审核</span>
            </el-option>
            <el-option label="自动拒绝" value="auto_reject">
              <span>自动拒绝</span>
              <span style="color:#999;margin-left:8px;font-size:12px;">高风险内容直接拒绝</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="敏感词检测">
          <el-switch v-model="form.enableSensitiveCheck" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="图片审核">
          <el-switch v-model="form.enableImageCheck" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="风险阈值">
          <el-input-number v-model="form.riskThreshold" :min="1" :max="5" style="width: 100%" />
          <div class="form-tip">1=最低风险，5=最高风险。超过阈值的自动转人工审核</div>
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAuditStrategies, updateAuditStrategy } from '../api/auditStrategyAdmin'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const form = reactive({ autoMode: 'auto_review', enableSensitiveCheck: 1, enableImageCheck: 0, riskThreshold: 2, status: 1 })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getAuditStrategies()
    tableData.value = res.data || []
  } catch { ElMessage.error('加载审核策略失败'); tableData.value = [] }
  finally { loading.value = false }
}

function autoModeTag(mode) {
  if (mode === 'auto_pass') return 'success'
  if (mode === 'auto_review') return 'warning'
  if (mode === 'auto_reject') return 'danger'
  return 'info'
}
function autoModeLabel(mode) {
  if (mode === 'auto_pass') return '自动通过'
  if (mode === 'auto_review') return '转人工审核'
  if (mode === 'auto_reject') return '自动拒绝'
  return mode || '未知'
}

function resetForm() {
  form.autoMode = 'auto_review'; form.enableSensitiveCheck = 1
  form.enableImageCheck = 0; form.riskThreshold = 2; form.status = 1
  editId.value = null
}

function openEditDialog(row) {
  editId.value = row.id
  form.autoMode = row.autoMode || 'auto_review'
  form.enableSensitiveCheck = row.enableSensitiveCheck != null ? row.enableSensitiveCheck : 1
  form.enableImageCheck = row.enableImageCheck != null ? row.enableImageCheck : 0
  form.riskThreshold = row.riskThreshold || 2
  form.status = row.status != null ? row.status : 1
  dialogVisible.value = true
}

async function submitForm() {
  submitLoading.value = true
  try {
    await updateAuditStrategy(editId.value, { ...form })
    ElMessage.success('策略已更新')
    dialogVisible.value = false; fetchData()
  } catch { ElMessage.error('更新策略失败') }
  finally { submitLoading.value = false }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 0; gap: 16px; }
.form-tip { color: #999; font-size: 12px; margin-top: 4px; }
</style>