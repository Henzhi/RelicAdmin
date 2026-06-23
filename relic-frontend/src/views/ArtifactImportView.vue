<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">文物数据批量导入</span>
          <el-button :icon="Download" @click="handleDownloadTemplate('xlsx')">下载模板</el-button>
        </div>
      </template>

      <!-- 步骤条 -->
      <el-steps :active="currentStep" finish-status="success" align-center style="margin-bottom: 24px;">
        <el-step title="上传文件" />
        <el-step title="数据预览" />
        <el-step title="确认导入" />
        <el-step title="导入结果" />
      </el-steps>

      <!-- Step 1: 上传文件 -->
      <div v-if="currentStep === 0" class="step-content">
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-exceed="() => ElMessage.warning('只能上传一个文件')"
          :before-upload="beforeUpload"
          accept=".csv,.xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">
              支持 .csv、.xlsx、.xls 格式，文件大小不超过 50MB，单次最多导入 5000 条记录
            </div>
          </template>
        </el-upload>
        <div class="step-actions">
          <el-button type="primary" :disabled="!selectedFile" :loading="previewLoading" @click="handlePreview">
            下一步：数据预览
          </el-button>
        </div>
      </div>

      <!-- Step 2: 数据预览与字段映射 -->
      <div v-if="currentStep === 1" class="step-content">
        <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
          <template #title>
            文件共 <strong>{{ totalRows }}</strong> 行数据，以下为前 20 行预览。请确认字段映射是否正确。
          </template>
        </el-alert>

        <!-- 字段映射配置 -->
        <el-card shadow="never" style="margin-bottom: 16px;">
          <template #header><span>字段映射配置</span></template>
          <el-table :data="mappingRows" border size="small" style="width: 100%;">
            <el-table-column prop="fileColumn" label="文件列名" width="200" />
            <el-table-column label="系统字段" width="250">
              <template #default="{ row }">
                <el-select v-model="row.systemField" placeholder="请选择" clearable style="width: 100%;">
                  <el-option v-for="f in systemFields" :key="f.value" :label="f.label" :value="f.value" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="说明" min-width="150">
              <template #default="{ row }">
                <span class="text-muted">{{ row.required ? '必填' : '选填' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 数据预览表格 -->
        <el-card shadow="never">
          <template #header><span>数据预览（前20行）</span></template>
          <el-table :data="previewRows" border stripe size="small" style="width: 100%;" max-height="400">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column v-for="header in headers" :key="header" :prop="header" :label="header" min-width="120" show-overflow-tooltip />
          </el-table>
        </el-card>

        <div class="step-actions">
          <el-button @click="currentStep = 0">上一步</el-button>
          <el-button type="primary" :loading="importLoading" @click="handleConfirmImport">
            确认导入
          </el-button>
        </div>
      </div>

      <!-- Step 3: 导入中 -->
      <div v-if="currentStep === 2" class="step-content">
        <div class="importing-state">
          <el-icon class="is-loading" :size="48"><Loading /></el-icon>
          <p class="importing-text">正在导入数据，请稍候...</p>
          <p class="importing-sub">文件较大时可能需要较长时间，请勿关闭页面</p>
        </div>
      </div>

      <!-- Step 4: 导入结果 -->
      <div v-if="currentStep === 3" class="step-content">
        <el-result
          :icon="importResult.failCount === 0 ? 'success' : 'warning'"
          :title="importResult.failCount === 0 ? '导入完成' : '导入完成（部分失败）'"
        >
          <template #sub-title>
            <div class="result-stats">
              <div class="result-stat-item">
                <span class="result-label">总数</span>
                <span class="result-value">{{ importResult.totalCount }}</span>
              </div>
              <div class="result-stat-item success">
                <span class="result-label">成功</span>
                <span class="result-value">{{ importResult.successCount }}</span>
              </div>
              <div class="result-stat-item danger">
                <span class="result-label">失败</span>
                <span class="result-value">{{ importResult.failCount }}</span>
              </div>
            </div>
          </template>
          <template #extra>
            <div v-if="importResult.errors && importResult.errors.length > 0" style="width: 100%; max-width: 700px;">
              <el-collapse>
                <el-collapse-item>
                  <template #title>
                    <strong>错误详情</strong>
                    <el-tag type="danger" size="small" style="margin-left: 8px;">{{ importResult.errors.length }} 条</el-tag>
                    <span v-if="importResult.hasMoreErrors" class="text-muted" style="margin-left: 8px;">（仅显示前50条）</span>
                  </template>
                  <el-table :data="importResult.errors" border size="small" style="width: 100%;" max-height="300">
                    <el-table-column prop="row" label="行号" width="70" />
                    <el-table-column prop="type" label="错误类型" width="100" />
                    <el-table-column prop="message" label="错误描述" min-width="200" show-overflow-tooltip />
                  </el-table>
                </el-collapse-item>
              </el-collapse>
            </div>
            <el-button type="primary" @click="handleReset" style="margin-top: 16px;">继续导入</el-button>
            <el-button @click="$router.push('/artifacts')">返回文物列表</el-button>
          </template>
        </el-result>
      </div>
    </el-card>

    <!-- 导入历史 -->
    <el-card shadow="never" style="margin-top: 16px;">
      <template #header><span class="card-title">导入历史</span></template>
      <el-table :data="historyData" border stripe v-loading="historyLoading" style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="fileName" label="文件名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="fileType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.fileType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总数" width="80" align="center" />
        <el-table-column prop="successCount" label="成功" width="80" align="center">
          <template #default="{ row }">
            <span style="color: #67c23a;">{{ row.successCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="failCount" label="失败" width="80" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.failCount > 0 ? '#f56c6c' : '#999' }">{{ row.failCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'completed'" type="success" size="small">完成</el-tag>
            <el-tag v-else-if="row.status === 'processing'" type="warning" size="small">处理中</el-tag>
            <el-tag v-else type="danger" size="small">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createdAt" label="导入时间" width="170" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showHistoryDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container" v-if="historyTotal > 0">
        <el-pagination background v-model:current-page="historyPage" v-model:page-size="historyPageSize"
          :total="historyTotal" layout="total, prev, pager, next" @current-change="fetchHistory" />
      </div>
    </el-card>

    <!-- 导入详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="导入详情" width="600px">
      <div v-if="detailData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">{{ detailData.fileName }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatSize(detailData.fileSize) }}</el-descriptions-item>
          <el-descriptions-item label="总数">{{ detailData.totalCount }}</el-descriptions-item>
          <el-descriptions-item label="成功">{{ detailData.successCount }}</el-descriptions-item>
          <el-descriptions-item label="失败">{{ detailData.failCount }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detailData.status }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ detailData.operatorName }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ detailData.startedAt }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ detailData.completedAt }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detailData.errorReport" style="margin-top: 12px;">
          <strong>错误报告：</strong>
          <el-table :data="parseErrorReport(detailData.errorReport)" border size="small" style="margin-top: 8px;" max-height="250">
            <el-table-column prop="row" label="行号" width="70" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="message" label="描述" min-width="200" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Download, Loading } from '@element-plus/icons-vue'
import { previewImport, confirmImport, getImportHistory, getImportDetail, downloadTemplate } from '../api/artifactImport'

const currentStep = ref(0)
const selectedFile = ref(null)
const uploadRef = ref(null)
const previewLoading = ref(false)
const importLoading = ref(false)

// 预览数据
const headers = ref([])
const previewRows = ref([])
const totalRows = ref(0)
const autoMapping = ref({})

// 字段映射
const systemFields = [
  { value: 'objectId', label: '文物编号' },
  { value: 'titleZh', label: '中文名称' },
  { value: 'titleEn', label: '英文名称' },
  { value: 'timePeriod', label: '时期' },
  { value: 'dynastyId', label: '朝代ID' },
  { value: 'type', label: '类型' },
  { value: 'material', label: '材质' },
  { value: 'description', label: '描述' },
  { value: 'dimensions', label: '尺寸' },
  { value: 'museumId', label: '博物馆ID' },
  { value: 'locationId', label: '地点ID' },
  { value: 'detailUrl', label: '详情链接' },
  { value: 'imageUrl', label: '图片链接' },
  { value: 'creditLine', label: '来源' },
  { value: 'accessionNumber', label: '入藏编号' },
  { value: 'crawlDate', label: '采集日期' }
]
const requiredFields = new Set(['titleZh', 'type'])

const mappingRows = ref([])

// 导入结果
const importResult = reactive({ totalCount: 0, successCount: 0, failCount: 0, errors: [], hasMoreErrors: false })

// 导入历史
const historyData = ref([])
const historyLoading = ref(false)
const historyPage = ref(1)
const historyPageSize = ref(10)
const historyTotal = ref(0)

// 详情弹窗
const detailDialogVisible = ref(false)
const detailData = ref(null)

onMounted(() => { fetchHistory() })

function handleFileChange(file) {
  selectedFile.value = file.raw
}

function beforeUpload(file) {
  const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
  if (!['.csv', '.xlsx', '.xls'].includes(ext)) {
    ElMessage.error('仅支持 CSV、XLSX、XLS 格式文件')
    return false
  }
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

async function handlePreview() {
  if (!selectedFile.value) { ElMessage.warning('请先选择文件'); return }
  previewLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    const res = await previewImport(formData)
    const data = res.data
    headers.value = data.headers || []
    previewRows.value = data.previewRows || []
    totalRows.value = data.totalRows || 0
    autoMapping.value = data.autoMapping || {}

    // 构建映射行
    mappingRows.value = headers.value.map(h => ({
      fileColumn: h,
      systemField: autoMapping.value[h] || '',
      required: requiredFields.has(autoMapping.value[h])
    }))

    currentStep.value = 1
  } catch (e) {
    ElMessage.error('文件解析失败：' + (e.response?.data?.msg || e.message))
  } finally {
    previewLoading.value = false
  }
}

async function handleConfirmImport() {
  // 构建字段映射
  const fieldMapping = {}
  for (const row of mappingRows.value) {
    if (row.systemField) fieldMapping[row.fileColumn] = row.systemField
  }

  // 检查必填字段
  const mappedFields = new Set(Object.values(fieldMapping))
  if (!mappedFields.has('titleZh') || !mappedFields.has('type')) {
    ElMessage.warning('请确保映射了必填字段：中文名称、类型')
    return
  }

  currentStep.value = 2
  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('fieldMapping', JSON.stringify(fieldMapping))
    const res = await confirmImport(formData)
    Object.assign(importResult, res.data)
    currentStep.value = 3
    fetchHistory()
  } catch (e) {
    ElMessage.error('导入失败：' + (e.response?.data?.msg || e.message))
    currentStep.value = 0
  } finally {
    importLoading.value = false
  }
}

function handleReset() {
  currentStep.value = 0
  selectedFile.value = null
  headers.value = []
  previewRows.value = []
  totalRows.value = 0
  mappingRows.value = []
  Object.assign(importResult, { totalCount: 0, successCount: 0, failCount: 0, errors: [], hasMoreErrors: false })
  if (uploadRef.value) uploadRef.value.clearFiles()
}

async function fetchHistory() {
  historyLoading.value = true
  try {
    const res = await getImportHistory({ page: historyPage.value, pageSize: historyPageSize.value })
    historyData.value = res.data.records
    historyTotal.value = res.data.total
  } catch { historyData.value = [] }
  finally { historyLoading.value = false }
}

async function showHistoryDetail(row) {
  try {
    const res = await getImportDetail(row.id)
    detailData.value = res.data
    detailDialogVisible.value = true
  } catch { ElMessage.error('获取详情失败') }
}

function parseErrorReport(json) {
  try { return JSON.parse(json) } catch { return [] }
}

function formatSize(bytes) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

async function handleDownloadTemplate(fileType) {
  try {
    const res = await downloadTemplate(fileType)
    const blob = new Blob([res.data], {
      type: fileType === 'csv' ? 'text/csv' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '文物数据导入模板.' + fileType
    link.click()
    window.URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('模板下载失败')
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.step-content { min-height: 300px; }
.step-actions { margin-top: 24px; display: flex; justify-content: center; gap: 12px; }
.text-muted { color: #999; }
.importing-state { display: flex; flex-direction: column; align-items: center; padding: 60px 0; gap: 12px; }
.importing-text { font-size: 18px; font-weight: 500; }
.importing-sub { font-size: 14px; color: #999; }
.result-stats { display: flex; justify-content: center; gap: 40px; margin-top: 8px; }
.result-stat-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.result-label { font-size: 13px; color: #999; }
.result-value { font-size: 28px; font-weight: 700; color: #303133; }
.result-stat-item.success .result-value { color: #67c23a; }
.result-stat-item.danger .result-value { color: #f56c6c; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
