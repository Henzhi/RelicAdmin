<template>
  <div v-loading="pageLoading" class="profile-page">
    <el-page-header class="profile-header" title="个人中心" content="查看与维护您的账号信息" />

    <el-row :gutter="16" class="profile-body">
      <el-col :xs="24" :sm="24" :md="8" :lg="7">
        <el-card shadow="never" class="profile-summary">
          <div class="summary-banner" />
          <div class="summary-user">
            <el-avatar :size="88" :src="profile.avatarUrl" class="summary-avatar">
              <el-icon :size="40"><UserFilled /></el-icon>
            </el-avatar>
            <h3 class="summary-name">{{ profile.realName || profile.username || '—' }}</h3>
            <p class="summary-username">@{{ profile.username || '—' }}</p>
            <div class="summary-tags">
              <el-tag :type="profile.status === 'active' ? 'success' : 'danger'" size="small" effect="light">
                {{ profile.status === 'active' ? '账号正常' : '账号禁用' }}
              </el-tag>
              <el-tag
                v-for="(tag, index) in profileRoleTags"
                :key="index"
                :type="tag.type"
                size="small"
                effect="light"
              >
                {{ tag.label }}
              </el-tag>
              <el-tag v-if="!profileRoleTags.length" type="info" size="small" effect="plain">
                未分配角色
              </el-tag>
            </div>
          </div>

          <el-divider />

          <ul class="summary-meta">
            <li>
              <el-icon><Clock /></el-icon>
              <div>
                <span class="meta-label">最后登录</span>
                <span class="meta-value">{{ formatDateTime(profile.lastLogin) }}</span>
              </div>
            </li>
            <li>
              <el-icon><Monitor /></el-icon>
              <div>
                <span class="meta-label">登录 IP</span>
                <span class="meta-value">{{ profile.lastIp || '—' }}</span>
              </div>
            </li>
            <li>
              <el-icon><Calendar /></el-icon>
              <div>
                <span class="meta-label">注册时间</span>
                <span class="meta-value">{{ formatDateTime(profile.createdAt) }}</span>
              </div>
            </li>
          </ul>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="16" :lg="17">
        <el-card shadow="never" class="profile-panel">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <el-tab-pane label="基本信息" name="info">
              <div class="panel-toolbar">
                <span class="panel-desc">更新您的联系资料与头像</span>
                <el-button v-if="!editing" type="primary" @click="startEdit">
                  <el-icon><Edit /></el-icon>
                  编辑资料
                </el-button>
              </div>

              <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="88px"
                label-position="right"
                class="profile-form"
                :disabled="!editing"
              >
                <div class="avatar-editor">
                  <el-upload
                    class="avatar-upload"
                    action="/admin/admin-user/current/avatar"
                    :headers="uploadHeaders"
                    :disabled="!editing"
                    accept=".jpg,.jpeg,.png,.gif,.webp"
                    :limit="1"
                    :show-file-list="false"
                    :before-upload="beforeAvatarUpload"
                    :on-success="handleAvatarSuccess"
                    :on-error="handleAvatarError"
                  >
                    <el-avatar v-if="form.avatarUrl" :size="96" :src="form.avatarUrl" class="avatar-preview" />
                    <div v-else class="avatar-placeholder">
                      <el-icon :size="32"><Plus /></el-icon>
                    </div>
                  </el-upload>
                  <div class="avatar-meta">
                    <p class="avatar-title">头像</p>
                    <p class="avatar-tip">
                      {{ editing ? '点击头像区域上传，支持 JPG / PNG / GIF / WebP，不超过 10MB' : '进入编辑模式后可更换头像' }}
                    </p>
                  </div>
                </div>

                <el-row :gutter="16">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="真实姓名" prop="realName">
                      <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="50" clearable />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="手机号码" prop="phone">
                      <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="20" clearable />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="电子邮箱" prop="email">
                      <el-input v-model="form.email" placeholder="请输入电子邮箱" maxlength="100" clearable />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item v-if="editing" class="form-actions">
                  <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
                  <el-button @click="cancelEdit">取消</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="安全设置" name="security">
              <el-alert
                title="修改密码后需要重新登录，请妥善保管新密码。"
                type="info"
                show-icon
                :closable="false"
                class="security-alert"
              />
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="88px"
                class="profile-form profile-form--narrow"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入原密码"
                    show-password
                    @input="onOldPasswordInput"
                    autocomplete="off"
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="至少 6 位字符"
                    show-password
                    autocomplete="new-password"
                    @input="onNewPasswordInput"
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                    autocomplete="new-password"
                  />
                </el-form-item>
                <el-form-item class="form-actions">
                  <el-button type="primary" :loading="changingPwd" @click="handleChangePassword">
                    修改密码
                  </el-button>
                  <el-button @click="resetPasswordForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentAdmin, getAdminUserPage, updateCurrentProfile, updateCurrentPassword } from '@/api/adminUser'
import { getRoleList } from '@/api/role'
import { buildProfileRoleTags } from '@/utils/adminRole'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const profile = ref({})
const allRoles = ref([])
const pageLoading = ref(false)
const activeTab = ref('info')
const editing = ref(false)
const saving = ref(false)
const changingPwd = ref(false)
const formRef = ref(null)
const passwordFormRef = ref(null)

const form = reactive({
  realName: '',
  email: '',
  phone: '',
  avatarUrl: ''
})

const uploadHeaders = { token: localStorage.getItem('admin_token') || '' }

const profileRoleTags = computed(() =>
  buildProfileRoleTags(profile.value.roleId, allRoles.value)
)

function formatDateTime(value) {
  if (!value) return '—'
  return String(value).replace('T', ' ')
}

async function loadRoles() {
  try {
    const res = await getRoleList()
    allRoles.value = res.data || []
  } catch {
    allRoles.value = []
  }
}

async function loadCurrentRoleId(adminId, username) {
  try {
    const res = await getAdminUserPage({
      page: 1,
      pageSize: 10,
      username: username || undefined
    })
    const match = (res.data?.records || []).find((row) => row.id === adminId)
    profile.value.roleId = match?.roleId ?? null
  } catch {
    profile.value.roleId = null
  }
}

function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

function handleAvatarSuccess(response) {
  if (response.code === 200) {
    form.avatarUrl = response.data
    profile.value.avatarUrl = response.data
    auth.patchUser({ avatarUrl: response.data })
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

function handleAvatarError() {
  ElMessage.error('头像上传失败')
}

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }]
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const SAME_PASSWORD_MSG = '新密码不能与原密码相同'

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateNewPasswordDistinct = (_rule, value, callback) => {
  if (value && passwordForm.oldPassword && value === passwordForm.oldPassword) {
    callback(new Error(SAME_PASSWORD_MSG))
  } else {
    callback()
  }
}

const validateOldPasswordDistinct = (_rule, value, callback) => {
  if (value && passwordForm.newPassword && value === passwordForm.newPassword) {
    callback(new Error(SAME_PASSWORD_MSG))
  } else {
    callback()
  }
}

function onOldPasswordInput() {
  if (passwordForm.newPassword) {
    passwordFormRef.value?.validateField('newPassword')
  }
}

function onNewPasswordInput() {
  if (passwordForm.oldPassword) {
    passwordFormRef.value?.validateField('oldPassword')
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { validator: validateOldPasswordDistinct, trigger: ['blur', 'change'] }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
    { validator: validateNewPasswordDistinct, trigger: ['blur', 'change'] }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function loadProfile() {
  pageLoading.value = true
  try {
    await loadRoles()
    const res = await getCurrentAdmin()
    profile.value = res.data
    await loadCurrentRoleId(res.data.id, res.data.username)
    form.realName = res.data.realName || ''
    form.email = res.data.email || ''
    form.phone = res.data.phone || ''
    form.avatarUrl = res.data.avatarUrl || ''
    auth.patchUser({
      id: res.data.id,
      username: res.data.username,
      nickname: res.data.realName || res.data.username,
      avatarUrl: res.data.avatarUrl || ''
    })
  } catch {
    ElMessage.error('加载个人信息失败')
  } finally {
    pageLoading.value = false
  }
}

function startEdit() {
  editing.value = true
}

function cancelEdit() {
  form.realName = profile.value.realName || ''
  form.email = profile.value.email || ''
  form.phone = profile.value.phone || ''
  form.avatarUrl = profile.value.avatarUrl || ''
  editing.value = false
  formRef.value?.clearValidate()
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateCurrentProfile({
      realName: form.realName,
      email: form.email,
      phone: form.phone,
      avatarUrl: form.avatarUrl
    })
    ElMessage.success('个人信息已更新')
    editing.value = false
    await loadProfile()
  } catch {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

function resetPasswordForm() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

async function handleChangePassword() {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await updateCurrentPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    resetPasswordForm()
    setTimeout(() => {
      auth.logout()
      router.push('/login')
    }, 1500)
  } catch {
    ElMessage.error('密码修改失败，请检查原密码是否正确')
  } finally {
    changingPwd.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>
