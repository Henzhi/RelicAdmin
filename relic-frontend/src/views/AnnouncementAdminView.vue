<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">公告管理</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">发布公告</el-button>
            <el-tag type="info" style="margin-left: 12px;">共 {{ pagination.total }} 条</el-tag>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width:120px" @change="handleSearch">
          <el-option label="已发布" :value="1" />
          <el-option label="已下线" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无公告" />
        </div>

        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
          <el-table-column label="展示位置" width="110">
            <template #default="{ row }">
              <el-tag :type="row.position === 'home' ? 'success' : row.position === 'user_center' ? 'warning' : ''" size="small">
                {{ positionLabel(row.position) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="目标受众" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ row.targetAudience === 'admin' ? '管理员' : row.targetAudience === 'users' ? '用户' : '全部' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '已发布' : '已下线' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" width="80" align="center" />
          <el-table-column prop="startTime" label="开始时间" width="170" />
          <el-table-column prop="endTime" label="结束时间" width="170" />
          <el-table-column prop="createdByName" label="创建人" width="120" />
          <el-table-column prop="createdAt" label="创建时间" width="170" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button v-if="row.status === 1" size="small" link type="warning" @click="handleOffline(row)">下线</el-button>
              <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px" :close-on-click-modal="false" @closed="resetForm">
      <el-form :model="form" label-width="90px">
        <el-form-item label="公告标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="展示位置">
          <el-select v-model="form.position" style="width: 100%">
            <el-option label="全局" value="global" />
            <el-option label="首页" value="home" />
            <el-option label="用户中心" value="user_center" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标受众">
          <el-select v-model="form.targetAudience" style="width: 100%">
            <el-option label="全部" value="all" />
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="users" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">立即发布</el-radio>
            <el-radio :value="0">暂存草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">{{ isEdit ? '保存修改' : '发布' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getAnnouncementPage, createAnnouncement, updateAnnouncement, offlineAnnouncement, deleteAnnouncement } from '../api/announcementAdmin'

const loading = ref(false)
const tableData = ref([])
const searchStatus = ref(null)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const form = reactive({ title: '', content: '', position: 'global', targetAudience: 'all', startTime: '', endTime: '', status: 1 })

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getAnnouncementPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: searchStatus.value !== null && searchStatus.value !== '' ? Number(searchStatus.value) : undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch {
    ElMessage.error('加载公告列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchStatus.value = null
  handleSearch()
}

function positionLabel(pos) {
  if (pos === 'global') return '全局'
  if (pos === 'home') return '首页'
  if (pos === 'user_center') return '用户中心'
  return pos || '全局'
}

function resetForm() {
  form.title = ''
  form.content = ''
  form.position = 'global'
  form.targetAudience = 'all'
  form.startTime = ''
  form.endTime = ''
  form.status = 1
  isEdit.value = false
  editId.value = null
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  editId.value = row.id
  form.title = row.title
  form.content = row.content
  form.position = row.position || 'global'
  form.targetAudience = row.targetAudience || 'all'
  form.startTime = row.startTime || ''
  form.endTime = row.endTime || ''
  form.status = row.status
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.title) { ElMessage.warning('请输入公告标题'); return }
  if (!form.content) { ElMessage.warning('请输入公告内容'); return }
  submitLoading.value = true
  try {
    const data = {
      title: form.title,
      content: form.content,
      position: form.position,
      targetAudience: form.targetAudience,
      startTime: form.startTime || undefined,
      endTime: form.endTime || undefined,
      status: form.status
    }
    if (isEdit.value) {
      await updateAnnouncement(editId.value, data)
      ElMessage.success('公告已更新')
    } else {
      await createAnnouncement(data)
      ElMessage.success('公告已发布')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleOffline(row) {
  try {
    await ElMessageBox.confirm(`确定要将公告「${row.title}」下线吗？`, '确认下线', { type: 'warning' })
    await offlineAnnouncement(row.id)
    ElMessage.success('公告已下线')
    fetchData()
  } catch {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除公告「${row.title}」吗？此操作不可撤销。`, '确认删除', { type: 'warning' })
    await deleteAnnouncement(row.id)
    ElMessage.success('公告已删除')
    fetchData()
  } catch {}
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 40px 0; gap: 16px; }
</style>