<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">备份策略配置</span>
          <el-button :icon="ArrowLeft" @click="$router.push('/backup')">返回备份管理</el-button>
        </div>
      </template>

      <div v-loading="loading">
        <div v-if="!strategy && !loading" class="empty-state">
          <el-empty description="备份策略未初始化" />
        </div>

        <el-form v-else :model="form" label-width="140px" size="default">
          <el-form-item label="自动备份">
            <el-switch v-model="form.autoBackupEnabled" :active-value="1" :inactive-value="0" />
            <span class="form-tip">开启后将按Cron表达式自动执行备份</span>
          </el-form-item>

          <el-form-item label="Cron表达式">
            <el-input v-model="form.backupCron" style="width:300px" placeholder="0 0 2 * * ?" />
            <div class="form-tip">
              <el-tag v-for="item in cronPresets" :key="item.label" size="small" style="cursor:pointer;margin-right:6px"
                      @click="form.backupCron = item.cron">{{ item.label }}</el-tag>
            </div>
          </el-form-item>

          <el-form-item label="备份类型">
            <el-select v-model="form.backupType" style="width:200px">
              <el-option label="全量备份" value="full" />
              <el-option label="增量备份" value="incremental" />
              <el-option label="导出备份" value="export" />
            </el-select>
          </el-form-item>

          <el-form-item label="保留天数">
            <el-input-number v-model="form.retentionDays" :min="1" :max="365" style="width:200px" />
            <span class="form-tip">超过保留天数的备份将自动清理</span>
          </el-form-item>

          <el-form-item label="加密存储">
            <el-switch v-model="form.encryptEnabled" :active-value="1" :inactive-value="0" />
          </el-form-item>

          <el-form-item label="失败通知">
            <el-switch v-model="form.notifyOnFailure" :active-value="1" :inactive-value="0" />
            <span class="form-tip">备份失败时通过系统消息通知管理员</span>
          </el-form-item>

          <el-form-item label="存储路径">
            <el-input v-model="form.storagePath" style="width:400px" placeholder="./backups" />
          </el-form-item>

          <el-form-item label="存储告警阈值(GB)">
            <el-input-number v-model="storageThresholdGB" :min="1" :max="1000" :step="1" style="width:200px" @change="onThresholdChange" />
            <span class="form-tip">超过阈值时触发告警</span>
          </el-form-item>

          <el-form-item label="启用状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitLoading" @click="submitForm">保存配置</el-button>
            <el-button @click="$router.push('/backup')">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getBackupStrategy, updateBackupStrategy } from '../api/backupAdmin'

const loading = ref(false)
const submitLoading = ref(false)
const strategy = ref(null)
const storageThresholdGB = ref(10)
const form = reactive({
  autoBackupEnabled:0, backupCron:'0 0 2 * * ?', backupType:'full',
  retentionDays:30, encryptEnabled:1, notifyOnFailure:1,
  storagePath:'./backups', storageWarningThreshold:10737418240, status:1
})

const cronPresets = [
  { label:'每天凌晨2点', cron:'0 0 2 * * ?' },
  { label:'每周一凌晨3点', cron:'0 0 3 ? * MON' },
  { label:'每月1号凌晨3点', cron:'0 0 3 1 * ?' },
  { label:'每小时', cron:'0 0 * * * ?' }
]

onMounted(() => { fetchStrategy() })

async function fetchStrategy() {
  loading.value = true
  try {
    const res = await getBackupStrategy()
    strategy.value = res.data
    if (res.data) {
      Object.assign(form, res.data)
      storageThresholdGB.value = Math.round(res.data.storageWarningThreshold / 1073741824)
    }
  } catch { ElMessage.error('加载备份策略失败') }
  finally { loading.value=false }
}

function onThresholdChange(val) {
  form.storageWarningThreshold = val * 1073741824
}

async function submitForm() {
  if (!strategy.value?.id) { ElMessage.warning('策略数据不完整'); return }
  submitLoading.value = true
  try {
    await updateBackupStrategy(strategy.value.id, {
      autoBackupEnabled:form.autoBackupEnabled, backupCron:form.backupCron,
      backupType:form.backupType, retentionDays:form.retentionDays,
      encryptEnabled:form.encryptEnabled, notifyOnFailure:form.notifyOnFailure,
      storagePath:form.storagePath, storageWarningThreshold:form.storageWarningThreshold,
      status:form.status
    })
    ElMessage.success('备份策略已保存')
  } catch { ElMessage.error('保存失败') }
  finally { submitLoading.value=false }
}
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
.form-tip { color:#999; font-size:12px; margin-left:8px }
</style>