<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">博物馆管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增博物馆</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchForm.name" placeholder="博物馆名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-input v-model="searchForm.country" placeholder="国家" clearable style="width: 160px; margin-left: 12px" @keyup.enter="handleSearch" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="博物馆名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="shortName" label="简称" width="120" show-overflow-tooltip />
        <el-table-column prop="country" label="国家" width="100" />
        <el-table-column prop="city" label="城市" width="120" show-overflow-tooltip />
        <el-table-column prop="website" label="官网" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="Edit" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该博物馆？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
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
        <el-form-item label="博物馆名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入博物馆名称" />
        </el-form-item>
        <el-form-item label="简称" prop="shortName">
          <el-input v-model="formData.shortName" placeholder="请输入简称" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-input v-model="formData.country" placeholder="请输入国家" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="formData.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="官网" prop="website">
          <el-input v-model="formData.website" placeholder="请输入官网地址" />
        </el-form-item>
        <el-form-item label="藏品链接" prop="collectionUrl">
          <el-input v-model="formData.collectionUrl" placeholder="请输入藏品页面链接" />
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
import { getMuseumPage, createMuseum, updateMuseum, deleteMuseum } from '@/api/museum'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ name: '', country: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  name: '', shortName: '', country: '', city: '', website: '', collectionUrl: ''
})
const formRules = {
  name: [{ required: true, message: '请输入博物馆名称', trigger: 'blur' }],
  country: [{ required: true, message: '请输入国家', trigger: 'blur' }]
}

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getMuseumPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
      country: searchForm.country || undefined
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
  searchForm.name = ''
  searchForm.country = ''
  handleSearch()
}

function handleAdd() {
  dialogTitle.value = '新增博物馆'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑博物馆'
  isEdit.value = true
  editId.value = row.id
  formData.name = row.name
  formData.shortName = row.shortName || ''
  formData.country = row.country || ''
  formData.city = row.city || ''
  formData.website = row.website || ''
  formData.collectionUrl = row.collectionUrl || ''
  dialogVisible.value = true
}

function resetForm() {
  formData.name = ''
  formData.shortName = ''
  formData.country = ''
  formData.city = ''
  formData.website = ''
  formData.collectionUrl = ''
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateMuseum(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createMuseum({ ...formData })
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
    await deleteMuseum(row.id)
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
.page-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-size: 18px;
  font-weight: 600;
}
.search-bar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>