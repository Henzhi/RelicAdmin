<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" size="small" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色标识" width="140" />
        <el-table-column prop="displayName" label="显示名称" width="140" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handlePermissions(row)">分配权限</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="formVisible" :title="formTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="角色标识" prop="name">
          <el-input v-model="form.name" :disabled="isEdit" placeholder="唯一标识，如 editor" />
        </el-form-item>
        <el-form-item label="显示名称" prop="displayName">
          <el-input v-model="form.displayName" placeholder="如 编辑者" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="角色描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-checkbox-group v-model="selectedPermIds">
        <div v-for="perm in allPermissions" :key="perm.id" style="margin-bottom:10px">
          <el-checkbox :label="perm.id" :value="perm.id">
            <el-tag size="small">{{ perm.module }}</el-tag>
            {{ perm.displayName || perm.name }}
          </el-checkbox>
        </div>
      </el-checkbox-group>
      <el-empty v-if="allPermissions.length === 0" description="暂无权限" />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPermissions">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRolePage, createRole, updateRole, deleteRole, getRolePermissions, assignRolePermissions } from '../api/role'
import { getPermissionList } from '../api/permission'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getRolePage({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSizeChange() {
  pagination.page = 1
  fetchData()
}

function handlePageChange(page) {
  pagination.page = page
  fetchData()
}

const formVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const editId = ref(null)
const form = reactive({ name: '', displayName: '', description: '' })
const formRules = {
  name: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
  displayName: [{ required: true, message: '请输入显示名称', trigger: 'blur' }]
}

const formTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

function handleAdd() {
  isEdit.value = false
  editId.value = null
  form.name = ''
  form.displayName = ''
  form.description = ''
  formVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.name = row.name
  form.displayName = row.displayName
  form.description = row.description || ''
  formVisible.value = true
}

async function confirmForm() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const data = {
    name: form.name,
    displayName: form.displayName,
    description: form.description
  }
  if (isEdit.value) {
    await updateRole(editId.value, data)
  } else {
    await createRole(data)
  }
  ElMessage.success('操作成功')
  formVisible.value = false
  fetchData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除角色「${row.displayName}」吗？`, '确认', { type: 'warning' })
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const permDialogVisible = ref(false)
const allPermissions = ref([])
const selectedPermIds = ref([])
let currentRoleId = null

async function handlePermissions(row) {
  currentRoleId = row.id
  permDialogVisible.value = true
  const [permRes, rolePermRes] = await Promise.all([
    getPermissionList(),
    getRolePermissions(row.id)
  ])
  allPermissions.value = permRes.data
  selectedPermIds.value = rolePermRes.data.map(p => p.id)
}

async function confirmPermissions() {
  await assignRolePermissions(currentRoleId, { permissionIds: selectedPermIds.value })
  ElMessage.success('权限分配成功')
  permDialogVisible.value = false
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
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>