<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员账号管理</span>
          <el-button type="primary" size="small" @click="handleOpenCreate">
            <el-icon><Plus /></el-icon>新增管理员
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="filter.username" placeholder="模糊搜索" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="filter.realName" placeholder="模糊搜索" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable style="width:120px">
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="banned" />
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
        <el-table-column label="头像" width="125">
          <template #default="{row}">
            <el-image style="width: 100px; height: 100px" :src="handleAvatar(row.avatarUrl)"/>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column label="角色" width="110">
          <template #default="{ row }">
            <el-tag :type="roleType(row.roleId)" size="small">{{ roleLabel(row.roleId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最后登录" width="160" />
        <el-table-column prop="lastIp" label="最后IP" width="130" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column prop="updatedAt" label="更新时间" width="160" />
        <el-table-column label="操作" min-width="350" fixed="right">
          <template #default="{ row }">
            <el-button :type="'success'" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button :type="'primary'" size="small" @click="handleAssignRoles(row)">分配角色</el-button>

            <el-popconfirm v-if="row.status === 'active'" title="确定要禁用该管理员吗？" @confirm="handleChangeStatus(row, 'banned')">
              <template #reference>
                <el-button size="small" type="warning">禁用</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm v-else title="确定要启用该管理员吗？" @confirm="handleChangeStatus(row, 'active')">
              <template #reference>
                <el-button size="small" type="success">启用</el-button>
              </template>
            </el-popconfirm>

            <el-button size="small" type="info" @click="handleResetPassword(row)">重置密码</el-button>

            <el-popconfirm title="确定要删除该管理员吗？删除后数据不可恢复。"
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

    <el-dialog v-model="createVisible" :title="isEdit ? '编辑管理员' : '新增管理员'" width="520px" :close-on-click-modal="false">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="createForm.username" placeholder="4~50个字符" maxlength="50"
            :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" placeholder="6~20个字符"
            maxlength="20" show-password />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="createForm.realName" placeholder="请输入真实姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" placeholder="请输入邮箱" maxlength="100" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="createForm.phone" placeholder="请输入手机号" maxlength="20" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="分配角色">
          <el-radio-group v-model="createForm.roleId" text-color="#fff" fill="#6c6cff">
            <el-radio-button v-for="role in allRoles" :key="role.id" :label="role.displayName" :value="role.id" />
          </el-radio-group>
          <!-- 多选方案（已废弃） -->
          <!-- <el-select v-model="createForm.roleIds" multiple placeholder="选择角色（可选）" style="width:100%">
            <el-option v-for="role in allRoles" :key="role.id" :label="role.displayName" :value="role.id" />
          </el-select> -->
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="480px">
      <div v-if="roleLoading" style="text-align:center;padding:40px">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      <template v-else>
        <el-form-item label="分配角色">
          <!-- <el-radio-group v-model="selectedRoleId" text-color="#fff" fill="#6c6cff">
            <el-radio-button v-for="role in allRoles" :key="role.id" :label="role.displayName" :value="role.id" />
          </el-radio-group> -->
          <!-- 采用下面的单选显示方案 -->
          <el-radio-group v-model="selectedRoleId">
            <el-radio v-for="role in allRoles" :key="role.id" :label="role.displayName" :value="role.id">
                {{ role.displayName || role.name }}
                <span style="color:#909399;font-size:12px;margin-left:8px">{{ role.description }}</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
         <!-- 多选方案（已废弃） -->
        <!-- <el-checkbox-group v-model="selectedRoleId">
          <div v-for="role in allRoles" :key="role.id" style="margin-bottom:12px">
            <el-checkbox :label="role.id" :value="role.id">
              {{ role.displayName || role.name }}
              <span style="color:#909399;font-size:12px;margin-left:8px">{{ role.description }}</span>
            </el-checkbox>
          </div>
        </el-checkbox-group> -->
        <el-empty v-if="allRoles.length === 0" description="暂无角色数据" />
      </template>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSubmitLoading" @click="confirmAssignRoles">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="重置密码" width="400px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="6~20个字符"
            maxlength="20" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="再次输入密码"
            maxlength="20" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordSubmitting" @click="confirmResetPassword">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="selfPasswordDialogVisible" title="修改密码" width="400px">
      <el-form ref="selfPasswordFormRef" :model="selfPasswordForm" :rules="selfPasswordRules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="selfPasswordForm.oldPassword" type="password" placeholder="请输入原密码"
            maxlength="20" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="selfPasswordForm.newPassword" type="password" placeholder="6~20个字符"
            maxlength="20" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="selfPasswordForm.confirmPassword" type="password" placeholder="再次输入密码"
            maxlength="20" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="selfPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="selfPasswordSubmitting" @click="confirmSelfPassword">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminUserPage, createAdminUser, updateAdminUser, deleteAdminUser,
  updateAdminUserStatus, assignAdminRoles, resetAdminPassword, updateAdminPassword
} from '../api/adminUser'
import { getRoleList } from '../api/role'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const filter = reactive({ username: '', realName: '', status: '' })

//异步查询管理员信息
async function fetchData() {
  loading.value = true
  try {
    const res = await getAdminUserPage({
      page: pagination.page,
      pageSize: pagination.pageSize,
      username: filter.username || undefined,
      realName: filter.realName || undefined,
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
  filter.realName = ''
  filter.status = ''
  handleSearch()
}

function handleAvatar(avatarUrl){
  return avatarUrl!==null&&avatarUrl!=''?avatarUrl:'https://seitem.oss-cn-beijing.aliyuncs.com/avatar/16/185f47ad-6198-4b73-90c5-e361f2238274.png';
}

//角色类型对应的tag类型，改变颜色用
function roleType(roleId) {
  switch(roleId){
    case 1: return "danger";
    case 4: return "success";
    case 5: return "warning";
  }
}

//不同角色对应展示的名字
function roleLabel(roleId) {
  switch(roleId){
    case 1: return "超级管理员";
    case 4: return "内容审核员";
    case 5: return "数据管理员";
  }
}

function statusType(status) {
  return status === 'active' ? 'success' : 'danger'
}

function statusLabel(status) {
  return status === 'active' ? '启用' : '禁用'
}

const createVisible = ref(false)
const createFormRef = ref(null)
const createSubmitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const createForm = reactive({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  roleId: 1
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

async function handleOpenCreate() {
  isEdit.value = false
  editId.value = null
  createForm.username = ''
  createForm.password = ''
  createForm.realName = ''
  createForm.email = ''
  createForm.phone = ''
  createForm.roleIds = []
  createFormRef.value?.resetFields()
  await loadRoles()
  createVisible.value = true
}

async function handleEdit(row) {
  isEdit.value = true
  editId.value = row.id
  createForm.username = row.username
  createForm.password = ''
  createForm.realName = row.realName || ''
  createForm.email = row.email || ''
  createForm.phone = row.phone || ''
  createForm.roleIds = []
  await loadRoles()
  createVisible.value = true
}

async function handleSubmit() {
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return
  createSubmitting.value = true
  try {
    const data = {
      username: createForm.username,
      realName: createForm.realName || undefined,
      email: createForm.email || undefined,
      phone: createForm.phone || undefined,
      roleId: createForm.roleId || undefined
    }
    if (!isEdit.value) {
      data.password = createForm.password
    }
    if (isEdit.value) {
      await updateAdminUser(editId.value, data)
      ElMessage.success('管理员信息更新成功')
    } else {
      await createAdminUser(data)
      ElMessage.success('管理员创建成功')
    }
    createVisible.value = false
    fetchData()
  } catch {
  } finally {
    createSubmitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteAdminUser(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    fetchData()
  } catch {
    ElMessage.error('删除失败，请重试')
  }
}

async function handleChangeStatus(row, status) {
  try {
    await updateAdminUserStatus(row.id, status)
    row.status = status
    ElMessage.success(status === 'active' ? '已启用' : '已禁用')
  } catch {
    ElMessage.error('操作失败，请重试')
  }
}

const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleSubmitLoading = ref(false)
const allRoles = ref([])
const selectedRoleId = ref(1)
let currentAdminUserId = null

async function loadRoles() {
  try {
    const res = await getRoleList()
    allRoles.value = res.data
  } catch {
    ElMessage.error('加载角色列表失败')
  }
}

async function handleAssignRoles(row) {
  currentAdminUserId = row.id
  roleDialogVisible.value = true
  roleLoading.value = true
  //默认选中超级管理员
  selectedRoleId.value = 1
  try {
    await loadRoles()
  } catch {
    roleDialogVisible.value = false
  } finally {
    roleLoading.value = false
  }
  // console.log(allRoles)
}

async function confirmAssignRoles() {
  roleSubmitLoading.value = true
  try {
    await assignAdminRoles(currentAdminUserId, { roleId: selectedRoleId.value })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    //重新查询管理员信息
    fetchData()
  } catch {
    ElMessage.error('角色分配失败')
  } finally {
    roleSubmitLoading.value = false
  }
}

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordSubmitting = ref(false)
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})
const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需要 6~20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}
let resetPasswordAdminId = null

async function handleResetPassword(row) {
  resetPasswordAdminId = row.id
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.resetFields()
  passwordDialogVisible.value = true
}

async function confirmResetPassword() {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  passwordSubmitting.value = true
  try {
    await resetAdminPassword(resetPasswordAdminId, passwordForm.newPassword)
    ElMessage.success('密码重置成功')
    passwordDialogVisible.value = false
  } catch {
  } finally {
    passwordSubmitting.value = false
  }
}

const selfPasswordDialogVisible = ref(false)
const selfPasswordFormRef = ref(null)
const selfPasswordSubmitting = ref(false)
const selfPasswordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const selfPasswordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需要 6~20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== selfPasswordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}
//组件挂载成功后马上拉取数据
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
