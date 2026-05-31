<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" size="small" @click="handleOpenCreate">
            <el-icon><Plus /></el-icon>新增用户
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="filter.username" placeholder="模糊搜索" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="filter.nickname" placeholder="模糊搜索" clearable
            @keyup.enter="handleSearch" />
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

      <el-table :data="tableData" v-loading="loading" stripe border row-key="id">
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
        <el-table-column label="操作" min-width="340" fixed="right">
          <template #default="{ row }">
<!--            <el-button size="small" @click="handleAssignRoles(row)">分配角色</el-button>-->

            <el-popconfirm title="确定要切换评论权限吗？" @confirm="handleToggleComment(row)">
              <template #reference>
                <el-button size="small"
                  :type="row.commentDisabled ? 'warning' : 'primary'"
                  :loading="row._commentLoading">
                  {{ row.commentDisabled ? '开启评论' : '禁止评论' }}
                </el-button>
              </template>
            </el-popconfirm>

            <el-popconfirm title="确定要切换上传权限吗？" @confirm="handleToggleUpload(row)">
              <template #reference>
                <el-button size="small"
                  :type="row.uploadDisabled ? 'warning' : 'primary'"
                  :loading="row._uploadLoading">
                  {{ row.uploadDisabled ? '开启上传' : '禁止上传' }}
                </el-button>
              </template>
            </el-popconfirm>

            <el-button v-if="row.status !== 'banned'" size="small" type="danger"
              @click="handleBan(row)">封禁</el-button>
            <el-popconfirm v-else title="确定要解封该用户吗？" @confirm="handleUnban(row)">
              <template #reference>
                <el-button size="small" type="success">解封</el-button>
              </template>
            </el-popconfirm>

            <el-popconfirm title="确定要删除该用户吗？删除后数据不可恢复。"
              confirm-button-text="确认删除" cancel-button-text="取消"
              confirm-button-type="danger" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger" plain>
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-popconfirm>
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
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="createVisible" title="新增用户" width="520px" :close-on-click-modal="false">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="createForm.username" placeholder="4~50个字符" maxlength="50" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" placeholder="6~20个字符"
            maxlength="20" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="createForm.nickname" placeholder="可选，默认为用户名" maxlength="50" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" placeholder="请输入邮箱" maxlength="100" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="createForm.phone" placeholder="请输入手机号" maxlength="20" />
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="createForm.source" placeholder="请选择" style="width:100%">
            <el-option label="Web" value="web" />
            <el-option label="App" value="app" />
            <el-option label="后台录入" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="handleCreate">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="480px">
      <div v-if="roleLoading" style="text-align:center;padding:40px">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      <template v-else>
        <el-checkbox-group v-model="selectedRoleIds" v-if="allRoles.length > 0">
          <div v-for="role in allRoles" :key="role.id" style="margin-bottom:12px">
            <el-checkbox :label="role.id" :value="role.id">
              {{ role.displayName || role.name }}
              <span style="color:#909399;font-size:12px">{{ role.description }}</span>
            </el-checkbox>
          </div>
        </el-checkbox-group>
        <el-empty v-else description="暂无角色数据" />
      </template>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSubmitLoading" @click="confirmAssignRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, createUser, deleteUser, banUser, assignUserRoles, disableComment, disableUpload } from '../api/user'
import { getRoleList } from '../api/role'

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
  } catch {
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

const createVisible = ref(false)
const createFormRef = ref(null)
const createSubmitting = ref(false)
const createForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  source: 'web'
})
const createRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 50, message: '用户名长度需要 4~50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需要 6~20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

function handleOpenCreate() {
  createForm.username = ''
  createForm.password = ''
  createForm.nickname = ''
  createForm.email = ''
  createForm.phone = ''
  createForm.source = 'web'
  createFormRef.value?.resetFields()
  createVisible.value = true
}

async function handleCreate() {
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return
  createSubmitting.value = true
  try {
    await createUser({
      username: createForm.username,
      password: createForm.password,
      nickname: createForm.nickname || undefined,
      email: createForm.email || undefined,
      phone: createForm.phone || undefined,
      source: createForm.source
    })
    ElMessage.success('用户创建成功')
    createVisible.value = false
    fetchData()
  } catch {
  } finally {
    createSubmitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    fetchData()
  } catch {
    ElMessage.error('删除失败，请重试')
  }
}

async function handleToggleComment(row) {
  row._commentLoading = true
  const newVal = row.commentDisabled ? 0 : 1
  try {
    await disableComment(row.id, { commentDisabled: newVal })
    row.commentDisabled = newVal
    ElMessage.success(newVal === 0 ? '已允许评论' : '已禁止评论')
  } catch {
    ElMessage.error('操作失败，请重试')
  } finally {
    row._commentLoading = false
  }
}

async function handleToggleUpload(row) {
  row._uploadLoading = true
  const newVal = row.uploadDisabled ? 0 : 1
  try {
    await disableUpload(row.id, { uploadDisabled: newVal })
    row.uploadDisabled = newVal
    ElMessage.success(newVal === 0 ? '已允许上传' : '已禁止上传')
  } catch {
    ElMessage.error('操作失败，请重试')
  } finally {
    row._uploadLoading = false
  }
}

async function handleUnban(row) {
  try {
    await banUser(row.id, { status: 'active', banReason: null })
    row.status = 'active'
    row.banReason = null
    ElMessage.success('解封成功')
  } catch {
    ElMessage.error('解封失败，请重试')
  }
}

async function handleBan(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入封禁原因', '封禁用户', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await banUser(row.id, { status: 'banned', banReason: value || '' })
    row.status = 'banned'
    row.banReason = value || ''
    ElMessage.success('封禁成功')
  } catch {
  }
}

const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleSubmitLoading = ref(false)
const allRoles = ref([])
const selectedRoleIds = ref([])
let currentUserId = null

async function handleAssignRoles(row) {
  currentUserId = row.id
  roleDialogVisible.value = true
  roleLoading.value = true
  try {
    const res = await getRoleList()
    allRoles.value = res.data
    selectedRoleIds.value = []
  } catch {
    ElMessage.error('加载角色列表失败')
    roleDialogVisible.value = false
  } finally {
    roleLoading.value = false
  }
}

async function confirmAssignRoles() {
  roleSubmitLoading.value = true
  try {
    await assignUserRoles(currentUserId, { roleIds: selectedRoleIds.value })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } catch {
    ElMessage.error('角色分配失败')
  } finally {
    roleSubmitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
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