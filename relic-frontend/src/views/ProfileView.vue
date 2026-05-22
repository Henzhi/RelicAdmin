<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="avatar-section">
            <el-avatar :size="100" :src="profile.avatarUrl" class="profile-avatar">
              <el-icon :size="48"><UserFilled /></el-icon>
            </el-avatar>
            <h3>{{ profile.realName || profile.username }}</h3>
            <el-tag :type="profile.status === 'active' ? 'success' : 'danger'" size="small">
              {{ profile.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </div>
          <el-divider />
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
            <el-descriptions-item label="角色">管理员</el-descriptions-item>
            <el-descriptions-item label="最后登录">
              {{ profile.lastLogin || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="登录IP">
              {{ profile.lastIp || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ profile.createdAt || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button v-if="!editing" type="primary" size="small" @click="startEdit">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            :disabled="!editing"
          >
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="50" />
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入电子邮箱" maxlength="100" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="20" />
            </el-form-item>
            <el-form-item label="头像上传" prop="avatarUrl">
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
                <el-avatar v-if="form.avatarUrl" :size="80" :src="form.avatarUrl" class="avatar-preview-img" />
                <el-icon v-else class="avatar-upload-icon" :size="48"><Plus /></el-icon>
                <div class="upload-tip" v-if="!editing">编辑模式下可点击更换</div>
              </el-upload>
              <div v-if="form.avatarUrl" class="avatar-url-text">{{ form.avatarUrl }}</div>
            </el-form-item>

            <el-form-item v-if="editing">
              <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" style="margin-top:20px">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（至少6位）"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="changingPwd">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentAdmin, updateCurrentProfile, updateCurrentPassword } from '@/api/adminUser'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const profile = ref({})
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

function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return false
  }
  return true
}

function handleAvatarSuccess(response) {
  if (response.code === 200) {
    form.avatarUrl = response.data
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
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function loadProfile() {
  try {
    const res = await getCurrentAdmin()
    profile.value = res.data
    form.realName = res.data.realName || ''
    form.email = res.data.email || ''
    form.phone = res.data.phone || ''
    form.avatarUrl = res.data.avatarUrl || ''
    auth.userInfo = {
      id: res.data.id,
      username: res.data.username,
      nickname: res.data.realName || res.data.username
    }
    localStorage.setItem('admin_user', JSON.stringify(auth.userInfo))
  } catch (e) {
    ElMessage.error('加载个人信息失败')
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
  } catch (e) {
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
  } catch (e) {
    ElMessage.error('密码修改失败，请检查原密码是否正确')
  } finally {
    changingPwd.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-page {
  max-width: 960px;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.profile-avatar {
  border: 3px solid #409eff;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.avatar-upload {
  cursor: pointer;
}
.avatar-preview-img:hover {
  opacity: 0.8;
}
.avatar-upload-icon {
  color: #8c939d;
  width: 80px;
  height: 80px;
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}
.avatar-upload-icon:hover {
  border-color: #409eff;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: center;
}
.avatar-url-text {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
  word-break: break-all;
}
</style>