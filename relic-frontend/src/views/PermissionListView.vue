<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" size="small" @click="handleAdd">新增权限</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="权限标识">
          <el-input v-model="filter.name" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="所属模块">
          <el-select v-model="filter.module" placeholder="全部" clearable style="width:150px">
            <el-option label="用户管理" value="用户管理" />
            <el-option label="角色管理" value="角色管理" />
            <el-option label="文物管理" value="文物管理" />
            <el-option label="博物馆管理" value="博物馆管理" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="权限标识" width="180" />
        <el-table-column prop="displayName" label="显示名称" width="180" />
        <el-table-column prop="module" label="所属模块" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <el-dialog v-model="formVisible" :title="formTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="权限标识" prop="name">
          <el-input v-model="form.name" :disabled="isEdit" placeholder="如 artifact:view" />
        </el-form-item>
        <el-form-item label="显示名称" prop="displayName">
          <el-input v-model="form.displayName" placeholder="如 查看文物" />
        </el-form-item>
        <el-form-item label="所属模块" prop="module">
          <el-input v-model="form.module" placeholder="如 文物管理" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionPage, createPermission, updatePermission, deletePermission } from '../api/permission'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ name: '', module: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getPermissionPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      name: filter.name || undefined,
      module: filter.module || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function resetFilter() {
  filter.name = ''
  filter.module = ''
  handleSearch()
}

const formVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const editId = ref(null)
const form = reactive({ name: '', displayName: '', module: '' })
const formRules = {
  name: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  displayName: [{ required: true, message: '请输入显示名称', trigger: 'blur' }],
  module: [{ required: true, message: '请输入所属模块', trigger: 'blur' }]
}

const formTitle = computed(() => isEdit.value ? '编辑权限' : '新增权限')

function handleAdd() {
  isEdit.value = false
  editId.value = null
  form.name = ''
  form.displayName = ''
  form.module = ''
  formVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.name = row.name
  form.displayName = row.displayName
  form.module = row.module
  formVisible.value = true
}

async function confirmForm() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const data = { name: form.name, displayName: form.displayName, module: form.module }
  if (isEdit.value) {
    await updatePermission(editId.value, data)
  } else {
    await createPermission(data)
  }
  ElMessage.success('操作成功')
  formVisible.value = false
  fetchData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除权限「${row.displayName}」吗？`, '确认', { type: 'warning' })
  await deletePermission(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
.filter-form {
  margin-bottom: 16px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>