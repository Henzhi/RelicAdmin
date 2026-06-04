<template>
  <div class="dashboard-page">
    <el-page-header class="dashboard-header" title="仪表盘" content="数据概览与运营监控" />

    <el-row :gutter="16" class="dashboard-section">
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-primary">
          <div class="stat-item">
            <el-icon :size="40" color="#409eff"><Monitor /></el-icon>
            <div>
              <div class="stat-num">{{ overview.onlineUsers || 0 }}</div>
              <div class="stat-txt">在线用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-success">
          <div class="stat-item">
            <el-icon :size="40" color="#67c23a"><UserFilled /></el-icon>
            <div>
              <div class="stat-num">{{ overview.todayNewUsers || 0 }}</div>
              <div class="stat-txt">今日新增用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-warning">
          <div class="stat-item">
            <el-icon :size="40" color="#e6a23c"><EditPen /></el-icon>
            <div>
              <div class="stat-num">{{ overview.todayContentCount || 0 }}</div>
              <div class="stat-txt">今日内容提交量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card-danger">
          <div class="stat-item">
            <el-icon :size="40" color="#f56c6c"><WarningFilled /></el-icon>
            <div>
              <div class="stat-num">{{ overview.auditBacklog || 0 }}</div>
              <div class="stat-txt">审核队列积压</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="dashboard-section">
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <el-icon :size="40" color="#409eff"><User /></el-icon>
            <div>
              <div class="stat-num">{{ overview.total_users || 0 }}</div>
              <div class="stat-txt">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <el-icon :size="40" color="#67c23a"><Collection /></el-icon>
            <div>
              <div class="stat-num">{{ overview.total_artifacts || 0 }}</div>
              <div class="stat-txt">文物总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <el-icon :size="40" color="#e6a23c"><OfficeBuilding /></el-icon>
            <div>
              <div class="stat-num">{{ overview.total_museums || 0 }}</div>
              <div class="stat-txt">博物馆数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <el-icon :size="40" color="#f56c6c"><ChatDotRound /></el-icon>
            <div>
              <div class="stat-num">{{ overview.total_comments || 0 }}</div>
              <div class="stat-txt">评论总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="dashboard-chart-card">
      <template #header>
        <div class="card-header">
          <span>访问量统计</span>
          <el-radio-group v-model="visitRange" size="small" @change="loadVisitTrend">
            <el-radio-button value="7">近7日</el-radio-button>
            <el-radio-button value="30">近30日</el-radio-button>
            <el-radio-button value="90">近90日</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="visitChartRef" style="height:360px"></div>
    </el-card>

    <el-row :gutter="16" class="dashboard-section">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-radio-group v-model="userGrowthRange" size="small" @change="loadUserTrend">
                <el-radio-button value="7">近7日</el-radio-button>
                <el-radio-button value="30">近30日</el-radio-button>
                <el-radio-button value="90">近90日</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="userTrendChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据增长统计</span>
              <el-radio-group v-model="dataGrowthRange" size="small" @change="loadDataGrowth">
                <el-radio-button value="7">近7日</el-radio-button>
                <el-radio-button value="30">近30日</el-radio-button>
                <el-radio-button value="90">近90日</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="dataGrowthChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="dashboard-chart-card">
      <template #header>
        <div class="card-header">
          <span>异常告警</span>
          <el-button size="small" type="primary" @click="loadAlerts" :loading="alertsLoading">刷新</el-button>
        </div>
      </template>
      <el-table :data="alerts" v-loading="alertsLoading" stripe border empty-text="暂无活跃告警">
        <el-table-column prop="alertType" label="告警类型" width="140">
          <template #default="{ row }">
            <el-tag :type="alertTypeTag(row.alertType)" size="small">{{ alertTypeLabel(row.alertType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="严重级别" width="100">
          <template #default="{ row }">
            <el-tag :type="severityTag(row.severity)" size="small">{{ severityLabel(row.severity) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="告警标题" min-width="180"></el-table-column>
        <el-table-column prop="message" label="告警详情" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="source" label="来源" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160"></el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleResolve(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDashboardOverview, getUserTrend, getVisitTrend, getDataGrowth,
  getAlertPage, resolveAlert
} from '@/api/statistics'

const overview = ref({})
const visitRange = ref(30)
const userGrowthRange = ref(30)
const dataGrowthRange = ref(30)
const alerts = ref([])
const alertsLoading = ref(false)

const visitChartRef = ref(null)
const userTrendChartRef = ref(null)
const dataGrowthChartRef = ref(null)

let visitChart = null
let userTrendChart = null
let dataGrowthChart = null

function alertTypeTag(type) {
  const map = { db_failure: 'danger', disk_full: 'warning', high_memory: 'warning', cpu_overload: 'warning', interface_timeout: 'warning', service_error: 'info' }
  return map[type] || 'info'
}

function alertTypeLabel(type) {
  const map = { db_failure: '数据库故障', disk_full: '磁盘不足', high_memory: '内存告警', cpu_overload: 'CPU过载', interface_timeout: '接口超时', service_error: '服务异常' }
  return map[type] || type
}

function severityTag(level) {
  const map = { critical: 'danger', warning: 'warning', info: 'info' }
  return map[level] || 'info'
}

function severityLabel(level) {
  const map = { critical: '严重', warning: '警告', info: '提示' }
  return map[level] || level
}

async function loadOverview() {
  try {
    const res = await getDashboardOverview()
    overview.value = res.data || {}
  } catch (e) {
    console.error('Failed to load overview', e)
  }
}

async function loadVisitTrend() {
  try {
    const res = await getVisitTrend(visitRange.value)
    renderVisitChart(res)
  } catch (e) {
    console.error('Failed to load visit trend', e)
  }
}

async function loadUserTrend() {
  try {
    const res = await getUserTrend(userGrowthRange.value)
    renderUserTrendChart(res)
  } catch (e) {
    console.error('Failed to load user trend', e)
  }
}

async function loadDataGrowth() {
  try {
    const res = await getDataGrowth(dataGrowthRange.value)
    renderDataGrowthChart(res)
  } catch (e) {
    console.error('Failed to load data growth', e)
  }
}

async function loadAlerts() {
  alertsLoading.value = true
  try {
    const res = await getAlertPage({ page: 1, pageSize: 10, status: 'active' })
    alerts.value = res.data.records || []
  } catch (e) {
    console.error('Failed to load alerts', e)
  } finally {
    alertsLoading.value = false
  }
}

function handleResolve(row) {
  ElMessageBox.prompt('请输入处理备注', '处理告警', {
    confirmButtonText: '确认处理',
    cancelButtonText: '取消',
    inputPlaceholder: '处理备注（可选）'
  }).then(async ({ value }) => {
    try {
      await resolveAlert(row.id, value || '')
      ElMessage.success('告警已处理')
      loadAlerts()
    } catch (e) {
      ElMessage.error('处理失败')
    }
  }).catch(() => {})
}

function renderVisitChart(res) {
  if (!visitChartRef.value) return
  if (!visitChart) {
    visitChart = echarts.init(visitChartRef.value)
  }
  const dates = []
  const counts = []
  const list = res.data
  if (Array.isArray(list)) {
    list.forEach(item => {
      dates.push(item.date)
      counts.push(item.count)
    })
  }
  visitChart.clear()
  visitChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: { type: 'value', name: '访问量' },
    series: [{
      name: '访问量',
      type: 'line',
      smooth: true,
      data: counts,
      areaStyle: { color: 'rgba(64,158,255,0.15)' },
      itemStyle: { color: '#409eff' }
    }]
  })
}

function renderUserTrendChart(res) {
  if (!userTrendChartRef.value) return
  if (!userTrendChart) {
    userTrendChart = echarts.init(userTrendChartRef.value)
  }
  const dates = []
  const counts = []
  const list = res.data
  if (Array.isArray(list)) {
    list.forEach(item => {
      dates.push(item.date)
      counts.push(item.count)
    })
  }
  userTrendChart.clear()
  userTrendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: { type: 'value', name: '新增用户' },
    series: [{
      name: '新增用户',
      type: 'line',
      smooth: true,
      data: counts,
      areaStyle: { color: 'rgba(103,194,58,0.15)' },
      itemStyle: { color: '#67c23a' }
    }]
  })
}

function renderDataGrowthChart(res) {
  if (!dataGrowthChartRef.value) return
  if (!dataGrowthChart) {
    dataGrowthChart = echarts.init(dataGrowthChartRef.value)
  }
  const dates = []
  const visits = []
  const newUsers = []
  const contentCounts = []
  const newArtifacts = []
  const list = res.data
  if (Array.isArray(list)) {
    list.forEach(item => {
      dates.push(item.date)
      visits.push(item.visits || 0)
      newUsers.push(item.newUsers || 0)
      contentCounts.push(item.contentCounts || 0)
      newArtifacts.push(item.newArtifacts || 0)
    })
  }
  dataGrowthChart.clear()
  dataGrowthChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['访问量', '新增用户', '内容提交', '新增文物'], top: 0 },
    grid: { top: '18%', left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: { type: 'value' },
    series: [
      { name: '访问量', type: 'line', smooth: true, data: visits, itemStyle: { color: '#409eff' } },
      { name: '新增用户', type: 'line', smooth: true, data: newUsers, itemStyle: { color: '#67c23a' } },
      { name: '内容提交', type: 'line', smooth: true, data: contentCounts, itemStyle: { color: '#e6a23c' } },
      { name: '新增文物', type: 'line', smooth: true, data: newArtifacts, itemStyle: { color: '#f56c6c' } }
    ]
  })
}

function handleResize() {
  visitChart?.resize()
  userTrendChart?.resize()
  dataGrowthChart?.resize()
}

onMounted(async () => {
  await nextTick()
  await loadOverview()
  loadVisitTrend()
  loadUserTrend()
  loadDataGrowth()
  loadAlerts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  visitChart?.dispose()
  userTrendChart?.dispose()
  dataGrowthChart?.dispose()
})
</script>
