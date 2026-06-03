<template>
  <div class="login-page">
    <div class="login-page__bg" />
    <el-card class="login-card" shadow="hover">
      <div class="login-card__brand">
        <el-icon :size="40" color="var(--el-color-primary)"><Collection /></el-icon>
        <h1>RelicAdmin</h1>
        <p>文物管理系统 · 管理后台</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-submit" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.login(form)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow: hidden;
}

.login-page__bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #ecf5ff 0%, #f0f9eb 50%, #fdf6ec 100%);
  z-index: 0;
}

.login-page__bg::after {
  content: '';
  position: absolute;
  width: 480px;
  height: 480px;
  right: -120px;
  top: -120px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--el-color-primary-light-7) 0%, transparent 70%);
  opacity: 0.6;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  border-radius: var(--el-border-radius-base);
}

.login-card__brand {
  text-align: center;
  margin-bottom: 28px;
}

.login-card__brand h1 {
  margin: 12px 0 6px;
  font-size: 22px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.login-card__brand p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.login-submit {
  width: 100%;
  margin-top: 8px;
}
</style>
