import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/UserListView.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'admin-users',
        name: 'AdminUserList',
        component: () => import('@/views/AdminUserListView.vue'),
        meta: { title: '管理员管理', icon: 'UserFilled' }
      },
      {
        path: 'roles',
        name: 'RoleList',
        component: () => import('@/views/RoleListView.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'permissions',
        name: 'PermissionList',
        component: () => import('@/views/PermissionListView.vue'),
        meta: { title: '权限管理', icon: 'Lock' }
      },
      {
        path: 'museums',
        name: 'MuseumList',
        component: () => import('@/views/MuseumListView.vue'),
        meta: { title: '博物馆管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'dynasties',
        name: 'DynastyList',
        component: () => import('@/views/DynastyListView.vue'),
        meta: { title: '朝代管理', icon: 'Clock' }
      },
      {
        path: 'artists',
        name: 'ArtistList',
        component: () => import('@/views/ArtistListView.vue'),
        meta: { title: '艺术家管理', icon: 'UserFilled' }
      },
      {
        path: 'locations',
        name: 'LocationList',
        component: () => import('@/views/LocationListView.vue'),
        meta: { title: '地点管理', icon: 'Location' }
      },
      {
        path: 'artifacts',
        name: 'ArtifactList',
        component: () => import('@/views/ArtifactListView.vue'),
        meta: { title: '文物管理', icon: 'CollectionTag' }
      },
      {
        path: 'artifact-images/:id',
        name: 'ArtifactImageList',
        component: () => import('@/views/ArtifactImageView.vue'),
        meta: { title: '文物图片管理', icon: 'Picture' }
      },
      {
        path: 'artifact-types',
        name: 'ArtifactTypeList',
        component: () => import('@/views/ArtifactTypeListView.vue'),
        meta: { title: '文物类型管理', icon: 'Collection' }
      },
      {
        path: 'favorites',
        name: 'UserFavoriteList',
        component: () => import('@/views/UserFavoriteListView.vue'),
        meta: { title: '用户收藏(旧)', icon: 'Star' }
      },
      {
        path: 'user-behavior/favorites',
        name: 'UserFavoriteAdmin',
        component: () => import('@/views/UserFavoriteAdminView.vue'),
        meta: { title: '用户收藏', icon: 'Star' }
      },
      {
        path: 'user-behavior/likes',
        name: 'UserLikeAdmin',
        component: () => import('@/views/UserLikeAdminView.vue'),
        meta: { title: '用户喜欢与点赞', icon: 'Pointer' }
      },
      {
        path: 'user-behavior/posts',
        name: 'UserPostAdmin',
        component: () => import('@/views/UserPostAdminView.vue'),
        meta: { title: '用户动态', icon: 'ChatLineSquare' }
      },
      {
        path: 'user-behavior/comments',
        name: 'UserCommentAdmin',
        component: () => import('@/views/UserCommentAdminView.vue'),
        meta: { title: '评论模块', icon: 'ChatDotSquare' }
      },
      {
        path: 'user-behavior/follows',
        name: 'UserFollowAdmin',
        component: () => import('@/views/UserFollowAdminView.vue'),
        meta: { title: '用户关注', icon: 'Connection' }
      },
      {
        path: 'user-behavior/uploads',
        name: 'UserUploadAdmin',
        component: () => import('@/views/UserUploadAdminView.vue'),
        meta: { title: '用户上传', icon: 'Upload' }
      },
      {
        path: 'user-behavior/browse-history',
        name: 'UserBrowseHistoryAdmin',
        component: () => import('@/views/UserBrowseHistoryAdminView.vue'),
        meta: { title: '浏览历史', icon: 'Timer' }
      },
      {
        path: 'user-behavior/behavior-logs',
        name: 'UserBehaviorLogAdmin',
        component: () => import('@/views/UserBehaviorLogAdminView.vue'),
        meta: { title: '行为日志', icon: 'Document' }
      },
      {
        path: 'audit',
        name: 'AuditAdmin',
        component: () => import('@/views/AuditAdminView.vue'),
        meta: { title: '审核管理', icon: 'Checked' }
      },
      {
        path: 'penalty',
        name: 'PenaltyAdmin',
        component: () => import('@/views/PenaltyAdminView.vue'),
        meta: { title: '违规处罚', icon: 'WarningFilled' }
      },
      {
        path: 'appeal',
        name: 'AppealAdmin',
        component: () => import('@/views/AppealAdminView.vue'),
        meta: { title: '申诉管理', icon: 'Comment' }
      },
      {
        path: 'announcement',
        name: 'AnnouncementAdmin',
        component: () => import('@/views/AnnouncementAdminView.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      },
      {
        path: 'sensitive-word',
        name: 'SensitiveWordAdmin',
        component: () => import('@/views/SensitiveWordAdminView.vue'),
        meta: { title: '敏感词库', icon: 'Edit' }
      },
      {
        path: 'audit-strategy',
        name: 'AuditStrategyAdmin',
        component: () => import('@/views/AuditStrategyAdminView.vue'),
        meta: { title: '审核策略', icon: 'Setting' }
      },
      {
        path: 'audit-stats',
        name: 'AuditStats',
        component: () => import('@/views/AuditStatsView.vue'),
        meta: { title: '审核统计', icon: 'DataAnalysis' }
      },
      {
        path: 'violation-type',
        name: 'ViolationTypeAdmin',
        component: () => import('@/views/ViolationTypeAdminView.vue'),
        meta: { title: '违规类型', icon: 'Warning' }
      },
      {
        path: 'backup',
        name: 'BackupAdmin',
        component: () => import('@/views/BackupAdminView.vue'),
        meta: { title: '备份管理', icon: 'FolderOpened' }
      },
      {
        path: 'backup-strategy',
        name: 'BackupStrategy',
        component: () => import('@/views/BackupStrategyAdminView.vue'),
        meta: { title: '备份策略', icon: 'Coin' }
      },
      {
        path: 'backup-restore-logs',
        name: 'RestoreRecordAdmin',
        component: () => import('@/views/RestoreRecordAdminView.vue'),
        meta: { title: '恢复日志', icon: 'Clock' }
      },
      {
        path: 'crawl-task',
        name: 'CrawlTaskAdmin',
        component: () => import('@/views/CrawlTaskAdminView.vue'),
        meta: { title: '爬取任务', icon: 'Monitor' }
      },
      {
        path: 'system-config',
        name: 'SystemConfigAdmin',
        component: () => import('@/views/SystemConfigAdminView.vue'),
        meta: { title: '系统配置', icon: 'Setting' }
      },
      {
        path: 'logs',
        name: 'LogAdmin',
        component: () => import('@/views/LogAdminView.vue'),
        meta: { title: '日志管理', icon: 'Document' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - RelicAdmin` : 'RelicAdmin'
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
