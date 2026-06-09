<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span class="card-title">高频失败问题统计</span></template>
          <el-table :data="failureTypeData" border stripe v-loading="failureLoading" style="width: 100%;">
            <el-table-column prop="failureType" label="失败类型" min-width="200">
              <template #default="{ row }">{{ failureTypeLabel(row.failureType) }}</template>
            </el-table-column>
            <el-table-column prop="count" label="数量" width="100" align="center" />
          </el-table>
          <el-empty v-if="!failureLoading && failureTypeData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span class="card-title">高频不准确问题统计</span></template>
          <el-table :data="inaccurateTypeData" border stripe v-loading="inaccurateLoading" style="width: 100%;">
            <el-table-column prop="intent" label="意图类型" min-width="200" />
            <el-table-column prop="count" label="数量" width="100" align="center" />
          </el-table>
          <el-empty v-if="!inaccurateLoading && inaccurateTypeData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getQaFailureTypeStats, getQaInaccurateTypeStats } from '../api/qa'

const failureTypeData = ref([])
const inaccurateTypeData = ref([])
const failureLoading = ref(false)
const inaccurateLoading = ref(false)

const failureTypeMap = {
  no_data_found: '未找到数据',
  object_not_resolved: '文物未解析',
  intent_not_recognized: '意图识别失败',
  system_error: '系统错误'
}

function failureTypeLabel(type) { return failureTypeMap[type] || type }

onMounted(async () => {
  failureLoading.value = true
  inaccurateLoading.value = true
  try {
    const res1 = await getQaFailureTypeStats()
    failureTypeData.value = Array.isArray(res1.data) ? res1.data : []
  } catch { failureTypeData.value = [] } finally { failureLoading.value = false }
  try {
    const res2 = await getQaInaccurateTypeStats()
    inaccurateTypeData.value = Array.isArray(res2.data) ? res2.data : []
  } catch { inaccurateTypeData.value = [] } finally { inaccurateLoading.value = false }
})
</script>

<style scoped>
.page-container { padding: 20px; }
.card-title { font-size: 18px; font-weight: 600; }
</style>
