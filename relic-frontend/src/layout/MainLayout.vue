<template>
  <el-container class="admin-layout">
    <el-aside class="admin-aside" :width="asideWidth">
      <div class="admin-logo" :class="{ 'is-collapsed': collapsed }">
        <el-icon :size="22" color="var(--el-color-primary)"><Collection /></el-icon>
        <span v-show="!collapsed" class="admin-logo__text">RelicAdmin</span>
      </div>
      <el-scrollbar class="admin-menu-scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          :collapse-transition="false"
          router
          class="admin-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin-users">
            <el-icon><UserFilled /></el-icon>
            <template #title>管理员管理</template>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><Avatar /></el-icon>
            <template #title>角色管理</template>
          </el-menu-item>
          <el-menu-item index="/permissions">
            <el-icon><Lock /></el-icon>
            <template #title>权限管理</template>
          </el-menu-item>

          <el-sub-menu index="content">
            <template #title>
              <el-icon><Collection /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/museums">
              <el-icon><OfficeBuilding /></el-icon>
              <template #title>博物馆管理</template>
            </el-menu-item>
            <el-menu-item index="/dynasties">
              <el-icon><Clock /></el-icon>
              <template #title>朝代管理</template>
            </el-menu-item>
            <el-menu-item index="/artists">
              <el-icon><UserFilled /></el-icon>
              <template #title>艺术家管理</template>
            </el-menu-item>
            <el-menu-item index="/locations">
              <el-icon><Location /></el-icon>
              <template #title>地点管理</template>
            </el-menu-item>
            <el-menu-item index="/artifacts">
              <el-icon><CollectionTag /></el-icon>
              <template #title>文物管理</template>
            </el-menu-item>
            <el-menu-item index="/artifact-types">
              <el-icon><Collection /></el-icon>
              <template #title>文物类型管理</template>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="user-behavior">
            <template #title>
              <el-icon><Monitor /></el-icon>
              <span>用户行为记录</span>
            </template>
            <el-menu-item index="/user-behavior/favorites">
              <el-icon><Star /></el-icon>
              <template #title>用户收藏</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/likes">
              <el-icon><Pointer /></el-icon>
              <template #title>用户喜欢与点赞</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/posts">
              <el-icon><ChatLineSquare /></el-icon>
              <template #title>用户动态</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/comments">
              <el-icon><ChatDotSquare /></el-icon>
              <template #title>评论模块</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/follows">
              <el-icon><Connection /></el-icon>
              <template #title>用户关注</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/uploads">
              <el-icon><Upload /></el-icon>
              <template #title>用户上传</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/browse-history">
              <el-icon><Timer /></el-icon>
              <template #title>浏览历史</template>
            </el-menu-item>
            <el-menu-item index="/user-behavior/behavior-logs">
              <el-icon><Document /></el-icon>
              <template #title>行为日志</template>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="security">
            <template #title>
              <el-icon><Warning /></el-icon>
              <span>内容安全</span>
            </template>
            <el-menu-item index="/audit">
              <el-icon><Checked /></el-icon>
              <template #title>审核管理</template>
            </el-menu-item>
            <el-menu-item index="/penalty">
              <el-icon><WarningFilled /></el-icon>
              <template #title>违规处罚</template>
            </el-menu-item>
            <el-menu-item index="/appeal">
              <el-icon><Comment /></el-icon>
              <template #title>申诉管理</template>
            </el-menu-item>
            <el-menu-item index="/announcement">
              <el-icon><Bell /></el-icon>
              <template #title>公告管理</template>
            </el-menu-item>
            <el-menu-item index="/sensitive-word">
              <el-icon><Edit /></el-icon>
              <template #title>敏感词库</template>
            </el-menu-item>
            <el-menu-item index="/audit-strategy">
              <el-icon><Setting /></el-icon>
              <template #title>审核策略</template>
            </el-menu-item>
            <el-menu-item index="/violation-type">
              <el-icon><CircleClose /></el-icon>
              <template #title>违规类型</template>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Tools /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/crawl-task">
              <el-icon><Monitor /></el-icon>
              <template #title>爬取任务</template>
            </el-menu-item>
            <el-menu-item index="/backup">
              <el-icon><FolderOpened /></el-icon>
              <template #title>备份管理</template>
            </el-menu-item>
            <el-menu-item index="/backup-strategy">
              <el-icon><Coin /></el-icon>
              <template #title>备份策略</template>
            </el-menu-item>
            <el-menu-item index="/backup-restore-logs">
              <el-icon><Clock /></el-icon>
              <template #title>恢复日志</template>
            </el-menu-item>
            <el-menu-item index="/system-config">
              <el-icon><Setting /></el-icon>
              <template #title>系统配置</template>
            </el-menu-item>
            <el-menu-item index="/logs">
              <el-icon><Document /></el-icon>
              <template #title>日志管理</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="admin-main-wrap">
      <el-header class="admin-header" height="var(--admin-header-height)">
        <div class="admin-header__left">
          <el-button text class="collapse-btn" @click="collapsed = !collapsed">
            <el-icon :size="20">
              <Expand v-if="collapsed" />
              <Fold v-else />
            </el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.meta?.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="admin-header__right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="admin-user">
              <el-avatar :size="32" :src="headerAvatarSrc" class="admin-user__avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="admin-user__name">
                {{ auth.userInfo?.nickname || auth.userInfo?.username || '管理员' }}
              </span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component, route: childRoute }">
          <transition name="fade" mode="out-in">
            <div :key="childRoute.path" class="route-page">
              <el-page-header
                v-if="showRoutePageHeader(childRoute)"
                class="route-page__header"
                :title="childRoute.meta.title"
                :content="childRoute.meta.description"
              />
              <component :is="Component" />
            </div>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getCurrentAdmin } from '../api/adminUser'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const collapsed = ref(false)

const activeMenu = computed(() => route.path)
const asideWidth = computed(() =>
  collapsed.value ? 'var(--admin-sidebar-collapsed-width)' : 'var(--admin-sidebar-width)'
)

const breadcrumbs = computed(() =>
  route.matched.filter((r) => r.meta?.title && r.path !== '/')
)

/** 有头像 URL 时显示图片，否则 el-avatar 显示默认图标 */
const headerAvatarSrc = computed(() => {
  const url = auth.userInfo?.avatarUrl
  return url && String(url).trim() ? url : ''
})

async function refreshHeaderAvatar() {
  if (!auth.token) return
  try {
    const res = await getCurrentAdmin()
    const data = res.data
    if (!data) return
    auth.patchUser({
      id: data.id,
      username: data.username,
      nickname: data.realName || data.username,
      avatarUrl: data.avatarUrl || ''
    })
  } catch {
    /* 静默失败，沿用登录时缓存 */
  }
}

onMounted(() => {
  refreshHeaderAvatar()
})

/** 子页面自带 el-page-header / PageContainer 时不重复显示 */
function showRoutePageHeader(childRoute) {
  if (!childRoute.meta?.title) return false
  if (childRoute.meta.pageHeader === false) return false
  return true
}

function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
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
.admin-layout {
  height: 100vh;
  background: var(--el-bg-color-page);
}

.admin-aside {
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color);
  transition: width 0.2s;
  overflow: hidden;
}

.admin-logo {
  height: var(--admin-header-height);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  padding: 0 12px;
  flex-shrink: 0;
}

.admin-logo.is-collapsed {
  padding: 0;
}

.admin-logo__text {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  white-space: nowrap;
}

.admin-menu-scroll {
  flex: 1;
}

.admin-menu {
  border-right: none;
}

.admin-main-wrap {
  min-width: 0;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.admin-header__left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.collapse-btn {
  padding: 8px;
}

.admin-header__right {
  flex-shrink: 0;
}

.admin-user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--el-text-color-primary);
  outline: none;
}

.admin-user__name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.layout-main {
  padding: var(--admin-page-gap);
  overflow-y: auto;
  background: var(--el-bg-color-page);
}
</style>
