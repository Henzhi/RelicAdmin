<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="filter.username" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="filter.nickname" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable style="width:120px">
            <el-option label="正常" value="active" />
            <el-option label="已禁用" value="disabled" />
            <el-option label="已封禁" value="banned" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registeredAt" label="注册时间" width="160" />
        <el-table-column prop="lastLogin" label="最后登录" width="160" />
        <el-table-column label="操作" min-width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleAssignRoles(row)">分配角色</el-button>
            <el-button size="small" :type="row.commentDisabled ? 'warning' : 'primary'"
              @click="handleToggleComment(row)">
              {{ row.commentDisabled ? '开启' : '禁止' }}评论
            </el-button>
            <el-button size="small" :type="row.uploadDisabled ? 'warning' : 'primary'"
              @click="handleToggleUpload(row)">
              {{ row.uploadDisabled ? '开启' : '禁止' }}上传
            </el-button>
            <el-button v-if="row.status !== 'banned'" size="small" type="danger" @click="handleBan(row)">
              封禁
            </el-button>
            <el-button v-else size="small" type="success" @click="handleUnban(row)">
              解封
            </el-button>
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

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="480px">
      <el-checkbox-group v-model="selectedRoleIds" v-if="allRoles.length > 0">
        <div v-for="role in allRoles" :key="role.id" style="margin-bottom:12px">
          <el-checkbox :label="role.id" :value="role.id">
            {{ role.displayName || role.name }}
            <span style="color:#909399;font-size:12px">{{ role.description }}</span>
          </el-checkbox>
        </div>
      </el-checkbox-group>
      <el-empty v-else description="暂无角色" />
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, banUser, assignUserRoles, disableComment, disableUpload } from '../api/user'
import { getRoleList, getRolePermissions } from '../api/role'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ username: '', nickname: '', status: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      username: filter.username || undefined,
      nickname: filter.nickname || undefined,
      status: filter.status || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
    pagination.page = res.data.page
    pagination.pageSize = res.data.pageSize
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function resetFilter() {
  filter.username = ''
  filter.nickname = ''
  filter.status = ''
  handleSearch()
}

function statusType(status) {
  return status === 'active' ? 'success' : status === 'disabled' ? 'info' : 'danger'
}

function statusLabel(status) {
  return status === 'active' ? '正常' : status === 'disabled' ? '已禁用' : '已封禁'
}

async function handleBan(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入封禁原因', '封禁用户', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await banUser(row.id, { status: 'banned', banReason: value || '' })
    ElMessage.success('封禁成功')
    fetchData()
  } catch { return }
}

async function handleToggleComment(row) {
  const newVal = row.commentDisabled ? 0 : 1
  await disableComment(row.id, { commentDisabled: newVal })
  ElMessage.success('操作成功')
  fetchData()
}

async function handleToggleUpload(row) {
  const newVal = row.uploadDisabled ? 0 : 1
  await disableUpload(row.id, { uploadDisabled: newVal })
  ElMessage.success('操作成功')
  fetchData()
}

const roleDialogVisible = ref(false)
const allRoles = ref([])
const selectedRoleIds = ref([])
let currentUserId = null

async function handleAssignRoles(row) {
  currentUserId = row.id
  roleDialogVisible.value = true
  const res = await getRoleList()
  allRoles.value = res.data
}

async function confirmAssignRoles() {
  await assignUserRoles(currentUserId, { roleIds: selectedRoleIds.value })
  ElMessage.success('角色分配成功')
  roleDialogVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
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