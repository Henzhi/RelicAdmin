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