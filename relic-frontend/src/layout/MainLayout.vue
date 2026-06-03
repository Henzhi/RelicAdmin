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
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="changeLanguage">切换语言</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
            <!-- 语言对话框 -->
        <LanguageDialog ref="langDialogRef" />
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
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin-users">
            <el-icon><UserFilled /></el-icon>
            <span>管理员管理</span>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><Avatar /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/permissions">
            <el-icon><Lock /></el-icon>
            <span>权限管理</span>
          </el-menu-item>

          <el-sub-menu index="content">
            <template #title>
              <el-icon><Collection /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/museums">
              <el-icon><OfficeBuilding /></el-icon>
              <span>博物馆管理</span>
            </el-menu-item>
            <el-menu-item index="/dynasties">
              <el-icon><Clock /></el-icon>
              <span>朝代管理</span>
            </el-menu-item>
            <el-menu-item index="/artists">
              <el-icon><UserFilled /></el-icon>
              <span>艺术家管理</span>
            </el-menu-item>
            <el-menu-item index="/locations">
              <el-icon><Location /></el-icon>
              <span>地点管理</span>
            </el-menu-item>
            <el-menu-item index="/artifacts">
              <el-icon><CollectionTag /></el-icon>
              <span>文物管理</span>
            </el-menu-item>
            <el-menu-item index="/artifact-types">
              <el-icon><Collection /></el-icon>
              <span>文物类型管理</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="user-behavior">
            <template #title>
              <el-icon><Monitor /></el-icon>
              <span>用户行为记录</span>
            </template>
            <el-menu-item index="/user-behavior/favorites">
              <el-icon><Star /></el-icon>
              <span>用户收藏</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/likes">
              <el-icon><Pointer /></el-icon>
              <span>用户喜欢与点赞</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/posts">
              <el-icon><ChatLineSquare /></el-icon>
              <span>用户动态</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/comments">
              <el-icon><ChatDotSquare /></el-icon>
              <span>评论模块</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/follows">
              <el-icon><Connection /></el-icon>
              <span>用户关注</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/uploads">
              <el-icon><Upload /></el-icon>
              <span>用户上传</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/browse-history">
              <el-icon><Timer /></el-icon>
              <span>浏览历史</span>
            </el-menu-item>
            <el-menu-item index="/user-behavior/behavior-logs">
              <el-icon><Document /></el-icon>
              <span>行为日志</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="security">
            <template #title>
              <el-icon><Warning /></el-icon>
              <span>内容安全</span>
            </template>
            <el-menu-item index="/audit">
              <el-icon><Checked /></el-icon>
              <span>审核管理</span>
            </el-menu-item>
            <el-menu-item index="/penalty">
              <el-icon><WarningFilled /></el-icon>
              <span>违规处罚</span>
            </el-menu-item>
            <el-menu-item index="/appeal">
              <el-icon><Comment /></el-icon>
              <span>申诉管理</span>
            </el-menu-item>
            <el-menu-item index="/announcement">
              <el-icon><Bell /></el-icon>
              <span>公告管理</span>
            </el-menu-item>
            <el-menu-item index="/sensitive-word">
              <el-icon><Edit /></el-icon>
              <span>敏感词库</span>
            </el-menu-item>
            <el-menu-item index="/audit-strategy">
              <el-icon><Setting /></el-icon>
              <span>审核策略</span>
            </el-menu-item>
            <el-menu-item index="/violation-type">
              <el-icon><CircleClose /></el-icon>
              <span>违规类型</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Tools /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/crawl-task">
              <el-icon><Monitor /></el-icon>
              <span>爬取任务</span>
            </el-menu-item>
            <el-menu-item index="/backup">
              <el-icon><FolderOpened /></el-icon>
              <span>备份管理</span>
            </el-menu-item>
            <el-menu-item index="/backup-strategy">
              <el-icon><Coin /></el-icon>
              <span>备份策略</span>
            </el-menu-item>
            <el-menu-item index="/backup-restore-logs">
              <el-icon><Clock /></el-icon>
              <span>恢复日志</span>
            </el-menu-item>
            <el-menu-item index="/system-config">
              <el-icon><Setting /></el-icon>
              <span>系统配置</span>
            </el-menu-item>
            <el-menu-item index="/logs">
              <el-icon><Document /></el-icon>
              <span>日志管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed,ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useLocaleStore } from '../stores/language'
import { ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import LanguageDialog from '../views/LanguageDialog.vue'


const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)

const localeStore = useLocaleStore()  // ← 使用同一个 store 实例
const langDialogRef = ref(null)
const { locale } = useI18n()


// function handleCommand(command) {
//   if (command === 'profile') {
//     router.push('/profile')
//   } else if(command === 'changeLanguage'){

//   }else if (command === 'logout') {
//     ElMessageBox.confirm('确定要退出登录吗？', '提示', {
//       confirmButtonText: '确定',
//       cancelButtonText: '取消',
//       type: 'warning'
//     }).then(() => {
//       auth.logout()
//       router.push('/login')
//     })
//   }
// }
function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'changeLanguage':
      langDialogRef.value?.open()
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        auth.logout()
        router.push('/login')
      }).catch(() => {})
      break
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
