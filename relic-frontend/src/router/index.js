import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/DashboardView.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/UserListView.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('../views/RoleListView.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'permissions',
        name: 'Permissions',
        component: () => import('../views/PermissionListView.vue'),
        meta: { title: '权限管理' }
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