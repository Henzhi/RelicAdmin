<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="40" color="#409eff"><User /></el-icon>
            <div>
              <div class="stat-num">{{ stats.totalUsers }}</div>
              <div class="stat-txt">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="40" color="#67c23a"><Collection /></el-icon>
            <div>
              <div class="stat-num">{{ stats.totalArtifacts }}</div>
              <div class="stat-txt">文物总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="40" color="#e6a23c"><OfficeBuilding /></el-icon>
            <div>
              <div class="stat-num">{{ stats.totalMuseums }}</div>
              <div class="stat-txt">博物馆数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="40" color="#f56c6c"><ChatDotRound /></el-icon>
            <div>
              <div class="stat-num">{{ stats.totalComments }}</div>
              <div class="stat-txt">评论总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px">
      <template #header>
        <span>欢迎使用 RelicAdmin 文物管理系统</span>
      </template>
      <p>本系统提供文物数据管理、用户管理、角色权限管理、审核管理等功能。请使用左侧菜单进行各项操作。</p>
      <el-descriptions :column="2" border style="margin-top:16px">
        <el-descriptions-item label="系统版本">v2.0</el-descriptions-item>
        <el-descriptions-item label="后端框架">Spring Boot 3.2 + MyBatis</el-descriptions-item>
        <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
        <el-descriptions-item label="数据存储">MySQL + Redis</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const stats = ref({
  totalUsers: 2560,
  totalArtifacts: 5000,
  totalMuseums: 50,
  totalComments: 12000
})
</script>

<style scoped>
.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-num {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-txt {
  font-size: 14px;
  color: #909399;
}
</style>