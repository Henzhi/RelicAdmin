<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <h2>RelicAdmin 文物管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><UserFilled /></el-icon>
            {{ auth.userInfo?.nickname || auth.userInfo?.username || '管理员' }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><Avatar /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/permissions">
            <el-icon><Key /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      auth.logout()
      router.push('/login')
    })
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-header {
  background-color: #409eff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}
.layout-header h2 {
  color: #fff;
  margin: 0;
  font-size: 18px;
}
.header-right {
  color: #fff;
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
}
.layout-aside {
  background-color: #304156;
  overflow-y: auto;
}
.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>