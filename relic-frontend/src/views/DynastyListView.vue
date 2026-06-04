<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">朝代管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增朝代</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchForm.nameZh" placeholder="朝代中文名" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nameZh" label="朝代（中文）" min-width="120" />
        <el-table-column prop="nameEn" label="朝代（英文）" min-width="150" show-overflow-tooltip />
        <el-table-column prop="startYear" label="起始年份" width="100" />
        <el-table-column prop="endYear" label="结束年份" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="Edit" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该朝代？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="朝代（中文）" prop="nameZh">
          <el-input v-model="formData.nameZh" placeholder="请输入朝代中文名" />
        </el-form-item>
        <el-form-item label="朝代（英文）" prop="nameEn">
          <el-input v-model="formData.nameEn" placeholder="请输入朝代英文名" />
        </el-form-item>
        <el-form-item label="起始年份" prop="startYear">
          <el-input-number v-model="formData.startYear" :min="-5000" :max="3000" placeholder="起始年份" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束年份" prop="endYear">
          <el-input-number v-model="formData.endYear" :min="-5000" :max="3000" placeholder="结束年份" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入朝代描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getDynastyPage, createDynasty, updateDynasty, deleteDynasty } from '@/api/dynasty'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ nameZh: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  nameZh: '', nameEn: '', startYear: null, endYear: null, description: ''
})
const formRules = {
  nameZh: [{ required: true, message: '请输入朝代中文名', trigger: 'blur' }],
  startYear: [{ required: true, message: '请输入起始年份', trigger: 'blur' }]
}

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getDynastyPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      nameZh: searchForm.nameZh || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handlePageChange(page) {
  pagination.page = page
  fetchData()
}

function handleReset() {
  searchForm.nameZh = ''
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增朝代'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑朝代'
  isEdit.value = true
  editId.value = row.id
  formData.nameZh = row.nameZh
  formData.nameEn = row.nameEn || ''
  formData.startYear = row.startYear
  formData.endYear = row.endYear
  formData.description = row.description || ''
  dialogVisible.value = true
}

function resetForm() {
  formData.nameZh = ''
  formData.nameEn = ''
  formData.startYear = null
  formData.endYear = null
  formData.description = ''
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateDynasty(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createDynasty({ ...formData })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteDynasty(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '删除失败')
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>