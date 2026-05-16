<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">敏感词库管理</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">添加敏感词</el-button>
            <el-button type="success" :icon="Upload" @click="openImportDialog">批量导入</el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchWord" placeholder="搜索敏感词" clearable style="width:180px" @keyup.enter="handleSearch" />
        <el-select v-model="searchCategory" placeholder="分类" clearable style="width:130px" @change="handleSearch">
          <el-option label="政治敏感" value="political" />
          <el-option label="色情" value="pornographic" />
          <el-option label="暴力" value="violence" />
          <el-option label="广告" value="advertising" />
          <el-option label="其他" value="other" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width:110px" @change="handleSearch">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-tag type="info" style="margin-left:auto">共 {{ pagination.total }} 条</el-tag>
      </div>

      <div v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无敏感词" />
        </div>
        <el-table v-else :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="word" label="敏感词" min-width="200" />
          <el-table-column prop="category" label="分类" width="110">
            <template #default="{ row }">
              <el-tag :type="categoryTag(row.category)" size="small">{{ categoryLabel(row.category) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="170" />
          <el-table-column prop="updatedAt" label="更新时间" width="170" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button size="small" link :type="row.status === 1 ? 'warning' : 'success'"
                @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
              <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container" v-if="tableData.length > 0">
        <el-pagination
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑敏感词' : '添加敏感词'" width="460px" :close-on-click-modal="false" @closed="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="敏感词">
          <el-input v-model="form.word" placeholder="请输入敏感词" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%">
            <el-option label="政治敏感" value="political" />
            <el-option label="色情" value="pornographic" />
            <el-option label="暴力" value="violence" />
            <el-option label="广告" value="advertising" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" v-if="isEdit">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">{{ isEdit ? '保存' : '添加' }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入敏感词" width="550px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="导入内容">
          <el-input v-model="importContent" type="textarea" :rows="8" placeholder="每行一个敏感词&#10;例如：&#10;敏感词A&#10;敏感词B&#10;敏感词C" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Upload } from '@element-plus/icons-vue'
import { getSensitiveWordPage, createSensitiveWord, updateSensitiveWord, updateSensitiveWordStatus, deleteSensitiveWord, batchImportSensitiveWords } from '../api/sensitiveWordAdmin'

const loading = ref(false)
const tableData = ref([])
const searchWord = ref('')
const searchCategory = ref('')
const searchStatus = ref(null)
const pagination = reactive({ page: 1, pageSize: 20, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const form = reactive({ word: '', category: 'other', status: 1 })

const importDialogVisible = ref(false)
const importContent = ref('')
const importLoading = ref(false)

onMounted(() => { fetchData() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getSensitiveWordPage({
      page: pagination.page, pageSize: pagination.pageSize,
      word: searchWord.value || undefined,
      category: searchCategory.value || undefined,
      status: searchStatus.value !== null && searchStatus.value !== '' ? Number(searchStatus.value) : undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch { ElMessage.error('加载敏感词列表失败'); tableData.value = [] }
  finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handlePageChange(page) { pagination.page = page; fetchData() }

function handleReset() {
  searchWord.value = ''; searchCategory.value = ''; searchStatus.value = null
  handleSearch()
}

function categoryTag(c) {
  if (c === 'political') return 'danger'
  if (c === 'pornographic') return 'danger'
  if (c === 'violence') return 'warning'
  if (c === 'advertising') return 'info'
  return ''
}
function categoryLabel(c) {
  const m = { political: '政治敏感', pornographic: '色情', violence: '暴力', advertising: '广告', other: '其他' }
  return m[c] || c || '其他'
}

function resetForm() {
  form.word = ''; form.category = 'other'; form.status = 1
  isEdit.value = false; editId.value = null
}

function openCreateDialog() { resetForm(); dialogVisible.value = true }

function openEditDialog(row) {
  isEdit.value = true; editId.value = row.id
  form.word = row.word; form.category = row.category; form.status = row.status
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.word.trim()) { ElMessage.warning('请输入敏感词'); return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateSensitiveWord(editId.value, { word: form.word.trim(), category: form.category, status: form.status })
      ElMessage.success('已更新')
    } else {
      await createSensitiveWord({ word: form.word.trim(), category: form.category })
      ElMessage.success('已添加')
    }
    dialogVisible.value = false; fetchData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateSensitiveWordStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除敏感词「${row.word}」吗？`, '确认删除', { type: 'warning' })
    await deleteSensitiveWord(row.id)
    ElMessage.success('已删除'); fetchData()
  } catch {}
}

function openImportDialog() { importContent.value = ''; importDialogVisible.value = true }

async function submitImport() {
  if (!importContent.value.trim()) { ElMessage.warning('请输入导入内容'); return }
  importLoading.value = true
  try {
    await batchImportSensitiveWords(importContent.value)
    ElMessage.success('批量导入成功')
    importDialogVisible.value = false; fetchData()
  } catch { ElMessage.error('批量导入失败') }
  finally { importLoading.value = false }
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