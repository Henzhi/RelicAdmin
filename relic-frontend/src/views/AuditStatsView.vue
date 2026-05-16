<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">审核统计</span>
          <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至"
            start-placeholder="开始时间" end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"
            style="width:360px" @change="fetchAllStats" />
        </div>
      </template>

      <div v-loading="statsLoading">
        <el-row :gutter="16" class="stats-row">
          <el-col :span="4">
            <el-statistic title="总审核量" :value="stats.totalCount || 0" />
          </el-col>
          <el-col :span="4">
            <el-statistic title="待审核" :value="stats.pendingCount || 0">
              <template #suffix><el-tag type="warning" size="small">待处理</el-tag></template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="已通过" :value="stats.approvedCount || 0">
              <template #suffix><el-tag type="success" size="small">通过率 {{ passRate }}%</el-tag></template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="已拒绝" :value="stats.rejectedCount || 0">
              <template #suffix><el-tag type="danger" size="small">拒绝率 {{ rejectRate }}%</el-tag></template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="自动拦截" :value="stats.autoRejectedCount || 0" />
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px;" v-loading="auditorLoading">
      <template #header>
        <span class="card-title">审核员工作量统计</span>
      </template>
      <div v-if="auditorStats.length === 0 && !auditorLoading" class="empty-state">
        <el-empty description="暂无审核员数据" />
      </div>
      <el-table v-else :data="auditorStats" border stripe style="width: 100%">
        <el-table-column prop="auditorName" label="审核员" width="200" />
        <el-table-column prop="auditCount" label="审核总量" width="120" align="center" sortable />
        <el-table-column prop="approvedCount" label="通过数" width="120" align="center" sortable />
        <el-table-column prop="rejectedCount" label="拒绝数" width="120" align="center" sortable />
        <el-table-column label="通过率" width="120" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.auditCount ? Math.round(row.approvedCount / row.auditCount * 100) : 0" :stroke-width="16" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAuditStats, getAuditorStats } from '../api/auditAdmin'

const statsLoading = ref(false)
const auditorLoading = ref(false)
const stats = reactive({ totalCount: 0, approvedCount: 0, rejectedCount: 0, pendingCount: 0, autoRejectedCount: 0 })
const auditorStats = ref([])
const dateRange = ref(null)

const passRate = computed(() => {
  if (!stats.totalCount) return 0
  return Math.round(stats.approvedCount / stats.totalCount * 100)
})
const rejectRate = computed(() => {
  if (!stats.totalCount) return 0
  return Math.round(stats.rejectedCount / stats.totalCount * 100)
})

onMounted(() => { fetchAllStats() })

async function fetchAllStats() {
  const startDate = dateRange.value?.[0] ? dateRange.value[0] : undefined
  const endDate = dateRange.value?.[1] ? dateRange.value[1] : undefined
  await Promise.all([fetchStats(startDate, endDate), fetchAuditorStats(startDate, endDate)])
}

async function fetchStats(startDate, endDate) {
  statsLoading.value = true
  try {
    const res = await getAuditStats({ startDate, endDate })
    Object.assign(stats, res.data || {})
  } catch { ElMessage.error('加载审核统计失败') }
  finally { statsLoading.value = false }
}

async function fetchAuditorStats(startDate, endDate) {
  auditorLoading.value = true
  try {
    const res = await getAuditorStats({ startDate, endDate })
    auditorStats.value = res.data || []
  } catch { ElMessage.error('加载审核员统计失败') }
  finally { auditorLoading.value = false }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.stats-row { margin-bottom: 8px; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 0; gap: 16px; }
</style>