<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">恢复操作日志</span>
          <el-button :icon="ArrowLeft" @click="$router.push('/backup')">返回备份管理</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width:120px" @change="handleSearch">
          <el-option label="进行中" :value="0" />
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="2" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-tag type="info" style="margin-left:auto">共 {{ pagination.total }} 条</el-tag>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无恢复记录" />
        </div>
        <el-table v-else :data="tableData" border stripe style="width:100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="backupName" label="恢复源" min-width="200" show-overflow-tooltip />
          <el-table-column prop="operatorName" label="操作人" width="110" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0" type="warning" size="small">进行中</el-tag>
              <el-tag v-else-if="row.status === 1" type="success" size="small">成功</el-tag>
              <el-tag v-else-if="row.status === 2" type="danger" size="small">失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="操作时间" width="170" />
        </el-table>
      </div>

      <div class="pagination-container" v-if="tableData.length > 0">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
          :page-sizes="[10,20,50]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch" @current-change="handlePageChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, ArrowLeft } from '@element-plus/icons-vue'
import { getRestorePage } from '../api/backupAdmin'

const loading = ref(false)
const tableData = ref([])
const searchStatus = ref(null)
const pagination = reactive({ page:1, pageSize:10, total:0 })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getRestorePage({ page:pagination.page, pageSize:pagination.pageSize, status:searchStatus.value })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载恢复记录失败'); tableData.value=[] }
  finally { loading.value=false }
}

function handleSearch() { pagination.page=1; fetchData() }
function handlePageChange(p) { pagination.page=p; fetchData() }
function handleReset() { searchStatus.value=null; handleSearch() }
</script>

<style scoped>
.page-container { padding:20px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { font-size:18px; font-weight:600 }
.search-bar { margin-bottom:16px; display:flex; align-items:center; gap:8px }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px }
.empty-state { display:flex; flex-direction:column; align-items:center; padding:40px 0; gap:16px }
</style>